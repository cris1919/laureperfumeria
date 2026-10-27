package com.laure.perfumeria.backend.model.clientes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    private String nombres;
    private String apellidos;
    private String correo;
    @Column(name = "password_hash")
    private String passwordHash;
    private String telefono;
    private String direccion;
    private String estado;
    private LocalDateTime fechaRegistro;
}
