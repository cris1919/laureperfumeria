package com.laure.perfumeria.backend.service.categorias;

import com.laure.perfumeria.backend.model.categorias.dto.CategoriaRequest;
import com.laure.perfumeria.backend.model.categorias.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse registrar(CategoriaRequest request);

    List<CategoriaResponse> listarTodas();

    CategoriaResponse buscarPorId(Long id);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    CategoriaResponse cambiarEstado(Long id, String estado);
}
