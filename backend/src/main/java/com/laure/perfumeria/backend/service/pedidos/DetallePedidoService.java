package com.laure.perfumeria.backend.service.pedidos;

import com.laure.perfumeria.backend.model.pedidos.dto.ActualizarCantidadDetalleRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoResponse;

import java.util.List;

public interface DetallePedidoService {

    DetallePedidoResponse agregarProducto(Long idPedido, DetallePedidoRequest request);

    List<DetallePedidoResponse> listarProductos(Long idPedido);

    DetallePedidoResponse actualizarCantidad(
            Long idPedido, Long idDetalle, ActualizarCantidadDetalleRequest request);

    void eliminarProducto(Long idPedido, Long idDetalle);
}
