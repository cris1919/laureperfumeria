package com.laure.perfumeria.backend.service.movimientosInventario;

import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioRequest;
import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoInventarioService {

    MovimientoInventarioResponse registrarEntrada(MovimientoInventarioRequest request);

    MovimientoInventarioResponse registrarSalida(MovimientoInventarioRequest request);

    MovimientoInventarioResponse registrarAjustePositivo(MovimientoInventarioRequest request);

    MovimientoInventarioResponse registrarAjusteNegativo(MovimientoInventarioRequest request);

    List<MovimientoInventarioResponse> listarPorProducto(Long idProducto);

    List<MovimientoInventarioResponse> listarPorFecha(LocalDate desde, LocalDate hasta);

    List<MovimientoInventarioResponse> listarPorTipo(String tipoMovimiento);

    List<MovimientoInventarioResponse> listarHistorial();
}
