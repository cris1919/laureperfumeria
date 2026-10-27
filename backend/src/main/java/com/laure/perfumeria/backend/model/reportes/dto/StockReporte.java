package com.laure.perfumeria.backend.model.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockReporte {

    private Long idProducto;
    private String nombreProducto;
    private String marca;
    private Integer stock;
    private String estado;
}
