package com.laure.perfumeria.backend.service.clientes;

import com.laure.perfumeria.backend.model.clientes.Cliente;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteRegistroRequest;
import com.laure.perfumeria.backend.model.clientes.dto.ClienteResponse;
import com.laure.perfumeria.backend.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private ClienteServiceImpl clienteService;

    @Test
    void registrarDebeGuardarClienteActivoYConPasswordCodificada() {
        when(clienteRepository.existsByCorreoIgnoreCase("cliente@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCliente(1L);
            return cliente;
        });

        ClienteResponse response = clienteService.registrar(new ClienteRegistroRequest(
                "Ana", "Pérez", "cliente@test.com", "secret123", "999999999", "Lima"));

        assertEquals("ACTIVO", response.getEstado());
        assertEquals("cliente@test.com", response.getCorreo());
    }
}
