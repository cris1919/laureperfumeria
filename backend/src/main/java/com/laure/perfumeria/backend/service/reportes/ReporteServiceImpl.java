package com.laure.perfumeria.backend.service.reportes;

import com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.model.reportes.dto.PedidosEstadoReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ProductoVentasReporte;
import com.laure.perfumeria.backend.model.reportes.dto.ResumenFinancieroReporte;
import com.laure.perfumeria.backend.model.reportes.dto.StockReporte;
import com.laure.perfumeria.backend.model.reportes.dto.VentasPeriodoReporte;
import com.laure.perfumeria.backend.model.ventas.Venta;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);
    private static final String ANULADA = "ANULADA";
    private static final BigDecimal CERO = BigDecimal.ZERO;

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    public List<ProductoVentasReporte> productosMasVendidos(Integer limite) {
        return productosVendidos(limite, false);
    }

    @Override
    public List<ProductoVentasReporte> productosMenosVendidos(Integer limite) {
        return productosVendidos(limite, true);
    }

    private List<ProductoVentasReporte> productosVendidos(Integer limite, boolean ascendente) {
        validarLimite(limite);
        Map<Long, ProductoAcumulado> acumulados = new HashMap<>();
        productoRepository.findAll().forEach(producto -> acumulados.put(
                producto.getIdProducto(), new ProductoAcumulado(producto)));

        for (Venta venta : ventasValidas(null, null)) {
            for (DetalleVenta detalle : detallesDe(venta)) {
                ProductoAcumulado acumulado = acumulados.computeIfAbsent(
                        detalle.getProducto().getIdProducto(), id -> new ProductoAcumulado(detalle.getProducto()));
                acumulado.agregar(detalle);
            }
        }

        Comparator<ProductoAcumulado> comparador = Comparator.comparing(
                ProductoAcumulado::cantidadVendida);
        if (!ascendente) {
            comparador = comparador.reversed();
        }
        return acumulados.values().stream()
                .sorted(comparador.thenComparing(ProductoAcumulado::nombreProducto))
                .limit(limite)
                .map(ProductoAcumulado::toResponse)
                .toList();
    }

    @Override
    public List<VentasPeriodoReporte> ventasPorPeriodo(
            String agrupacion, LocalDate desde, LocalDate hasta) {
        String agrupacionNormalizada = StringUtils.defaultIfBlank(agrupacion, "dia").toLowerCase();
        if (!List.of("dia", "mes").contains(agrupacionNormalizada)) {
            throw new IllegalArgumentException("La agrupación debe ser DIA o MES");
        }
        Map<String, PeriodoAcumulado> acumulados = new LinkedHashMap<>();
        for (Venta venta : ventasValidas(desde, hasta)) {
            String periodo = "mes".equals(agrupacionNormalizada)
                    ? YearMonth.from(venta.getFechaVenta()).toString()
                    : venta.getFechaVenta().toLocalDate().toString();
            PeriodoAcumulado acumulado = acumulados.computeIfAbsent(periodo, key -> new PeriodoAcumulado());
            acumulado.cantidadVentas++;
            for (DetalleVenta detalle : detallesDe(venta)) {
                acumulado.agregar(detalle);
            }
        }
        log.info("Reporte de ventas generado: agrupacion={}, registros={}", agrupacionNormalizada, acumulados.size());
        return acumulados.entrySet().stream()
                .map(entry -> entry.getValue().toResponse(entry.getKey()))
                .toList();
    }

    @Override
    public ResumenFinancieroReporte ingresosVentas(LocalDate desde, LocalDate hasta) {
        return resumenFinanciero(desde, hasta);
    }

    @Override
    public ResumenFinancieroReporte costosProductosVendidos(LocalDate desde, LocalDate hasta) {
        return resumenFinanciero(desde, hasta);
    }

    @Override
    public ResumenFinancieroReporte margenBruto(LocalDate desde, LocalDate hasta) {
        return resumenFinanciero(desde, hasta);
    }

    private ResumenFinancieroReporte resumenFinanciero(LocalDate desde, LocalDate hasta) {
        BigDecimal ingresos = CERO;
        BigDecimal costos = CERO;
        for (Venta venta : ventasValidas(desde, hasta)) {
            for (DetalleVenta detalle : detallesDe(venta)) {
                ingresos = ingresos.add(detalle.getSubtotal());
                costos = costos.add(detalle.getCostoUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }
        }
        return new ResumenFinancieroReporte(ingresos, costos, ingresos.subtract(costos));
    }

    @Override
    public List<StockReporte> stockDisponible() {
        return productoRepository.findAll().stream()
                .map(this::stockResponse)
                .toList();
    }

    @Override
    public List<StockReporte> productosBajoStock(Integer limite) {
        validarLimite(limite);
        return productoRepository.findByStockLessThanEqual(limite).stream()
                .map(this::stockResponse)
                .toList();
    }

    @Override
    public List<PedidosEstadoReporte> pedidosPorEstado() {
        return pedidoRepository.findAll().stream()
                .collect(Collectors.groupingBy(Pedido::getEstadoPedido, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new PedidosEstadoReporte(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<Venta> ventasValidas(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.of(2999, 12, 31, 23, 59, 59) : hasta.atTime(LocalTime.MAX);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return ventaRepository.findByFechaVentaBetweenOrderByFechaVentaDesc(inicio, fin).stream()
                .filter(venta -> !ANULADA.equalsIgnoreCase(venta.getEstado()))
                .toList();
    }

    private List<DetalleVenta> detallesDe(Venta venta) {
        return detalleVentaRepository.findByVenta_IdVentaOrderByIdDetalleVentaAsc(venta.getIdVenta());
    }

    private StockReporte stockResponse(Producto producto) {
        return new StockReporte(
                producto.getIdProducto(), producto.getNombre(), producto.getMarca(),
                producto.getStock(), producto.getEstado());
    }

    private void validarLimite(Integer limite) {
        if (limite == null || limite < 1) {
            throw new IllegalArgumentException("El límite debe ser mayor a cero");
        }
    }

    private static class ProductoAcumulado {
        private final Long idProducto;
        private final String nombre;
        private int cantidad;
        private BigDecimal ingresos = CERO;
        private BigDecimal costos = CERO;

        private ProductoAcumulado(Producto producto) {
            this.idProducto = producto.getIdProducto();
            this.nombre = producto.getNombre();
        }

        private void agregar(DetalleVenta detalle) {
            cantidad += detalle.getCantidad();
            ingresos = ingresos.add(detalle.getSubtotal());
            costos = costos.add(detalle.getCostoUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad())));
        }

        private int cantidadVendida() { return cantidad; }
        private String nombreProducto() { return nombre; }

        private ProductoVentasReporte toResponse() {
            return new ProductoVentasReporte(idProducto, nombre, cantidad, ingresos, costos, ingresos.subtract(costos));
        }
    }

    private static class PeriodoAcumulado {
        private int cantidadVentas;
        private BigDecimal ingresos = CERO;
        private BigDecimal costos = CERO;

        private void agregar(DetalleVenta detalle) {
            ingresos = ingresos.add(detalle.getSubtotal());
            costos = costos.add(detalle.getCostoUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad())));
        }

        private VentasPeriodoReporte toResponse(String periodo) {
            return new VentasPeriodoReporte(periodo, cantidadVentas, ingresos, costos, ingresos.subtract(costos));
        }
    }
}
