package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.pedidos.dto.ActualizarCantidadDetalleRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoResponse;
import com.laure.perfumeria.backend.service.pedidos.DetallePedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos/{idPedido}/detalles")
@RequiredArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @PostMapping
    public ResponseEntity<DetallePedidoResponse> agregarProducto(
            @PathVariable Long idPedido,
            @Valid @RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detallePedidoService.agregarProducto(idPedido, request));
    }

    @GetMapping
    public ResponseEntity<List<DetallePedidoResponse>> listarProductos(@PathVariable Long idPedido) {
        return ResponseEntity.ok(detallePedidoService.listarProductos(idPedido));
    }

    @PatchMapping("/{idDetalle}")
    public ResponseEntity<DetallePedidoResponse> actualizarCantidad(
            @PathVariable Long idPedido,
            @PathVariable Long idDetalle,
            @Valid @RequestBody ActualizarCantidadDetalleRequest request) {
        return ResponseEntity.ok(
                detallePedidoService.actualizarCantidad(idPedido, idDetalle, request));
    }

    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long idPedido,
            @PathVariable Long idDetalle) {
        detallePedidoService.eliminarProducto(idPedido, idDetalle);
        return ResponseEntity.noContent().build();
    }
}
