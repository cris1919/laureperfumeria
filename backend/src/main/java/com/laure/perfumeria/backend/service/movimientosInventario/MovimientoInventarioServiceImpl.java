package com.laure.perfumeria.backend.service.movimientosInventario;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.movimientosInventario.MovimientoInventario;
import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioRequest;
import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioResponse;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.repository.MovimientoInventarioRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private static final String ENTRADA = "ENTRADA";
    private static final String SALIDA = "SALIDA";
    private static final String AJUSTE_POSITIVO = "AJUSTE_POSITIVO";
    private static final String AJUSTE_NEGATIVO = "AJUSTE_NEGATIVO";

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public MovimientoInventarioResponse registrarEntrada(MovimientoInventarioRequest request) {
        return registrar(request, ENTRADA);
    }

    @Override
    public MovimientoInventarioResponse registrarSalida(MovimientoInventarioRequest request) {
        return registrar(request, SALIDA);
    }

    @Override
    public MovimientoInventarioResponse registrarAjustePositivo(MovimientoInventarioRequest request) {
        return registrar(request, AJUSTE_POSITIVO);
    }

    @Override
    public MovimientoInventarioResponse registrarAjusteNegativo(MovimientoInventarioRequest request) {
        return registrar(request, AJUSTE_NEGATIVO);
    }

    private MovimientoInventarioResponse registrar(
            MovimientoInventarioRequest request, String tipoMovimiento) {
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el producto con ID " + request.getIdProducto()));
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID " + request.getIdUsuario()));

        int variacion = switch (tipoMovimiento) {
            case ENTRADA, AJUSTE_POSITIVO -> request.getCantidad();
            case SALIDA, AJUSTE_NEGATIVO -> -request.getCantidad();
            default -> throw new IllegalArgumentException("Tipo de movimiento no válido");
        };
        int stockNuevo = producto.getStock() + variacion;
        if (stockNuevo < 0) {
            throw new IllegalArgumentException("El movimiento no puede dejar el stock por debajo de cero");
        }

        producto.setStock(stockNuevo);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setReferencia(request.getReferencia());
        return convertir(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorProducto(Long idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new RecursoNoEncontradoException("No se encontró el producto con ID " + idProducto);
        }
        return movimientoRepository.findByProducto_IdProductoOrderByFechaMovimientoDesc(idProducto)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorFecha(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.of(2999, 12, 31, 23, 59, 59) : hasta.atTime(LocalTime.MAX);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        return movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(inicio, fin)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorTipo(String tipoMovimiento) {
        return movimientoRepository.findByTipoMovimientoIgnoreCaseOrderByFechaMovimientoDesc(
                        tipoMovimiento.trim().toUpperCase())
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarHistorial() {
        return movimientoRepository.findAllByOrderByFechaMovimientoDesc()
                .stream().map(this::convertir).toList();
    }

    private MovimientoInventarioResponse convertir(MovimientoInventario movimiento) {
        return new MovimientoInventarioResponse(
                movimiento.getIdMovimiento(),
                movimiento.getProducto().getIdProducto(),
                movimiento.getProducto().getNombre(),
                movimiento.getUsuario().getIdUsuario(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad(),
                movimiento.getProducto().getStock(),
                movimiento.getFechaMovimiento(),
                movimiento.getMotivo(),
                movimiento.getReferencia());
    }
}
