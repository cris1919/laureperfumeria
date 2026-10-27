package com.laure.perfumeria.backend.model.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PagoResponse {

    private Long idPago;
    private Long idPedido;
    private LocalDateTime fechaPago;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private String referencia;
    private String observaciones;
    private LocalDateTime fechaRegistro;
}
