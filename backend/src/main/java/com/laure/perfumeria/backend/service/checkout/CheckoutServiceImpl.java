package com.laure.perfumeria.backend.service.checkout;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.checkout.dto.CheckoutResponse;
import com.laure.perfumeria.backend.model.pedidos.Pedido;
import com.laure.perfumeria.backend.model.pedidos.dto.PedidoRequest;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.model.ventas.dto.RegistrarVentaRequest;
import com.laure.perfumeria.backend.model.ventas.dto.VentaResponse;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import com.laure.perfumeria.backend.repository.PedidoRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import com.laure.perfumeria.backend.service.pedidos.PedidoService;
import com.laure.perfumeria.backend.service.ventas.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private static final String ACTIVO = "ACTIVO";
    private static final String ADMIN = "ADMIN";
    private static final String CONFIRMADO = "CONFIRMADO";

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;
    private final VentaService ventaService;

    @Override
    @Transactional
    public CheckoutResponse realizar(PedidoRequest request, String correoAutenticado) {
        var cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el cliente"));
        if (!cliente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new AccessDeniedException("El pedido no pertenece al cliente autenticado");
        }

        var pedidoCreado = pedidoService.registrar(request);
        Pedido pedido = pedidoRepository.findById(pedidoCreado.getIdPedido())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el pedido creado"));
        pedido.setEstadoPedido(CONFIRMADO);
        pedidoRepository.save(pedido);

        var admin = usuarioRepository.findFirstByRolIgnoreCaseAndEstadoIgnoreCase(ADMIN, ACTIVO)
                .orElseGet(this::obtenerUsuarioSistema);
        VentaResponse venta = ventaService.registrar(new RegistrarVentaRequest(pedido.getIdPedido(), admin.getIdUsuario()));
        return new CheckoutResponse(pedido.getIdPedido(), venta.getIdVenta(), venta.getTotalVenta());
    }

    private Usuario obtenerUsuarioSistema() {
        Usuario sistema = new Usuario();
        sistema.setNombres("Sistema");
        sistema.setApellidos("Laure Perfumería");
        sistema.setCorreo("sistema@laure.local");
        sistema.setPasswordHash("!");
        sistema.setRol(ADMIN);
        sistema.setEstado(ACTIVO);
        sistema.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(sistema);
    }
}
