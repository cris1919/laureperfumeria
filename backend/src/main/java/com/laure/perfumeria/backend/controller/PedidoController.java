package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.pedidos.dto.EstadoPedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoResponse;
import com.laure.perfumeria.backend.service.pedidos.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> registrar(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.registrar(request));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoResponse>> listarPorCliente(@PathVariable Long idCliente,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(idCliente, authentication.getName(),
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))));
    }

    @GetMapping("/cliente/{idCliente}/historial")
    public ResponseEntity<List<PedidoResponse>> listarHistorial(@PathVariable Long idCliente,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(pedidoService.listarHistorialCliente(idCliente, authentication.getName(),
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponse>> filtrar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(pedidoService.filtrar(estado, desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<String> consultarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.consultarEstado(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }
}
