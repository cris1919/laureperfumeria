package com.laure.perfumeria.backend.service.pagos;

import com.laure.perfumeria.backend.model.pagos.Pago;
import com.laure.perfumeria.backend.model.pagos.dto.PagoResponse;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.repository.PagoRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
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
class PagoServiceImplTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @InjectMocks private PagoServiceImpl pagoService;

    @Test
    void confirmarDebeCambiarEstadoYRegistrarFecha() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(2L);
        Pago pago = new Pago(1L, pedido, null, new BigDecimal("50"),
                "TRANSFERENCIA", "PENDIENTE", null, null, null);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagoResponse response = pagoService.confirmar(1L);

        assertEquals("CONFIRMADO", response.getEstadoPago());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getFechaPago());
    }
}
