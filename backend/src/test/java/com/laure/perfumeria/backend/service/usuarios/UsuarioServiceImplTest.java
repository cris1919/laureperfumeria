package com.laure.perfumeria.backend.service.usuarios;

import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.model.usuarios.dto.EstadoUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioResponse;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UsuarioServiceImpl usuarioService;

    @Test
    void cambiarEstadoDebeActualizarUsuario() {
        Usuario usuario = new Usuario(1L, "Admin", "Sistema", "admin@test.com",
                "hash", "ADMIN", "ACTIVO", null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse response = usuarioService.cambiarEstado(1L, new EstadoUsuarioRequest("INACTIVO"));

        assertEquals("INACTIVO", response.getEstado());
    }
}
