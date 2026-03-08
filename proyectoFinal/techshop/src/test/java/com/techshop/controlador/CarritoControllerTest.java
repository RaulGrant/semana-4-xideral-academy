package com.techshop.controlador;

import com.techshop.modelo.CarritoItem;
import com.techshop.servicio.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    @Test
    void agregar_itemValido_devuelve201() throws Exception {
        CarritoItem item = new CarritoItem(1L, "prod-1", "Laptop HP",
                new BigDecimal("15999.99"), 2);
        item.setId(10L);
        when(carritoService.agregarItem(1L, "prod-1", 2)).thenReturn(item);

        mockMvc.perform(post("/api/carrito")
                        .param("usuarioId", "1")
                        .param("productoId", "prod-1")
                        .param("cantidad", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombreProducto", is("Laptop HP")))
                .andExpect(jsonPath("$.cantidad", is(2)));
    }

    @Test
    void agregar_usuarioNoExiste_devuelve400() throws Exception {
        when(carritoService.agregarItem(99L, "prod-1", 1))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado con id: 99"));

        mockMvc.perform(post("/api/carrito")
                        .param("usuarioId", "99")
                        .param("productoId", "prod-1")
                        .param("cantidad", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Usuario no encontrado")));
    }

    @Test
    void agregar_productoNoExiste_devuelve400() throws Exception {
        when(carritoService.agregarItem(1L, "prod-999", 1))
                .thenThrow(new IllegalArgumentException("Producto no encontrado con id: prod-999"));

        mockMvc.perform(post("/api/carrito")
                        .param("usuarioId", "1")
                        .param("productoId", "prod-999")
                        .param("cantidad", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Producto no encontrado")));
    }

    @Test
    void verCarrito_conItems_devuelve200() throws Exception {
        CarritoItem item1 = new CarritoItem(1L, "prod-1", "Laptop", new BigDecimal("15999.99"), 1);
        item1.setId(10L);
        CarritoItem item2 = new CarritoItem(1L, "prod-2", "Mouse", new BigDecimal("299.99"), 2);
        item2.setId(11L);
        when(carritoService.obtenerCarrito(1L)).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/api/carrito").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreProducto", is("Laptop")))
                .andExpect(jsonPath("$[1].nombreProducto", is("Mouse")));
    }

    @Test
    void verCarrito_vacio_devuelve200() throws Exception {
        when(carritoService.obtenerCarrito(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/carrito").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void eliminar_itemExistente_devuelve204() throws Exception {
        doNothing().when(carritoService).eliminarItem(10L);

        mockMvc.perform(delete("/api/carrito/10"))
                .andExpect(status().isNoContent());

        verify(carritoService).eliminarItem(10L);
    }

    @Test
    void eliminar_itemNoExistente_devuelve400() throws Exception {
        doThrow(new IllegalArgumentException("Item de carrito no encontrado con id: 99"))
                .when(carritoService).eliminarItem(99L);

        mockMvc.perform(delete("/api/carrito/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Item de carrito no encontrado")));
    }
}
