package com.laure.perfumeria.backend.model.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El costo de envío es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo de envío no puede ser negativo")
    private BigDecimal costoEnvio;

    private String observaciones;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<@Valid DetallePedidoRequest> detalles;
}
