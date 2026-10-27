package com.laure.perfumeria.backend.model.ventas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarVentaRequest {

    @NotNull(message = "El pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El usuario es obligatorio")
    private Long idUsuario;
}
