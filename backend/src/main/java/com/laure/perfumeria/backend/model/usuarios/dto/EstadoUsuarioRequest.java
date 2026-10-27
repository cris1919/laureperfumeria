package com.laure.perfumeria.backend.model.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUsuarioRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
