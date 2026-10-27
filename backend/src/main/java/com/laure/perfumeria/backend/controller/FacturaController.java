package com.laure.perfumeria.backend.controller;

import com.laure.perfumeria.backend.service.facturas.FacturaPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaPdfService facturaPdfService;

    @GetMapping("/venta/{idVenta}/pdf")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Long idVenta, Authentication authentication) {
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        byte[] pdf = facturaPdfService.generar(idVenta, authentication.getName(), administrador);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("factura-venta-" + idVenta + ".pdf")
                .build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
