package com.laure.perfumeria.backend.model.pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoContraEntregaRequest {

    @NotNull(message = "El pedido es obligatorio")
    private Long idPedido;

    private String referencia;
    private String observaciones;
}
