package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.model.ventas.dto.VentaResponse;
import com.laure.perfumeria.backend.model.ventas.dto.RegistrarVentaRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaService {

    VentaResponse registrar(RegistrarVentaRequest request);

    List<VentaResponse> listarTodas();

    VentaResponse buscarPorId(Long id);

    List<VentaResponse> buscarPorPeriodo(LocalDate desde, LocalDate hasta);

    VentaResponse buscarPorPedido(Long idPedido);

    VentaResponse anular(Long id);

    BigDecimal calcularTotalVentas();
}
