package com.laure.perfumeria.backend.repository;

import com.laure.perfumeria.backend.model.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria_IdCategoria(Long idCategoria);

    List<Producto> findByEstadoIgnoreCaseAndStockGreaterThan(String estado, Integer stock);

    List<Producto> findTop4ByEstadoIgnoreCaseAndStockGreaterThanOrderByFechaCreacionDesc(
            String estado, Integer stock);

    List<Producto> findByStockLessThanEqual(Integer limiteStock);
}
