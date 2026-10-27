package com.laure.perfumeria.backend.service.categorias;

import com.laure.perfumeria.backend.model.categorias.dto.CategoriaRequest;
import com.laure.perfumeria.backend.model.categorias.dto.CategoriaResponse;
import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.categorias.Categoria;
import com.laure.perfumeria.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    private final CategoriaRepository categoriaRepository;

    @Override
    public CategoriaResponse registrar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setEstado(ACTIVO);
        return convertir(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertir)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return convertir(obtenerCategoria(id));
    }

    @Override
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obtenerCategoria(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return convertir(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponse cambiarEstado(Long id, String estado) {
        Categoria categoria = obtenerCategoria(id);
        String estadoNormalizado = estado.trim().toUpperCase();

        if (!ACTIVO.equals(estadoNormalizado) && !INACTIVO.equals(estadoNormalizado)) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }

        categoria.setEstado(estadoNormalizado);
        return convertir(categoriaRepository.save(categoria));
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la categoría con ID " + id));
    }

    private CategoriaResponse convertir(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado());
    }
}
