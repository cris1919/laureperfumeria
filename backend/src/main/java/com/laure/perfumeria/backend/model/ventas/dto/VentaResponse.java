package com.laure.perfumeria.backend.model.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class VentaResponse {

    private Long idVenta;
    private Long idPedido;
    private Long idUsuario;
    private LocalDateTime fechaVenta;
    private BigDecimal totalVenta;
    private BigDecimal costoTotal;
    private BigDecimal margenBruto;
    private String estado;
    private List<DetalleVentaResponse> detalles;
}
