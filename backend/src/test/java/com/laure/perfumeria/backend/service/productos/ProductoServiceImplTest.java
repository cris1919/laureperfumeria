package com.laure.perfumeria.backend.service.productos;

import com.laure.perfumeria.backend.model.categorias.Categoria;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.model.productos.dto.PrecioProductoRequest;
import com.laure.perfumeria.backend.model.productos.dto.ProductoResponse;
import com.laure.perfumeria.backend.repository.CategoriaRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void actualizarPrecioYCostoDebeConservarElProductoYActualizarValores() {
        Categoria categoria = new Categoria(1L, "Perfumes", "", "ACTIVO");
        Producto producto = new Producto();
        producto.setIdProducto(10L);
        producto.setCategoria(categoria);
        producto.setNombre("Perfume A");
        producto.setPrecioVenta(new BigDecimal("50.00"));
        producto.setCostoUnitario(new BigDecimal("20.00"));
        producto.setStock(5);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponse response = productoService.actualizarPrecioYCosto(
                10L, new PrecioProductoRequest(new BigDecimal("60.00"), new BigDecimal("25.00")));

        assertEquals(new BigDecimal("60.00"), response.getPrecioVenta());
        assertEquals(new BigDecimal("25.00"), response.getCostoUnitario());
        assertEquals(5, response.getStock());
    }
}
