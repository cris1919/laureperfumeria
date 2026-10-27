package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.deatallePedido.DetallePedido;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.model.ventas.Venta;
import com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta;
import com.laure.perfumeria.backend.model.ventas.dto.DetalleVentaResponse;
import com.laure.perfumeria.backend.model.ventas.dto.RegistrarVentaRequest;
import com.laure.perfumeria.backend.model.ventas.dto.VentaResponse;
import com.laure.perfumeria.backend.repository.DetallePedidoRepository;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private static final String CONFIRMADO = "CONFIRMADO";
    private static final String REGISTRADA = "REGISTRADA";
    private static final String ANULADA = "ANULADA";

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    public VentaResponse registrar(RegistrarVentaRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el pedido con ID " + request.getIdPedido()));
        if (!CONFIRMADO.equalsIgnoreCase(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException("Solo se pueden registrar ventas de pedidos CONFIRMADOS");
        }
        if (ventaRepository.findByPedido_IdPedido(request.getIdPedido()).isPresent()) {
            throw new IllegalArgumentException("El pedido ya tiene una venta registrada");
        }

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID " + request.getIdUsuario()));

        List<DetallePedido> detallesPedido = detallePedidoRepository
                .findByPedido_IdPedidoOrderByIdDetalleAsc(pedido.getIdPedido());
        if (detallesPedido.isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene productos");
        }

        Venta venta = new Venta();
        venta.setPedido(pedido);
        venta.setUsuario(usuario);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(REGISTRADA);
        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetallePedido detallePedido : detallesPedido) {
            var producto = detallePedido.getProducto();
            if (producto.getStock() < detallePedido.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getNombre());
            }

            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(ventaGuardada);
            detalleVenta.setProducto(producto);
            detalleVenta.setCantidad(detallePedido.getCantidad());
            detalleVenta.setPrecioUnitario(detallePedido.getPrecioUnitario());
            detalleVenta.setCostoUnitario(producto.getCostoUnitario());
            detalleVenta.setSubtotal(detallePedido.getSubtotal());
            detalleVentaRepository.save(detalleVenta);

            producto.setStock(producto.getStock() - detallePedido.getCantidad());
            productoRepository.save(producto);
        }

        return convertir(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarTodas() {
        return ventaRepository.findAllByOrderByFechaVentaDesc().stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(Long id) {
        return convertir(obtenerVenta(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> buscarPorPeriodo(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.of(2999, 12, 31, 23, 59, 59) : hasta.atTime(LocalTime.MAX);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return ventaRepository.findByFechaVentaBetweenOrderByFechaVentaDesc(inicio, fin)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse buscarPorPedido(Long idPedido) {
        return convertir(ventaRepository.findByPedido_IdPedido(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró una venta para el pedido indicado")));
    }

    @Override
    public VentaResponse anular(Long id) {
        Venta venta = obtenerVenta(id);
        if (ANULADA.equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalArgumentException("La venta ya está anulada");
        }
        detalleVentaRepository.findByVenta_IdVentaOrderByIdDetalleVentaAsc(id).forEach(detalle -> {
            var producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        });
        venta.setEstado(ANULADA);
        return convertir(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalVentas() {
        return ventaRepository.findAll().stream()
                .filter(venta -> !ANULADA.equalsIgnoreCase(venta.getEstado()))
                .map(venta -> venta.getPedido().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Venta obtenerVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la venta con ID " + id));
    }

    private VentaResponse convertir(Venta venta) {
        List<DetalleVentaResponse> detalles = detalleVentaRepository
                .findByVenta_IdVentaOrderByIdDetalleVentaAsc(venta.getIdVenta())
                .stream().map(this::convertirDetalle).toList();
        BigDecimal costoTotal = detalles.stream().map(DetalleVentaResponse::getCostoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal margen = detalles.stream().map(DetalleVentaResponse::getMargenBruto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new VentaResponse(
                venta.getIdVenta(),
                venta.getPedido().getIdPedido(),
                venta.getUsuario().getIdUsuario(),
                venta.getFechaVenta(),
                venta.getPedido().getTotal(),
                costoTotal,
                margen,
                venta.getEstado(),
                detalles);
    }

    private DetalleVentaResponse convertirDetalle(com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta detalle) {
        BigDecimal costoTotal = detalle.getCostoUnitario()
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
        BigDecimal margen = detalle.getSubtotal().subtract(costoTotal);
        return new DetalleVentaResponse(
                detalle.getIdDetalleVenta(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getCostoUnitario(),
                detalle.getSubtotal(),
                costoTotal,
                margen);
    }
}
