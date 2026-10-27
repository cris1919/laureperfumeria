package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByRolIgnoreCase(String rol);

    Optional<Usuario> findFirstByRolIgnoreCaseAndEstadoIgnoreCase(String rol, String estado);
}
