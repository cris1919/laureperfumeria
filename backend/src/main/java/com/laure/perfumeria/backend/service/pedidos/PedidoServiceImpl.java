package com.laure.perfumeria.backend.service.pedidos;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.deatallePedido.DetallePedido;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoResponse;
import com.laure.perfumeria.backend.model.pedidos.dto.EstadoPedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoResponse;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import com.laure.perfumeria.backend.repository.DetallePedidoRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
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
public class PedidoServiceImpl implements PedidoService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String CONFIRMADO = "CONFIRMADO";
    private static final String CANCELADO = "CANCELADO";
    private static final String ENTREGADO = "ENTREGADO";
    private static final String ACTIVO = "ACTIVO";

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Override
    public PedidoResponse registrar(PedidoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el cliente con ID " + request.getIdCliente()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstadoPedido(PENDIENTE);
        pedido.setCostoEnvio(request.getCostoEnvio());
        pedido.setObservaciones(request.getObservaciones());

        BigDecimal subtotal = BigDecimal.ZERO;
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el producto con ID " + detalleRequest.getIdProducto()));
            if (!ACTIVO.equalsIgnoreCase(producto.getEstado())) {
                throw new IllegalArgumentException("El producto " + producto.getNombre() + " no está activo");
            }
            if (producto.getStock() < detalleRequest.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getNombre());
            }

            BigDecimal precio = producto.getPrecioVenta();
            BigDecimal detalleSubtotal = precio.multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));
            subtotal = subtotal.add(detalleSubtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(detalleSubtotal);
            detallePedidoRepository.save(detalle);
        }

        pedidoGuardado.setSubtotal(subtotal);
        pedidoGuardado.setTotal(subtotal.add(request.getCostoEnvio()));
        return convertir(pedidoRepository.save(pedidoGuardado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long idCliente, String correoAutenticado, boolean esAdministrador) {
        Cliente cliente = validarCliente(idCliente);
        validarAccesoCliente(cliente, correoAutenticado, esAdministrador);
        return pedidoRepository.findByCliente_IdClienteOrderByFechaPedidoDesc(idCliente)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return convertir(obtenerPedido(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarHistorialCliente(Long idCliente, String correoAutenticado, boolean esAdministrador) {
        return listarPorCliente(idCliente, correoAutenticado, esAdministrador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> filtrar(String estado, LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.of(2999, 12, 31, 23, 59, 59) : hasta.atTime(LocalTime.MAX);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        if (estado != null && !estado.isBlank()) {
            return pedidoRepository.findByEstadoPedidoIgnoreCaseAndFechaPedidoBetweenOrderByFechaPedidoDesc(
                            estado.trim(), inicio, fin)
                    .stream().map(this::convertir).toList();
        }
        return pedidoRepository.findByFechaPedidoBetweenOrderByFechaPedidoDesc(inicio, fin)
                .stream().map(this::convertir).toList();
    }

    @Override
    public PedidoResponse actualizarEstado(Long id, EstadoPedidoRequest request) {
        Pedido pedido = obtenerPedido(id);
        String estado = request.getEstado().trim().toUpperCase();
        if (!List.of(PENDIENTE, CONFIRMADO, "EN_PREPARACION", "ENVIADO", ENTREGADO, CANCELADO)
                .contains(estado)) {
            throw new IllegalArgumentException("Estado de pedido no válido");
        }
        if (CANCELADO.equals(pedido.getEstadoPedido()) || ENTREGADO.equals(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException("No se puede cambiar el estado de un pedido finalizado");
        }
        validarTransicion(pedido.getEstadoPedido(), estado);
        pedido.setEstadoPedido(estado);
        return convertir(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = obtenerPedido(id);
        if (!PENDIENTE.equals(pedido.getEstadoPedido()) && !CONFIRMADO.equals(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException("El pedido solo puede cancelarse cuando está pendiente o confirmado");
        }
        pedido.setEstadoPedido(CANCELADO);
        return convertir(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public String consultarEstado(Long id) {
        return obtenerPedido(id).getEstadoPedido();
    }

    private Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el pedido con ID " + id));
    }

    private Cliente validarCliente(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente con ID " + idCliente));
    }

    private void validarAccesoCliente(Cliente cliente, String correoAutenticado, boolean esAdministrador) {
        if (!esAdministrador && !cliente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para consultar los pedidos de este cliente");
        }
    }

    private void validarTransicion(String actual, String nuevo) {
        boolean valida = switch (actual.toUpperCase()) {
            case PENDIENTE -> CONFIRMADO.equals(nuevo) || CANCELADO.equals(nuevo);
            case CONFIRMADO -> "EN_PREPARACION".equals(nuevo) || CANCELADO.equals(nuevo);
            case "EN_PREPARACION" -> "ENVIADO".equals(nuevo);
            case "ENVIADO" -> ENTREGADO.equals(nuevo);
            default -> false;
        };
        if (!valida) {
            throw new IllegalArgumentException("Transición de estado no permitida: " + actual + " -> " + nuevo);
        }
    }

    private PedidoResponse convertir(Pedido pedido) {
        List<DetallePedidoResponse> detalles = detallePedidoRepository
                .findByPedido_IdPedidoOrderByIdDetalleAsc(pedido.getIdPedido())
                .stream()
                .map(detalle -> new DetallePedidoResponse(
                        detalle.getIdDetalle(),
                        detalle.getProducto().getIdProducto(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()))
                .toList();

        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getCliente().getIdCliente(),
                pedido.getFechaPedido(),
                pedido.getEstadoPedido(),
                pedido.getSubtotal(),
                pedido.getCostoEnvio(),
                pedido.getTotal(),
                pedido.getObservaciones(),
                detalles);
    }
}
