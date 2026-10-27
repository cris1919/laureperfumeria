package com.laure.perfumeria.backend.model.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PedidoResponse {

    private Long idPedido;
    private Long idCliente;
    private LocalDateTime fechaPedido;
    private String estadoPedido;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;
    private String observaciones;
    private List<DetallePedidoResponse> detalles;
}
