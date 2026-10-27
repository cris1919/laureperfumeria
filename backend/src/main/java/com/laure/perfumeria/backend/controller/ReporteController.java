package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.reportes.dto.PedidosEstadoReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ProductoVentasReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ResumenFinancieroReporte;
import com.laure.perfumeria.backend.model.reportes.dto.StockReporte;
import com.laure.perfumeria.backend.model.reportes.dto.VentasPeriodoReporte;
import com.laure.perfumeria.backend.service.reportes.ReporteService;
import com.laure.perfumeria.backend.service.reportes.ReporteExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteExcelService reporteExcelService;

    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(defaultValue = "dia") String agrupacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "10") Integer limite) throws IOException {
        byte[] archivo = reporteExcelService.generarExcel(agrupacion, desde, hasta, limite);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reportes-perfumeria.xlsx");
        headers.setContentLength(archivo.length);
        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoVentasReporte>> productosMasVendidos(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(reporteService.productosMasVendidos(limite));
    }

    @GetMapping("/productos-menos-vendidos")
    public ResponseEntity<List<ProductoVentasReporte>> productosMenosVendidos(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(reporteService.productosMenosVendidos(limite));
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<VentasPeriodoReporte>> ventasPorPeriodo(
            @RequestParam(defaultValue = "dia") String agrupacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.ventasPorPeriodo(agrupacion, desde, hasta));
    }

    @GetMapping("/ingresos")
    public ResponseEntity<BigDecimal> ingresos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.ingresosVentas(desde, hasta).getIngresos());
    }

    @GetMapping("/costos")
    public ResponseEntity<BigDecimal> costos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.costosProductosVendidos(desde, hasta).getCostos());
    }

    @GetMapping("/margen-bruto")
    public ResponseEntity<ResumenFinancieroReporte> margenBruto(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.margenBruto(desde, hasta));
    }

    @GetMapping("/stock")
    public ResponseEntity<List<StockReporte>> stockDisponible() {
        return ResponseEntity.ok(reporteService.stockDisponible());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<StockReporte>> stockBajo(
            @RequestParam(defaultValue = "5") Integer limite) {
        return ResponseEntity.ok(reporteService.productosBajoStock(limite));
    }

    @GetMapping("/pedidos-por-estado")
    public ResponseEntity<List<PedidosEstadoReporte>> pedidosPorEstado() {
        return ResponseEntity.ok(reporteService.pedidosPorEstado());
    }
}
