package com.laure.perfumeria.backend.model.movimientosInventario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovimientoInventarioResponse {

    private Long idMovimiento;
    private Long idProducto;
    private String nombreProducto;
    private Long idUsuario;
    private String tipoMovimiento;
    private Integer cantidad;
    private Integer stockActual;
    private LocalDateTime fechaMovimiento;
    private String motivo;
    private String referencia;
}
