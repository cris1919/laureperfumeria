package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.pagos.dto.PagoContraEntregaRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoResponse;
import com.laure.perfumeria.backend.service.pagos.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> registrar(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrar(request));
    }

    @PostMapping("/contra-entrega")
    public ResponseEntity<PagoResponse> registrarContraEntrega(
            @Valid @RequestBody PagoContraEntregaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagoService.registrarContraEntrega(request));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<PagoResponse>> listarPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pagoService.listarPorPedido(idPedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PagoResponse>> listarPendientes() {
        return ResponseEntity.ok(pagoService.listarPendientes());
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.confirmar(id));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagoResponse> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.rechazar(id));
    }

}
