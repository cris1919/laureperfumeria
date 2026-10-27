package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.clientes.dto.CambiarPasswordRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteRegistroRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteResponse;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteUpdateRequest;
import com.laure.perfumeria.backend.model.clientes.dto.EstadoClienteRequest;
import com.laure.perfumeria.backend.service.clientes.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody ClienteRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.registrar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultarPerfil(@PathVariable Long id,
                                                           Authentication authentication) {
        return ResponseEntity.ok(clienteService.consultarPerfil(id, authentication.getName(),
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarDatos(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(clienteService.actualizarDatos(id, request, authentication.getName(),
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication authentication) {
        clienteService.cambiarPassword(id, request, authentication.getName(),
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoClienteRequest request) {
        return ResponseEntity.ok(clienteService.cambiarEstado(id, request));
    }
}
