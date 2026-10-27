package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.model.ventas.dto.DetalleVentaResponse;

import java.util.List;

public interface DetalleVentaService {

    List<DetalleVentaResponse> listarPorVenta(Long idVenta);
}
