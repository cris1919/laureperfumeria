package com.laure.perfumeria.backend.model.productos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoProductoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
