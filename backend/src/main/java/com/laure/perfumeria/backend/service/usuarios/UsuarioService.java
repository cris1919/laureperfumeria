package com.laure.perfumeria.backend.service.usuarios;

import com.laure.perfumeria.backend.model.usuarios.dto.CambiarPasswordUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.EstadoUsuarioRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioResponse;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioUpdateRequest;
import com.laure.perfumeria.backend.model.usuarios.dto.UsuarioRegistroInicialRequest;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse registrarAdministradorInicial(UsuarioRegistroInicialRequest request);

    UsuarioResponse consultarPerfil(Long id);

    UsuarioResponse actualizarDatos(Long id, UsuarioUpdateRequest request);

    void cambiarPassword(Long id, CambiarPasswordUsuarioRequest request);

    List<UsuarioResponse> listarTodos();

    UsuarioResponse cambiarEstado(Long id, EstadoUsuarioRequest request);
}
