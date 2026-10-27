package com.laure.perfumeria.backend.service.reportes;

import com.laure.perfumeria.backend.model.reportes.dto.PedidosEstadoReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ProductoVentasReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ResumenFinancieroReporte;
import com.laure.perfumeria.backend.model.reportes.dto.StockReporte;
import com.laure.perfumeria.backend.model.reportes.dto.VentasPeriodoReporte;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteExcelService {

    private final ReporteService reporteService;

    public byte[] generarExcel(String agrupacion, LocalDate desde, LocalDate hasta, Integer limite)
            throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            CellStyle encabezado = crearEstiloEncabezado(workbook);

            agregarProductos(workbook, encabezado, "Mas vendidos",
                    reporteService.productosMasVendidos(limite));
            agregarProductos(workbook, encabezado, "Menos vendidos",
                    reporteService.productosMenosVendidos(limite));
            agregarVentasPeriodo(workbook, encabezado,
                    reporteService.ventasPorPeriodo(agrupacion, desde, hasta));
            agregarResumen(workbook, encabezado, "Resumen financiero",
                    reporteService.margenBruto(desde, hasta));
            agregarStock(workbook, encabezado, "Stock disponible",
                    reporteService.stockDisponible());
            agregarStock(workbook, encabezado, "Stock bajo",
                    reporteService.productosBajoStock(limite));
            agregarPedidosEstado(workbook, encabezado,
                    reporteService.pedidosPorEstado());

            workbook.write(output);
            return output.toByteArray();
        }
    }

    private void agregarProductos(Workbook workbook, CellStyle estilo,
                                  String nombreHoja, List<ProductoVentasReporte> productos) {
        Sheet sheet = workbook.createSheet(nombreHoja);
        String[] headers = {"ID producto", "Producto", "Cantidad vendida", "Ingresos", "Costos", "Margen bruto"};
        crearEncabezados(sheet, estilo, headers);
        int fila = 1;
        for (ProductoVentasReporte producto : productos) {
            Row row = sheet.createRow(fila++);
            texto(row, 0, producto.getIdProducto());
            texto(row, 1, producto.getNombreProducto());
            texto(row, 2, producto.getCantidadVendida());
            decimal(row, 3, producto.getIngresos());
            decimal(row, 4, producto.getCostos());
            decimal(row, 5, producto.getMargenBruto());
        }
        ajustarColumnas(sheet, headers.length);
    }

    private void agregarVentasPeriodo(Workbook workbook, CellStyle estilo,
                                      List<VentasPeriodoReporte> ventas) {
        Sheet sheet = workbook.createSheet("Ventas por periodo");
        String[] headers = {"Periodo", "Cantidad ventas", "Ingresos", "Costos", "Margen bruto"};
        crearEncabezados(sheet, estilo, headers);
        int fila = 1;
        for (VentasPeriodoReporte venta : ventas) {
            Row row = sheet.createRow(fila++);
            texto(row, 0, venta.getPeriodo());
            texto(row, 1, venta.getCantidadVentas());
            decimal(row, 2, venta.getIngresos());
            decimal(row, 3, venta.getCostos());
            decimal(row, 4, venta.getMargenBruto());
        }
        ajustarColumnas(sheet, headers.length);
    }

    private void agregarResumen(Workbook workbook, CellStyle estilo,
                                String nombreHoja, ResumenFinancieroReporte resumen) {
        Sheet sheet = workbook.createSheet(nombreHoja);
        String[] headers = {"Ingresos", "Costos", "Margen bruto"};
        crearEncabezados(sheet, estilo, headers);
        Row row = sheet.createRow(1);
        decimal(row, 0, resumen.getIngresos());
        decimal(row, 1, resumen.getCostos());
        decimal(row, 2, resumen.getMargenBruto());
        ajustarColumnas(sheet, headers.length);
    }

    private void agregarStock(Workbook workbook, CellStyle estilo,
                              String nombreHoja, List<StockReporte> productos) {
        Sheet sheet = workbook.createSheet(nombreHoja);
        String[] headers = {"ID producto", "Producto", "Marca", "Stock", "Estado"};
        crearEncabezados(sheet, estilo, headers);
        int fila = 1;
        for (StockReporte producto : productos) {
            Row row = sheet.createRow(fila++);
            texto(row, 0, producto.getIdProducto());
            texto(row, 1, producto.getNombreProducto());
            texto(row, 2, producto.getMarca());
            texto(row, 3, producto.getStock());
            texto(row, 4, producto.getEstado());
        }
        ajustarColumnas(sheet, headers.length);
    }

    private void agregarPedidosEstado(Workbook workbook, CellStyle estilo,
                                      List<PedidosEstadoReporte> pedidos) {
        Sheet sheet = workbook.createSheet("Pedidos por estado");
        String[] headers = {"Estado", "Cantidad"};
        crearEncabezados(sheet, estilo, headers);
        int fila = 1;
        for (PedidosEstadoReporte pedido : pedidos) {
            Row row = sheet.createRow(fila++);
            texto(row, 0, pedido.getEstado());
            texto(row, 1, pedido.getCantidad());
        }
        ajustarColumnas(sheet, headers.length);
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private void crearEncabezados(Sheet sheet, CellStyle style, String[] headers) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void texto(Row row, int columna, Object valor) {
        if (valor == null) {
            row.createCell(columna).setCellValue("");
        } else if (valor instanceof Number number) {
            row.createCell(columna).setCellValue(number.doubleValue());
        } else {
            row.createCell(columna).setCellValue(valor.toString());
        }
    }

    private void decimal(Row row, int columna, BigDecimal valor) {
        row.createCell(columna).setCellValue(valor == null ? 0D : valor.doubleValue());
    }

    private void ajustarColumnas(Sheet sheet, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
