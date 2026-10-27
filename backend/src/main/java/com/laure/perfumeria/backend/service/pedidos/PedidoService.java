package com.laure.perfumeria.backend.service.pedidos;

import com.laure.perfumeria.backend.model.pedidos.dto.EstadoPedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoResponse;

import java.time.LocalDate;
import java.util.List;

public interface PedidoService {

    PedidoResponse registrar(PedidoRequest request);

    List<PedidoResponse> listarPorCliente(Long idCliente, String correoAutenticado, boolean esAdministrador);

    PedidoResponse buscarPorId(Long id);

    List<PedidoResponse> listarHistorialCliente(Long idCliente, String correoAutenticado, boolean esAdministrador);

    List<PedidoResponse> listarTodos();

    List<PedidoResponse> filtrar(String estado, LocalDate desde, LocalDate hasta);

    PedidoResponse actualizarEstado(Long id, EstadoPedidoRequest request);

    PedidoResponse cancelar(Long id);

    String consultarEstado(Long id);
}
