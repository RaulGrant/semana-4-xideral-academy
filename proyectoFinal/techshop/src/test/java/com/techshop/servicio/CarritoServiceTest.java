package com.techshop.servicio;

import com.techshop.modelo.CarritoItem;
import com.techshop.modelo.Producto;
import com.techshop.repositorio.CarritoItemRepository;
import com.techshop.repositorio.ProductoRepository;
import com.techshop.repositorio.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarritoService carritoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("Laptop HP", "Laptop potente", new BigDecimal("15999.99"),
                10, null, "Laptops", null);
        producto.setId("prod-1");
    }

    @Test
    void agregarItem_productoNuevo_creaItem() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findById("prod-1")).thenReturn(Optional.of(producto));
        when(carritoItemRepository.findByUsuarioIdAndProductoId(1L, "prod-1"))
                .thenReturn(Optional.empty());
        when(carritoItemRepository.save(any(CarritoItem.class)))
                .thenAnswer(i -> i.getArgument(0));

        CarritoItem resultado = carritoService.agregarItem(1L, "prod-1", 2);

        assertEquals(1L, resultado.getUsuarioId());
        assertEquals("prod-1", resultado.getProductoId());
        assertEquals("Laptop HP", resultado.getNombreProducto());
        assertEquals(new BigDecimal("15999.99"), resultado.getPrecioUnitario());
        assertEquals(2, resultado.getCantidad());
        verify(carritoItemRepository).save(any(CarritoItem.class));
    }

    @Test
    void agregarItem_productoExistente_sumaCantidad() {
        CarritoItem existente = new CarritoItem(1L, "prod-1", "Laptop HP",
                new BigDecimal("15999.99"), 1);
        existente.setId(10L);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findById("prod-1")).thenReturn(Optional.of(producto));
        when(carritoItemRepository.findByUsuarioIdAndProductoId(1L, "prod-1"))
                .thenReturn(Optional.of(existente));
        when(carritoItemRepository.save(any(CarritoItem.class)))
                .thenAnswer(i -> i.getArgument(0));

        CarritoItem resultado = carritoService.agregarItem(1L, "prod-1", 3);

        assertEquals(4, resultado.getCantidad());
        verify(carritoItemRepository).save(existente);
    }

    @Test
    void agregarItem_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarItem(99L, "prod-1", 1));

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        verify(carritoItemRepository, never()).save(any());
    }

    @Test
    void agregarItem_productoNoExiste_lanzaExcepcion() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.findById("prod-999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarItem(1L, "prod-999", 1));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(carritoItemRepository, never()).save(any());
    }

    @Test
    void eliminarItem_existente_eliminaCorrectamente() {
        when(carritoItemRepository.existsById(10L)).thenReturn(true);

        carritoService.eliminarItem(10L);

        verify(carritoItemRepository).deleteById(10L);
    }

    @Test
    void eliminarItem_noExistente_lanzaExcepcion() {
        when(carritoItemRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.eliminarItem(99L));

        assertTrue(ex.getMessage().contains("Item de carrito no encontrado"));
    }

    @Test
    void obtenerCarrito_devuelveItems() {
        CarritoItem item1 = new CarritoItem(1L, "prod-1", "Laptop", new BigDecimal("15999.99"), 1);
        CarritoItem item2 = new CarritoItem(1L, "prod-2", "Mouse", new BigDecimal("299.99"), 2);
        when(carritoItemRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(item1, item2));

        List<CarritoItem> resultado = carritoService.obtenerCarrito(1L);

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerCarrito_vacio_devuelveListaVacia() {
        when(carritoItemRepository.findByUsuarioId(1L)).thenReturn(List.of());

        List<CarritoItem> resultado = carritoService.obtenerCarrito(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void vaciarCarrito_eliminaTodosLosItems() {
        carritoService.vaciarCarrito(1L);

        verify(carritoItemRepository).deleteByUsuarioId(1L);
    }
}
