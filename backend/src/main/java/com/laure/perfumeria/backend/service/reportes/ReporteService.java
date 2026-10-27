package com.laure.perfumeria.backend.service.reportes;

import com.laure.perfumeria.backend.model.reportes.dto.PedidosEstadoReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ProductoVentasReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ResumenFinancieroReporte;
import com.laure.perfumeria.backend.model.reportes.dto.StockReporte;
import com.laure.perfumeria.backend.model.reportes.dto.VentasPeriodoReporte;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<ProductoVentasReporte> productosMasVendidos(Integer limite);

    List<ProductoVentasReporte> productosMenosVendidos(Integer limite);

    List<VentasPeriodoReporte> ventasPorPeriodo(String agrupacion, LocalDate desde, LocalDate hasta);

    ResumenFinancieroReporte ingresosVentas(LocalDate desde, LocalDate hasta);

    ResumenFinancieroReporte costosProductosVendidos(LocalDate desde, LocalDate hasta);

    ResumenFinancieroReporte margenBruto(LocalDate desde, LocalDate hasta);

    List<StockReporte> stockDisponible();

    List<StockReporte> productosBajoStock(Integer limite);

    List<PedidosEstadoReporte> pedidosPorEstado();
}
