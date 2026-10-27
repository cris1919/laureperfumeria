package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.deatallePedido.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido_IdPedidoOrderByIdDetalleAsc(Long idPedido);

    Optional<DetallePedido> findByPedido_IdPedidoAndProducto_IdProducto(Long idPedido, Long idProducto);
}
