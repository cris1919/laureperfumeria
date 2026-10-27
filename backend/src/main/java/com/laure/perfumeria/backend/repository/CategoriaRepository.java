package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.categorias.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
