package com.techshop.controlador;

import com.techshop.modelo.Orden;
import com.techshop.modelo.OrdenDetalle;
import com.techshop.servicio.OrdenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenController.class)
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenService ordenService;

    private Orden crearOrdenDePrueba() {
        Orden orden = new Orden();
        orden.setId(100L);
        orden.setUsuarioId(1L);
        orden.setFechaCreacion(LocalDateTime.of(2026, 3, 6, 12, 0));
        orden.setEstado("PENDIENTE");
        orden.setTotal(new BigDecimal("16299.98"));

        OrdenDetalle detalle = new OrdenDetalle("prod-1", "Laptop HP", 1,
                new BigDecimal("15999.99"), new BigDecimal("15999.99"));
        orden.agregarDetalle(detalle);

        return orden;
    }

    @Test
    void crearOrden_carritoConItems_devuelve201() throws Exception {
        Orden orden = crearOrdenDePrueba();
        when(ordenService.crearOrden(1L)).thenReturn(orden);

        mockMvc.perform(post("/api/ordenes").param("usuarioId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.detalles", hasSize(1)))
                .andExpect(jsonPath("$.detalles[0].nombreProducto", is("Laptop HP")));
    }

    @Test
    void crearOrden_carritoVacio_devuelve400() throws Exception {
        when(ordenService.crearOrden(1L))
                .thenThrow(new IllegalArgumentException("El carrito esta vacio. Agrega productos antes de crear una orden."));

        mockMvc.perform(post("/api/ordenes").param("usuarioId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("carrito esta vacio")));
    }

    @Test
    void buscarPorId_existente_devuelve200() throws Exception {
        Orden orden = crearOrdenDePrueba();
        when(ordenService.buscarPorId(100L)).thenReturn(orden);

        mockMvc.perform(get("/api/ordenes/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.usuarioId", is(1)))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.detalles[0].nombreProducto", is("Laptop HP")));
    }

    @Test
    void buscarPorId_noExistente_devuelve400() throws Exception {
        when(ordenService.buscarPorId(999L))
                .thenThrow(new IllegalArgumentException("Orden no encontrada con id: 999"));

        mockMvc.perform(get("/api/ordenes/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Orden no encontrada")));
    }

    @Test
    void listarPorUsuario_conOrdenes_devuelve200() throws Exception {
        Orden o1 = crearOrdenDePrueba();
        Orden o2 = new Orden();
        o2.setId(101L);
        o2.setUsuarioId(1L);
        o2.setFechaCreacion(LocalDateTime.of(2026, 3, 6, 14, 0));
        o2.setEstado("CONFIRMADA");
        o2.setTotal(new BigDecimal("599.98"));
        when(ordenService.listarPorUsuario(1L)).thenReturn(Arrays.asList(o1, o2));

        mockMvc.perform(get("/api/ordenes").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[1].estado", is("CONFIRMADA")));
    }

    @Test
    void listarPorUsuario_sinOrdenes_devuelve200Vacio() throws Exception {
        when(ordenService.listarPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/ordenes").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
