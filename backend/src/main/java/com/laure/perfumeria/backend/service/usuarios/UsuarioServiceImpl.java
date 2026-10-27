package com.laure.perfumeria.backend.service.usuarios;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.model.usuarios.dto.CambiarPasswordUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.EstadoUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioResponse;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioUpdateRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioRegistroInicialRequest;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";
    private static final String ADMIN = "ADMIN";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrarAdministradorInicial(UsuarioRegistroInicialRequest request) {
        if (usuarioRepository.existsByRolIgnoreCase(ADMIN)) {
            throw new IllegalStateException("Ya existe un administrador registrado");
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getCorreo().trim().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(ADMIN);
        usuario.setEstado(ACTIVO);
        return convertir(usuarioRepository.save(usuario));
    }
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse consultarPerfil(Long id) {
        return convertir(obtenerUsuario(id));
    }

    @Override
    public UsuarioResponse actualizarDatos(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerUsuario(id);
        usuarioRepository.findByCorreoIgnoreCase(request.getCorreo()).ifPresent(otro -> {
            if (!otro.getIdUsuario().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro usuario con ese correo");
            }
        });

        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getCorreo());
        usuario.setRol(request.getRol());
        return convertir(usuarioRepository.save(usuario));
    }

    @Override
    public void cambiarPassword(Long id, CambiarPasswordUsuarioRequest request) {
        Usuario usuario = obtenerUsuario(id);
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::convertir).toList();
    }

    @Override
    public UsuarioResponse cambiarEstado(Long id, EstadoUsuarioRequest request) {
        Usuario usuario = obtenerUsuario(id);
        String estado = request.getEstado().trim().toUpperCase();
        if (!ACTIVO.equals(estado) && !INACTIVO.equals(estado)) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        usuario.setEstado(estado);
        return convertir(usuarioRepository.save(usuario));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID " + id));
    }

    private UsuarioResponse convertir(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getFechaRegistro());
    }
}
