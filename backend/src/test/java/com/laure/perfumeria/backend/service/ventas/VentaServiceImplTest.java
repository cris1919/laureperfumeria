package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.ventas.Venta;
import com.laure.perfumeria.backend.repository.DetallePedidoRepository;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private DetallePedidoRepository detallePedidoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @InjectMocks private VentaServiceImpl ventaService;

    @Test
    void calcularTotalVentasDebeExcluirVentasAnuladas() {
        Pedido pedidoValido = new Pedido();
        pedidoValido.setTotal(new BigDecimal("100.00"));
        Pedido pedidoAnulado = new Pedido();
        pedidoAnulado.setTotal(new BigDecimal("50.00"));
        Venta registrada = new Venta();
        registrada.setEstado("REGISTRADA");
        registrada.setPedido(pedidoValido);
        Venta anulada = new Venta();
        anulada.setEstado("ANULADA");
        anulada.setPedido(pedidoAnulado);
        when(ventaRepository.findAll()).thenReturn(List.of(registrada, anulada));

        assertEquals(new BigDecimal("100.00"), ventaService.calcularTotalVentas());
    }
}
