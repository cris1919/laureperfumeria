package com.laure.perfumeria.backend.service.auth;

import com.laure.perfumeria.backend.model.auth.dto.LoginRequest;
import com.laure.perfumeria.backend.model.auth.dto.LoginResponse;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import com.laure.perfumeria.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private AuthServiceImpl authService;

    @Test
    void loginDebeGenerarTokenParaAdministradorActivo() {
        Usuario usuario = new Usuario(1L, "Admin", "Sistema", "admin@test.com",
                "hash", "ADMIN", "ACTIVO", null);
        when(usuarioRepository.findByCorreoIgnoreCase("admin@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secret123", "hash")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("jwt-token");

        LoginResponse response = authService.iniciarSesion(new LoginRequest("admin@test.com", "secret123"));

        assertEquals("jwt-token", response.getToken());
        assertEquals("ADMIN", response.getRol());
    }
}
