package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.ventas.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByPedido_IdPedido(Long idPedido);

    List<Venta> findAllByOrderByFechaVentaDesc();

    List<Venta> findByFechaVentaBetweenOrderByFechaVentaDesc(
            LocalDateTime desde, LocalDateTime hasta);
}
