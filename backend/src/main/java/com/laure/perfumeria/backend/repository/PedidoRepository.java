package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCliente_IdClienteOrderByFechaPedidoDesc(Long idCliente);

    List<Pedido> findByEstadoPedidoIgnoreCaseOrderByFechaPedidoDesc(String estadoPedido);

    List<Pedido> findByFechaPedidoBetweenOrderByFechaPedidoDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<Pedido> findByEstadoPedidoIgnoreCaseAndFechaPedidoBetweenOrderByFechaPedidoDesc(
            String estadoPedido, LocalDateTime desde, LocalDateTime hasta);
}
