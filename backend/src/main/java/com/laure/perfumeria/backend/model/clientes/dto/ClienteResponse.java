package com.laure.perfumeria.backend.model.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ClienteResponse {

    private Long idCliente;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;
    private String estado;
    private LocalDateTime fechaRegistro;
}
