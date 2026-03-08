package com.techshop.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.modelo.Producto;
import com.techshop.servicio.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("MacBook Pro", "Laptop Apple", new BigDecimal("29999.99"),
                10, "https://img.com/mac.jpg", "Laptops", Map.of("ram", "16GB"));
        producto.setId("prod1");
    }

    @Test
    void listar_sinFiltro_devuelveTodosConDatos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("MacBook Pro")));
    }

    @Test
    void buscarPorId_existente_devuelve200() throws Exception {
        when(productoService.buscarPorId("prod1")).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("MacBook Pro")))
                .andExpect(jsonPath("$.precio", is(29999.99)));
    }

    @Test
    void buscarPorId_noExistente_devuelve404() throws Exception {
        when(productoService.buscarPorId("xyz")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_conQueryParamCategoria_filtra() throws Exception {
        when(productoService.buscarPorCategoria("Laptops")).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos").param("categoria", "Laptops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoria", is("Laptops")));
    }

    @Test
    void listar_sinQueryParam_devuelveTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(productoService).listarTodos();
        verify(productoService, never()).buscarPorCategoria(any());
    }

    @Test
    void crear_productoValido_devuelve201() throws Exception {
        when(productoService.crear(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("prod1")))
                .andExpect(jsonPath("$.nombre", is("MacBook Pro")));
    }

    @Test
    void crear_sinNombre_devuelve400() throws Exception {
        Producto invalido = new Producto("", "Sin nombre", new BigDecimal("100"),
                5, null, "Laptops", null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_categoriaInexistente_devuelve400() throws Exception {
        when(productoService.crear(any(Producto.class)))
                .thenThrow(new IllegalArgumentException("La categoria no existe: Relojes"));

        Producto conCatInvalida = new Producto("Reloj", "Desc", new BigDecimal("5000"),
                3, null, "Relojes", null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conCatInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("La categoria no existe")));
    }

    @Test
    void actualizar_existente_devuelve200() throws Exception {
        Producto actualizado = new Producto("MacBook Pro M3", "Nuevo", new BigDecimal("32999"),
                8, null, "Laptops", Map.of("ram", "32GB"));
        actualizado.setId("prod1");
        when(productoService.actualizar(eq("prod1"), any(Producto.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/prod1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("MacBook Pro M3")));
    }

    @Test
    void eliminar_existente_devuelve204() throws Exception {
        doNothing().when(productoService).eliminar("prod1");

        mockMvc.perform(delete("/api/productos/prod1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noExistente_devuelve400() throws Exception {
        doThrow(new IllegalArgumentException("Producto no encontrado"))
                .when(productoService).eliminar("xyz");

        mockMvc.perform(delete("/api/productos/xyz"))
                .andExpect(status().isBadRequest());
    }

    // --- Casos edge adicionales ---

    @Test
    void listar_conCategoriaVacia_devuelveTodos() throws Exception {
        // Cubre la rama donde categoria no es null pero si esta vacia (isBlank)
        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos").param("categoria", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(productoService).listarTodos();
        verify(productoService, never()).buscarPorCategoria(any());
    }

    @Test
    void actualizar_noExistente_devuelve400() throws Exception {
        when(productoService.actualizar(eq("xyz"), any(Producto.class)))
                .thenThrow(new IllegalArgumentException("Producto no encontrado con id: xyz"));

        mockMvc.perform(put("/api/productos/xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Producto no encontrado")));
    }

    @Test
    void actualizar_categoriaInexistente_devuelve400() throws Exception {
        when(productoService.actualizar(eq("prod1"), any(Producto.class)))
                .thenThrow(new IllegalArgumentException("La categoria no existe: Relojes"));

        Producto conCatInvalida = new Producto("MacBook", "Desc", new BigDecimal("5000"),
                3, null, "Relojes", null);

        mockMvc.perform(put("/api/productos/prod1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conCatInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("La categoria no existe")));
    }

    @Test
    void crear_sinPrecio_devuelve400() throws Exception {
        // Producto sin precio (campo @NotNull)
        Producto sinPrecio = new Producto("Test", "Desc", null,
                5, null, "Laptops", null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinPrecio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_sinStock_devuelve400() throws Exception {
        // Producto sin stock (campo @NotNull)
        Producto sinStock = new Producto("Test", "Desc", new BigDecimal("100"),
                null, null, "Laptops", null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinStock)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_sinCategoria_devuelve400() throws Exception {
        // Producto sin categoria (campo @NotBlank)
        Producto sinCategoria = new Producto("Test", "Desc", new BigDecimal("100"),
                5, null, "", null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinCategoria)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_sinResultados_devuelve200ConListaVacia() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listar_porCategoriaInexistente_devuelve200ConListaVacia() throws Exception {
        when(productoService.buscarPorCategoria("Inexistente")).thenReturn(List.of());

        mockMvc.perform(get("/api/productos").param("categoria", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
