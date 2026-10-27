package com.laure.perfumeria.backend.model.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioResponse {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String rol;
    private String estado;
    private java.time.LocalDateTime fechaRegistro;
}
