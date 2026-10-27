package com.laure.perfumeria.backend.service.clientes;

import com.laure.perfumeria.backend.exception.RecursoNoEncontradoException;
import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.clientes.dto.CambiarPasswordRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteRegistroRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteResponse;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteUpdateRequest;
import com.laure.perfumeria.backend.model.clientes.dto.EstadoClienteRequest;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClienteResponse registrar(ClienteRegistroRequest request) {
        if (clienteRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo");
        }

        Cliente cliente = new Cliente();
        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setCorreo(request.getCorreo());
        cliente.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setEstado(ACTIVO);
        cliente.setFechaRegistro(LocalDateTime.now());
        return convertir(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse consultarPerfil(Long id, String correoAutenticado, boolean esAdministrador) {
        Cliente cliente = obtenerCliente(id);
        validarAcceso(cliente, correoAutenticado, esAdministrador);
        return convertir(cliente);
    }

    @Override
    public ClienteResponse actualizarDatos(Long id, ClienteUpdateRequest request,
                                           String correoAutenticado, boolean esAdministrador) {
        Cliente cliente = obtenerCliente(id);
        validarAcceso(cliente, correoAutenticado, esAdministrador);
        clienteRepository.findByCorreoIgnoreCase(request.getCorreo()).ifPresent(otro -> {
            if (!otro.getIdCliente().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro cliente registrado con ese correo");
            }
        });

        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        return convertir(clienteRepository.save(cliente));
    }

    @Override
    public void cambiarPassword(Long id, CambiarPasswordRequest request,
                                String correoAutenticado, boolean esAdministrador) {
        Cliente cliente = obtenerCliente(id);
        validarAcceso(cliente, correoAutenticado, esAdministrador);
        if (!passwordEncoder.matches(request.getPasswordActual(), cliente.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        cliente.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll().stream().map(this::convertir).toList();
    }

    @Override
    public ClienteResponse cambiarEstado(Long id, EstadoClienteRequest request) {
        Cliente cliente = obtenerCliente(id);
        String estado = request.getEstado().trim().toUpperCase();
        if (!ACTIVO.equals(estado) && !INACTIVO.equals(estado)) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        cliente.setEstado(estado);
        return convertir(clienteRepository.save(cliente));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el cliente con ID " + id));
    }

    private void validarAcceso(Cliente cliente, String correoAutenticado, boolean esAdministrador) {
        if (!esAdministrador && !cliente.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para acceder a este cliente");
        }
    }

    private ClienteResponse convertir(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getEstado(),
                cliente.getFechaRegistro());
    }

}
