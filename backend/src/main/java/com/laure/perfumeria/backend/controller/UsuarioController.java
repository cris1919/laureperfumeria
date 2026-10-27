package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.usuarios.dto.CambiarPasswordUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.EstadoUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioResponse;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioUpdateRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioRegistroInicialRequest;
import com.laure.perfumeria.backend.service.usuarios.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro-inicial")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UsuarioResponse> registrarAdministradorInicial(
            @Valid @RequestBody UsuarioRegistroInicialRequest request) {
        return ResponseEntity.status(201).body(usuarioService.registrarAdministradorInicial(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> consultarPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.consultarPerfil(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarDatos(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarDatos(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordUsuarioRequest request) {
        usuarioService.cambiarPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request));
    }
}
