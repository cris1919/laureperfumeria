package com.laure.perfumeria.backend.model.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CheckoutResponse {
    private Long idPedido;
    private Long idVenta;
    private BigDecimal total;
}
