package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.ventas.dto.DetalleVentaResponse;
import com.laure.perfumeria.backend.service.ventas.DetalleVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ventas/{idVenta}/detalles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;

    @GetMapping
    public ResponseEntity<List<DetalleVentaResponse>> listarPorVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(detalleVentaService.listarPorVenta(idVenta));
    }
}
