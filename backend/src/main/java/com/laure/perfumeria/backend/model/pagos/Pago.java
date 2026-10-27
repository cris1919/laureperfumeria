package com.laure.perfumeria.backend.model.pagos;

import com.laure.perfumeria.backend.model.pedidos.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    private LocalDateTime fechaPago;

    @Column(precision = 12, scale = 2)
    private BigDecimal monto;

    private String metodoPago;
    private String estadoPago;
    private String referencia;
    private String observaciones;
    private LocalDateTime fechaRegistro;
}
