package com.laure.perfumeria.backend.service.checkout;

import com.laure.perfumeria.backend.model.checkout.dto.CheckoutResponse;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;

public interface CheckoutService {
    CheckoutResponse realizar(PedidoRequest request, String correoAutenticado);
}
