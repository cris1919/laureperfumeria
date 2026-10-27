package com.laure.perfumeria.backend.service.productos;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.categorias.Categoria;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.model.productos.dto.EstadoProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.PrecioProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoResponse;
import com.laure.perfumeria.backend.repository.CategoriaRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public ProductoResponse registrar(ProductoRequest request) {
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        producto.setEstado(ACTIVO);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());
        return convertir(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return convertir(obtenerProducto(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> filtrarPorCategoria(Long idCategoria) {
        return productoRepository.findByCategoria_IdCategoria(idCategoria)
                .stream().map(this::convertir).toList();
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerProducto(id);
        aplicarDatos(producto, request);
        producto.setFechaActualizacion(LocalDateTime.now());
        return convertir(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizarPrecioYCosto(Long id, PrecioProductoRequest request) {
        Producto producto = obtenerProducto(id);
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCostoUnitario(request.getCostoUnitario());
        producto.setFechaActualizacion(LocalDateTime.now());
        return convertir(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse cambiarEstado(Long id, EstadoProductoRequest request) {
        Producto producto = obtenerProducto(id);
        String estado = request.getEstado().trim().toUpperCase();
        if (!ACTIVO.equals(estado) && !INACTIVO.equals(estado)) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        producto.setEstado(estado);
        producto.setFechaActualizacion(LocalDateTime.now());
        return convertir(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarDisponibles() {
        return productoRepository.findByEstadoIgnoreCaseAndStockGreaterThan(ACTIVO, 0)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarDestacados() {
        return productoRepository
                .findTop4ByEstadoIgnoreCaseAndStockGreaterThanOrderByFechaCreacionDesc(ACTIVO, 0)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarBajoStock(Integer limiteStock) {
        if (limiteStock < 0) {
            throw new IllegalArgumentException("El límite de stock no puede ser negativo");
        }
        return productoRepository.findByStockLessThanEqual(limiteStock)
                .stream().map(this::convertir).toList();
    }

    private void aplicarDatos(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la categoría con ID " + request.getIdCategoria()));
        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setMarca(request.getMarca());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCostoUnitario(request.getCostoUnitario());
        producto.setStock(request.getStock());
        if (request.getImagenes() == null || request.getImagenes().size() != 4) {
            throw new IllegalArgumentException("Cada producto debe tener exactamente 4 imágenes");
        }
        producto.setImagenes(new ArrayList<>(request.getImagenes()));
        producto.setImagenUrl(request.getImagenes().get(0));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el producto con ID " + id));
    }

    private ProductoResponse convertir(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getCategoria().getIdCategoria(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getMarca(),
                producto.getPrecioVenta(),
                producto.getCostoUnitario(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getImagenes(),
                producto.getEstado(),
                producto.getFechaCreacion(),
                producto.getFechaActualizacion());
    }
}
