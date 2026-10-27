package com.laure.perfumeria.backend.model.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DetalleVentaResponse {

    private Long idDetalleVenta;
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
    private BigDecimal costoTotal;
    private BigDecimal margenBruto;
}
