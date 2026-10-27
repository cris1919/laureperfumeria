package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.model.checkout.dto.CheckoutResponse;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.service.checkout.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> realizar(@Valid @RequestBody PedidoRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(checkoutService.realizar(request, authentication.getName()));
    }
}
