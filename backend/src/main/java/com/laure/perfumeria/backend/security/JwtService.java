package com.laure.perfumeria.backend.security;

import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generarToken(Cliente cliente) {
        return generarToken(cliente.getCorreo(), cliente.getIdCliente(), "CLIENTE");
    }

    public String generarToken(Usuario usuario) {
        return generarToken(usuario.getCorreo(), usuario.getIdUsuario(), usuario.getRol());
    }

    private String generarToken(String subject, Long id, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(subject)
                .claim("id", id)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(obtenerClave())
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esTokenValido(String token, String correo) {
        return correo.equals(extraerCorreo(token)) && !estaExpirado(token);
    }

    public String extraerRol(String token) {
        return extraerClaim(token, claims -> claims.get("rol", String.class));
    }

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
