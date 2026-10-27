package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioRequest;
import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioResponse;
import com.laure.perfumeria.backend.service.movimientosInventario.MovimientoInventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoInventarioResponse> registrarEntrada(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrarEntrada(request));
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoInventarioResponse> registrarSalida(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrarSalida(request));
    }

    @PostMapping("/ajuste-positivo")
    public ResponseEntity<MovimientoInventarioResponse> registrarAjustePositivo(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrarAjustePositivo(request));
    }

    @PostMapping("/ajuste-negativo")
    public ResponseEntity<MovimientoInventarioResponse> registrarAjusteNegativo(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrarAjusteNegativo(request));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarPorProducto(
            @PathVariable Long idProducto) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(idProducto));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(movimientoService.listarPorFecha(desde, hasta));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarPorTipo(
            @PathVariable String tipoMovimiento) {
        return ResponseEntity.ok(movimientoService.listarPorTipo(tipoMovimiento));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarHistorial() {
        return ResponseEntity.ok(movimientoService.listarHistorial());
    }
}
