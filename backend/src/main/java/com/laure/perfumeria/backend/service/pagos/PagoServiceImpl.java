package com.laure.perfumeria.backend.service.pagos;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.pagos.Pago;
import com.laure.perfumeria.backend.model.pagos.dto.PagoContraEntregaRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoResponse;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.repository.PagoRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String CONFIRMADO = "CONFIRMADO";
    private static final String RECHAZADO = "RECHAZADO";
    private static final String FALLIDO = "FALLIDO";
    private static final String CONTRA_ENTREGA = "CONTRA_ENTREGA";

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    public PagoResponse registrar(PagoRequest request) {
        Pedido pedido = obtenerPedido(request.getIdPedido());
        String metodo = request.getMetodoPago().trim().toUpperCase();
        if (CONTRA_ENTREGA.equals(metodo)) {
            throw new IllegalArgumentException(
                    "Los pagos CONTRA_ENTREGA deben registrarse mediante /contra-entrega");
        }
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setFechaPago(null);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(metodo);
        pago.setEstadoPago(PENDIENTE);
        pago.setReferencia(request.getReferencia());
        pago.setObservaciones(request.getObservaciones());
        pago.setFechaRegistro(LocalDateTime.now());
        return convertir(pagoRepository.save(pago));
    }

    @Override
    public PagoResponse registrarContraEntrega(PagoContraEntregaRequest request) {
        Pedido pedido = obtenerPedido(request.getIdPedido());
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setMetodoPago(CONTRA_ENTREGA);
        pago.setEstadoPago(PENDIENTE);
        pago.setReferencia(request.getReferencia());
        pago.setObservaciones(request.getObservaciones());
        pago.setFechaRegistro(LocalDateTime.now());
        return convertir(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorPedido(Long idPedido) {
        obtenerPedido(idPedido);
        return pagoRepository.findByPedido_IdPedidoOrderByFechaRegistroDesc(idPedido)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse buscarPorId(Long id) {
        return convertir(obtenerPago(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPendientes() {
        return pagoRepository.findByEstadoPagoIgnoreCaseOrderByFechaRegistroDesc(PENDIENTE)
                .stream().map(this::convertir).toList();
    }

    @Override
    public PagoResponse confirmar(Long id) {
        return convertir(cambiarEstado(id, CONFIRMADO));
    }

    @Override
    public PagoResponse rechazar(Long id) {
        return convertir(cambiarEstado(id, RECHAZADO));
    }

    private Pago cambiarEstado(Long id, String estado) {
        Pago pago = obtenerPago(id);
        pago.setEstadoPago(estado);
        if (CONFIRMADO.equals(estado)) {
            pago.setFechaPago(LocalDateTime.now());
        }
        return pagoRepository.save(pago);
    }

    private Pago obtenerPago(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el pago con ID " + id));
    }

    private Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el pedido con ID " + id));
    }

    private PagoResponse convertir(Pago pago) {
        return new PagoResponse(
                pago.getIdPago(),
                pago.getPedido().getIdPedido(),
                pago.getFechaPago(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstadoPago(),
                pago.getReferencia(),
                pago.getObservaciones(),
                pago.getFechaRegistro());
    }
}
