package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.ventas.dto.RegistrarVentaRequest;
import com.laure.perfumeria.backend.model.ventas.dto.VentaResponse;
import com.laure.perfumeria.backend.service.ventas.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody RegistrarVentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VentaResponse>> buscarPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ventaService.buscarPorPeriodo(desde, hasta));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<VentaResponse> buscarPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(ventaService.buscarPorPedido(idPedido));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calcularTotalVentas() {
        return ResponseEntity.ok(ventaService.calcularTotalVentas());
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anular(id));
    }

}
