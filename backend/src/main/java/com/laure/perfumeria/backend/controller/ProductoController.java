package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.productos.dto.EstadoProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.PrecioProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoResponse;
import com.laure.perfumeria.backend.service.productos.ProductoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> registrar(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoResponse>> filtrarPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(productoService.filtrarPorCategoria(idCategoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/precio-costo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> actualizarPrecioYCosto(
            @PathVariable Long id,
            @Valid @RequestBody PrecioProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizarPrecioYCosto(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoProductoRequest request) {
        return ResponseEntity.ok(productoService.cambiarEstado(id, request));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoResponse>> listarDisponibles() {
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoResponse>> listarDestacados() {
        return ResponseEntity.ok(productoService.listarDestacados());
    }

    @GetMapping("/bajo-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductoResponse>> listarBajoStock(
            @RequestParam(defaultValue = "5") @Min(value = 0, message = "El límite no puede ser negativo") Integer limite) {
        return ResponseEntity.ok(productoService.listarBajoStock(limite));
    }
}
