package com.laure.perfumeria.backend.service.pedidos;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.deatallePedido.DetallePedido;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.pedidos.dto.ActualizarCantidadDetalleRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoRequest;
import com.laure.perfumeria.backend.model.pedidos.dto.DetallePedidoResponse;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.repository.DetallePedidoRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String ACTIVO = "ACTIVO";

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public DetallePedidoResponse agregarProducto(Long idPedido, DetallePedidoRequest request) {
        Pedido pedido = obtenerPedido(idPedido);
        validarEditable(pedido);
        Producto producto = obtenerProducto(request.getIdProducto());
        validarDisponibilidad(producto, request.getCantidad());

        DetallePedido detalle = detallePedidoRepository
                .findByPedido_IdPedidoAndProducto_IdProducto(idPedido, request.getIdProducto())
                .orElseGet(DetallePedido::new);

        int cantidadTotal = request.getCantidad();
        if (detalle.getIdDetalle() != null) {
            cantidadTotal += detalle.getCantidad();
            validarDisponibilidad(producto, cantidadTotal);
        } else {
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
        }

        detalle.setCantidad(cantidadTotal);
        detalle.setPrecioUnitario(producto.getPrecioVenta());
        detalle.setSubtotal(calcularSubtotal(producto.getPrecioVenta(), cantidadTotal));
        DetallePedido guardado = detallePedidoRepository.save(detalle);
        recalcularPedido(pedido);
        return convertir(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> listarProductos(Long idPedido) {
        obtenerPedido(idPedido);
        return detallePedidoRepository.findByPedido_IdPedidoOrderByIdDetalleAsc(idPedido)
                .stream().map(this::convertir).toList();
    }

    @Override
    public DetallePedidoResponse actualizarCantidad(
            Long idPedido, Long idDetalle, ActualizarCantidadDetalleRequest request) {
        Pedido pedido = obtenerPedido(idPedido);
        validarEditable(pedido);
        DetallePedido detalle = obtenerDetalle(idPedido, idDetalle);
        validarDisponibilidad(detalle.getProducto(), request.getCantidad());
        detalle.setCantidad(request.getCantidad());
        detalle.setSubtotal(calcularSubtotal(detalle.getPrecioUnitario(), request.getCantidad()));
        DetallePedido actualizado = detallePedidoRepository.save(detalle);
        recalcularPedido(pedido);
        return convertir(actualizado);
    }

    @Override
    public void eliminarProducto(Long idPedido, Long idDetalle) {
        Pedido pedido = obtenerPedido(idPedido);
        validarEditable(pedido);
        DetallePedido detalle = obtenerDetalle(idPedido, idDetalle);
        detallePedidoRepository.delete(detalle);
        recalcularPedido(pedido);
    }

    private void validarEditable(Pedido pedido) {
        if (!PENDIENTE.equalsIgnoreCase(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException(
                    "Los productos solo pueden modificarse mientras el pedido esté PENDIENTE");
        }
    }

    private void validarDisponibilidad(Producto producto, int cantidad) {
        if (!ACTIVO.equalsIgnoreCase(producto.getEstado())) {
            throw new IllegalArgumentException("El producto no está activo");
        }
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getNombre());
        }
    }

    private void recalcularPedido(Pedido pedido) {
        BigDecimal subtotal = detallePedidoRepository.findByPedido_IdPedidoOrderByIdDetalleAsc(pedido.getIdPedido())
                .stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setSubtotal(subtotal);
        pedido.setTotal(subtotal.add(pedido.getCostoEnvio()));
        pedidoRepository.save(pedido);
    }

    private BigDecimal calcularSubtotal(BigDecimal precio, int cantidad) {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    private Pedido obtenerPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el pedido con ID " + idPedido));
    }

    private Producto obtenerProducto(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el producto con ID " + idProducto));
    }

    private DetallePedido obtenerDetalle(Long idPedido, Long idDetalle) {
        return detallePedidoRepository.findById(idDetalle)
                .filter(detalle -> detalle.getPedido().getIdPedido().equals(idPedido))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el detalle dentro del pedido indicado"));
    }

    private DetallePedidoResponse convertir(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getIdDetalle(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal());
    }
}
