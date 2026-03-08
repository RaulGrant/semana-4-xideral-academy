package com.techshop.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.modelo.Categoria;
import com.techshop.servicio.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Laptops", "Computadoras portatiles",
                Arrays.asList("ram", "procesador"));
        categoria.setId("abc123");
    }

    @Test
    void listarTodas_devuelve200ConLista() throws Exception {
        Categoria otra = new Categoria("Smartphones", "Telefonos", List.of("pantalla"));
        otra.setId("def456");
        when(categoriaService.listarTodas()).thenReturn(Arrays.asList(categoria, otra));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Laptops")))
                .andExpect(jsonPath("$[1].nombre", is("Smartphones")));
    }

    @Test
    void buscarPorId_existente_devuelve200() throws Exception {
        when(categoriaService.buscarPorId("abc123")).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Laptops")))
                .andExpect(jsonPath("$.descripcion", is("Computadoras portatiles")));
    }

    @Test
    void buscarPorId_noExistente_devuelve404() throws Exception {
        when(categoriaService.buscarPorId("xyz")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_categoriaValida_devuelve201() throws Exception {
        when(categoriaService.crear(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.nombre", is("Laptops")));
    }

    @Test
    void crear_nombreVacio_devuelve400() throws Exception {
        Categoria sinNombre = new Categoria("", "Sin nombre", List.of());

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinNombre)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreDuplicado_devuelve400() throws Exception {
        when(categoriaService.crear(any(Categoria.class)))
                .thenThrow(new IllegalArgumentException("Ya existe una categoria con el nombre: Laptops"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ya existe")));
    }

    @Test
    void actualizar_existente_devuelve200() throws Exception {
        Categoria actualizada = new Categoria("Laptops Gaming", "Para gamers", List.of("gpu"));
        actualizada.setId("abc123");
        when(categoriaService.actualizar(eq("abc123"), any(Categoria.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/categorias/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Laptops Gaming")));
    }

    @Test
    void actualizar_noExistente_devuelve400() throws Exception {
        when(categoriaService.actualizar(eq("xyz"), any(Categoria.class)))
                .thenThrow(new IllegalArgumentException("Categoria no encontrada con id: xyz"));

        mockMvc.perform(put("/api/categorias/xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_existente_devuelve204() throws Exception {
        doNothing().when(categoriaService).eliminar("abc123");

        mockMvc.perform(delete("/api/categorias/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noExistente_devuelve400() throws Exception {
        doThrow(new IllegalArgumentException("Categoria no encontrada"))
                .when(categoriaService).eliminar("xyz");

        mockMvc.perform(delete("/api/categorias/xyz"))
                .andExpect(status().isBadRequest());
    }

    // --- Casos edge adicionales ---

    @Test
    void listarTodas_listaVacia_devuelve200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void actualizar_conNombreVacio_devuelve400() throws Exception {
        // @Valid debe rechazar nombre vacio (@NotBlank)
        Categoria sinNombre = new Categoria("", "Descripcion", List.of());

        mockMvc.perform(put("/api/categorias/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinNombre)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_existente_verificaNoContent() throws Exception {
        doNothing().when(categoriaService).eliminar("abc123");

        mockMvc.perform(delete("/api/categorias/abc123"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(categoriaService).eliminar("abc123");
    }

    @Test
    void buscarPorId_verificaAtributos() throws Exception {
        when(categoriaService.buscarPorId("abc123")).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("abc123")))
                .andExpect(jsonPath("$.atributos", hasSize(2)))
                .andExpect(jsonPath("$.atributos[0]", is("ram")));
    }

    @Test
    void crear_sinBody_devuelve400() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
