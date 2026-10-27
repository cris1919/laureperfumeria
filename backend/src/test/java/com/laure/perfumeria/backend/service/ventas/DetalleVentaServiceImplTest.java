package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.model.ventas.dto.DetalleVentaResponse;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalleVentaServiceImplTest {

    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private VentaRepository ventaRepository;
    @InjectMocks private DetalleVentaServiceImpl detalleVentaService;

    @Test
    void listarPorVentaDebeRetornarListaVaciaCuandoNoHayDetalles() {
        when(ventaRepository.existsById(1L)).thenReturn(true);
        when(detalleVentaRepository.findByVenta_IdVentaOrderByIdDetalleVentaAsc(1L))
                .thenReturn(List.of());

        List<DetalleVentaResponse> response = detalleVentaService.listarPorVenta(1L);

        assertTrue(response.isEmpty());
    }
}
