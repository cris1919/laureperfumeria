package com.laure.perfumeria.backend.model.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidosEstadoReporte {

    private String estado;
    private Long cantidad;
}
