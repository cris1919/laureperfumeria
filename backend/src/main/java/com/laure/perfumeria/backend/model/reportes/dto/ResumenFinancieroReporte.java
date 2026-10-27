package com.laure.perfumeria.backend.model.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ResumenFinancieroReporte {

    private BigDecimal ingresos;
    private BigDecimal costos;
    private BigDecimal margenBruto;
}
