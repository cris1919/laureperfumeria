package com.laure.perfumeria.backend.service.clientes;

import com.laure.perfumeria.backend.model.clientes.dto.CambiarPasswordRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteRegistroRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteResponse;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteUpdateRequest;
import com.laure.perfumeria.backend.model.clientes.dto.EstadoClienteRequest;

import java.util.List;

public interface ClienteService {

    ClienteResponse registrar(ClienteRegistroRequest request);

    ClienteResponse consultarPerfil(Long id, String correoAutenticado, boolean esAdministrador);

    ClienteResponse actualizarDatos(Long id, ClienteUpdateRequest request,
                                    String correoAutenticado, boolean esAdministrador);

    void cambiarPassword(Long id, CambiarPasswordRequest request,
                         String correoAutenticado, boolean esAdministrador);

    List<ClienteResponse> listarTodos();

    ClienteResponse cambiarEstado(Long id, EstadoClienteRequest request);
}
