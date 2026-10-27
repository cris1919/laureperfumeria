package com.laure.perfumeria.backend.service.ventas;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.detalle_venta.DetalleVenta;
import com.laure.perfumeria.backend.model.ventas.dto.DetalleVentaResponse;
import com.laure.perfumeria.backend.repository.DetalleVentaRepository;
import com.laure.perfumeria.backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;

    @Override
    public List<DetalleVentaResponse> listarPorVenta(Long idVenta) {
        if (!ventaRepository.existsById(idVenta)) {
            throw new RecursoNoEncontradoException("No se encontró la venta con ID " + idVenta);
        }
        return detalleVentaRepository.findByVenta_IdVentaOrderByIdDetalleVentaAsc(idVenta)
                .stream().map(this::convertir).toList();
    }

    private DetalleVentaResponse convertir(DetalleVenta detalle) {
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
