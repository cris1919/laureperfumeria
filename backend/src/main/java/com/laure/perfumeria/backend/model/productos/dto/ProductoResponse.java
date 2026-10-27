package com.laure.perfumeria.backend.model.productos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductoResponse {

    private Long idProducto;
    private Long idCategoria;
    private String nombre;
    private String descripcion;
    private String marca;
    private BigDecimal precioVenta;
    private BigDecimal costoUnitario;
    private Integer stock;
    private String imagenUrl;
    private List<String> imagenes;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
