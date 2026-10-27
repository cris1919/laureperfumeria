package com.laure.perfumeria.backend.service.pagos;

import com.laure.perfumeria.backend.model.pagos.dto.PagoContraEntregaRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoRequest;
import com.laure.perfumeria.backend.model.pagos.dto.PagoResponse;

import java.util.List;

public interface PagoService {

    PagoResponse registrar(PagoRequest request);

    PagoResponse registrarContraEntrega(PagoContraEntregaRequest request);

    List<PagoResponse> listarPorPedido(Long idPedido);

    PagoResponse buscarPorId(Long id);

    List<PagoResponse> listarPendientes();

    PagoResponse confirmar(Long id);

    PagoResponse rechazar(Long id);

}
