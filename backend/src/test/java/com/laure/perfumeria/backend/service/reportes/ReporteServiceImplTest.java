package com.laure.perfumeria.backend.service.reportes;

import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @InjectMocks private ReporteServiceImpl reporteService;

    @Test
    void productosBajoStockDebeFiltrarPorLimite() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Perfume");
        producto.setStock(2);
        when(productoRepository.findByStockLessThanEqual(5)).thenReturn(List.of(producto));

        assertEquals(1, reporteService.productosBajoStock(5).size());
    }
}
