package com.laure.perfumeria.backend.model.usuarios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombres;
    private String apellidos;
    private String correo;

    @Column(name = "password_hash")
    private String passwordHash;

    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;
}
