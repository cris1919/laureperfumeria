package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.pagos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPedido_IdPedidoOrderByFechaRegistroDesc(Long idPedido);

    List<Pago> findByEstadoPagoIgnoreCaseOrderByFechaRegistroDesc(String estadoPago);
}
