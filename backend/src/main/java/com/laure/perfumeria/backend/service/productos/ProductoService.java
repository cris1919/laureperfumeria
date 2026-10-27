package com.laure.perfumeria.backend.service.productos;

import com.laure.perfumeria.backend.model.productos.dto.EstadoProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.PrecioProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse registrar(ProductoRequest request);

    List<ProductoResponse> listarTodos();

    ProductoResponse buscarPorId(Long id);

    List<ProductoResponse> buscarPorNombre(String nombre);

    List<ProductoResponse> filtrarPorCategoria(Long idCategoria);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    ProductoResponse actualizarPrecioYCosto(Long id, PrecioProductoRequest request);

    ProductoResponse cambiarEstado(Long id, EstadoProductoRequest request);

    List<ProductoResponse> listarDisponibles();

    List<ProductoResponse> listarDestacados();

    List<ProductoResponse> listarBajoStock(Integer limiteStock);
}
