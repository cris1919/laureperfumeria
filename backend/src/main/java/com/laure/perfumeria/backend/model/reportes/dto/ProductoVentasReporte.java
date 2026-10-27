package com.laure.perfumeria.backend.model.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductoVentasReporte {

    private Long idProducto;
    private String nombreProducto;
    private Integer cantidadVendida;
    private BigDecimal ingresos;
    private BigDecimal costos;
    private BigDecimal margenBruto;
}
