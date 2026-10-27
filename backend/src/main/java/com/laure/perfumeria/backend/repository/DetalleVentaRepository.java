package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta_IdVentaOrderByIdDetalleVentaAsc(Long idVenta);
}
