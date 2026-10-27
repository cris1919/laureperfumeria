package com.laure.perfumeria.backend.service.auth;

import com.laure.perfumeria.backend.model.auth.dto.LoginRequest;
import com.laure.perfumeria.backend.model.auth.dto.LoginResponse;
import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import com.laure.perfumeria.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final String ACTIVO = "ACTIVO";
    private static final String ADMIN = "ADMIN";
    private static final String CLIENTE = "CLIENTE";

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse iniciarSesion(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo()).orElse(null);
        if (usuario != null && passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())
                && ACTIVO.equalsIgnoreCase(usuario.getEstado())) {
            return new LoginResponse(
                    "Inicio de sesión exitoso",
                    jwtService.generarToken(usuario),
                    "Bearer",
                    usuario.getIdUsuario(),
                    usuario.getNombres(),
                    usuario.getApellidos(),
                    usuario.getCorreo(),
                    ADMIN);
        }

        Cliente cliente = clienteRepository.findByCorreoIgnoreCase(request.getCorreo()).orElse(null);
        if (cliente != null && passwordEncoder.matches(request.getPassword(), cliente.getPasswordHash())
                && ACTIVO.equalsIgnoreCase(cliente.getEstado())) {
            return new LoginResponse(
                    "Inicio de sesión exitoso",
                    jwtService.generarToken(cliente),
                    "Bearer",
                    cliente.getIdCliente(),
                    cliente.getNombres(),
                    cliente.getApellidos(),
                    cliente.getCorreo(),
                    CLIENTE);
        }

        throw new IllegalArgumentException("Correo o contraseña incorrectos");
    }
}
