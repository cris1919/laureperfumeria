package com.laure.perfumeria.backend.service.movimientosInventario;

import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioRequest;
import com.laure.perfumeria.backend.model.movimientosInventario.dto.MovimientoInventarioResponse;
import com.laure.perfumeria.backend.model.productos.Producto;
import com.laure.perfumeria.backend.model.usuarios.Usuario;
import com.laure.perfumeria.backend.repository.MovimientoInventarioRepository;
import com.laure.perfumeria.backend.repository.ProductoRepository;
import com.laure.perfumeria.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceImplTest {

    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private MovimientoInventarioServiceImpl movimientoService;

    @Test
    void registrarEntradaDebeIncrementarStock() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Perfume");
        producto.setStock(3);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MovimientoInventarioResponse response = movimientoService.registrarEntrada(
                new MovimientoInventarioRequest(1L, 2L, 5, "Compra", "OC-1"));

        assertEquals(8, producto.getStock());
        assertEquals(8, response.getStockActual());
    }

    @Test
    void registrarSalidaNoDebePermitirStockNegativo() {
        Producto producto = new Producto();
        producto.setStock(2);
        Usuario usuario = new Usuario();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class, () -> movimientoService.registrarSalida(
                new MovimientoInventarioRequest(1L, 2L, 3, "Salida", null)));
    }
}
