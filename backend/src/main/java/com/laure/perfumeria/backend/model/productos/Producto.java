package com.laure.perfumeria.backend.model.productos;

import com.laure.perfumeria.backend.model.categorias.Categoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    private String nombre;
    private String descripcion;
    private String marca;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(precision = 12, scale = 2)
    private BigDecimal costoUnitario;

    private Integer stock;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @ElementCollection
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "id_producto"))
    @OrderColumn(name = "orden")
    @Column(name = "imagen_url", nullable = false)
    private List<String> imagenes = new ArrayList<>();

    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
