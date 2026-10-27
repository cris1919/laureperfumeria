package com.laure.perfumeria.backend.service.pedidos;

import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoResponse;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import com.laure.perfumeria.backend.repository.DetallePedidoRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private DetallePedidoRepository detallePedidoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ProductoRepository productoRepository;
    @InjectMocks private PedidoServiceImpl pedidoService;

    @Test
    void registrarDebeCalcularSubtotalEnvioYTotal() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        Producto producto = new Producto();
        producto.setIdProducto(2L);
        producto.setNombre("Perfume");
        producto.setEstado("ACTIVO");
        producto.setPrecioVenta(new BigDecimal("30.00"));
        producto.setStock(10);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            if (pedido.getIdPedido() == null) pedido.setIdPedido(5L);
            return pedido;
        });
        when(detallePedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(detallePedidoRepository.findByPedido_IdPedidoOrderByIdDetalleAsc(5L)).thenReturn(List.of());

        PedidoResponse response = pedidoService.registrar(new PedidoRequest(
                1L, new BigDecimal("5.00"), "Urgente",
                List.of(new DetallePedidoRequest(2L, 2))));

        assertEquals(new BigDecimal("60.00"), response.getSubtotal());
        assertEquals(new BigDecimal("65.00"), response.getTotal());
    }
}
