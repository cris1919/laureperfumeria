package com.laure.perfumeria.backend.model.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String mensaje;
    private String token;
    private String tipoToken;
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String rol;
}
