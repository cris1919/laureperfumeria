package com.laure.perfumeria.backend.model.categorias.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoriaResponse {

    private Long idCategoria;
    private String nombre;
    private String descripcion;
    private String estado;
}
