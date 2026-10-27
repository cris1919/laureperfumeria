package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.movimientosInventario.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProducto_IdProductoOrderByFechaMovimientoDesc(Long idProducto);

    List<MovimientoInventario> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<MovimientoInventario> findByTipoMovimientoIgnoreCaseOrderByFechaMovimientoDesc(
            String tipoMovimiento);

    List<MovimientoInventario> findAllByOrderByFechaMovimientoDesc();
}
