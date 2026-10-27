package com.laure.perfumeria.backend.service.categorias;

import com.laure.perfumeria.backend.model.categorias.Categoria;
import com.laure.perfumeria.backend.model.categorias.dto.CategoriaRequest;
import com.laure.perfumeria.backend.model.categorias.dto.CategoriaResponse;
import com.laure.perfumeria.backend.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void registrarDebeCrearCategoriaActiva() {
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaResponse response = categoriaService.registrar(
                new CategoriaRequest("Perfumes", "Fragancias disponibles"));

        assertEquals("Perfumes", response.getNombre());
        assertEquals("ACTIVO", response.getEstado());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void cambiarEstadoDebeRechazarEstadoInvalido() {
        Categoria categoria = new Categoria(1L, "Perfumes", "Descripción", "ACTIVO");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.cambiarEstado(1L, "DESCONOCIDO"));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}
