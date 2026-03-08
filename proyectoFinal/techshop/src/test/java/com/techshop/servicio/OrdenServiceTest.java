package com.techshop.servicio;

import com.techshop.modelo.CarritoItem;
import com.techshop.modelo.Orden;
import com.techshop.repositorio.OrdenRepository;
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
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private OrdenService ordenService;

    @Test
    void crearOrden_carritoConItems_creaOrdenCorrectamente() {
        CarritoItem item1 = new CarritoItem(1L, "prod-1", "Laptop HP",
                new BigDecimal("15999.99"), 1);
        CarritoItem item2 = new CarritoItem(1L, "prod-2", "Mouse Gamer",
                new BigDecimal("299.99"), 2);
        when(carritoService.obtenerCarrito(1L)).thenReturn(Arrays.asList(item1, item2));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> {
            Orden o = i.getArgument(0);
            o.setId(100L);
            return o;
        });

        Orden orden = ordenService.crearOrden(1L);

        assertEquals(1L, orden.getUsuarioId());
        assertEquals("PENDIENTE", orden.getEstado());
        assertEquals(2, orden.getDetalles().size());
        // 15999.99 * 1 + 299.99 * 2 = 16599.97
        assertEquals(new BigDecimal("16599.97"), orden.getTotal());
        assertNotNull(orden.getFechaCreacion());
        verify(carritoService).vaciarCarrito(1L);
        verify(ordenRepository).save(any(Orden.class));
    }

    @Test
    void crearOrden_carritoVacio_lanzaExcepcion() {
        when(carritoService.obtenerCarrito(1L)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ordenService.crearOrden(1L));

        assertTrue(ex.getMessage().contains("carrito esta vacio"));
        verify(ordenRepository, never()).save(any());
        verify(carritoService, never()).vaciarCarrito(anyLong());
    }

    @Test
    void crearOrden_verificaDetallesConSubtotales() {
        CarritoItem item = new CarritoItem(1L, "prod-1", "Laptop",
                new BigDecimal("10000.00"), 3);
        when(carritoService.obtenerCarrito(1L)).thenReturn(List.of(item));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        Orden orden = ordenService.crearOrden(1L);

        assertEquals(1, orden.getDetalles().size());
        assertEquals(new BigDecimal("30000.00"), orden.getDetalles().get(0).getSubtotal());
        assertEquals(new BigDecimal("30000.00"), orden.getTotal());
    }

    @Test
    void buscarPorId_existente_retornaOrden() {
        Orden orden = new Orden(1L, "PENDIENTE", new BigDecimal("1000.00"));
        orden.setId(100L);
        when(ordenRepository.findById(100L)).thenReturn(Optional.of(orden));

        Orden resultado = ordenService.buscarPorId(100L);

        assertEquals(100L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(ordenRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ordenService.buscarPorId(999L));

        assertTrue(ex.getMessage().contains("Orden no encontrada"));
    }

    @Test
    void listarPorUsuario_devuelveOrdenes() {
        Orden o1 = new Orden(1L, "PENDIENTE", new BigDecimal("1000.00"));
        Orden o2 = new Orden(1L, "CONFIRMADA", new BigDecimal("2000.00"));
        when(ordenRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(o1, o2));

        List<Orden> resultado = ordenService.listarPorUsuario(1L);

        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorUsuario_sinOrdenes_devuelveListaVacia() {
        when(ordenRepository.findByUsuarioId(1L)).thenReturn(List.of());

        List<Orden> resultado = ordenService.listarPorUsuario(1L);

        assertTrue(resultado.isEmpty());
    }
}
