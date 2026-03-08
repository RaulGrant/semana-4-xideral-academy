package com.techshop.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.modelo.Usuario;
import com.techshop.servicio.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan Perez", "juan@ejemplo.com", "password123");
        usuario.setId(1L);
        usuario.setFechaRegistro(LocalDateTime.of(2026, 3, 6, 10, 0));
    }

    @Test
    void registrar_usuarioValido_devuelve201() throws Exception {
        when(usuarioService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Perez")))
                .andExpect(jsonPath("$.email", is("juan@ejemplo.com")));
    }

    @Test
    void registrar_emailDuplicado_devuelve400() throws Exception {
        when(usuarioService.registrar(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con el email: juan@ejemplo.com"));

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ya existe un usuario")));
    }

    @Test
    void registrar_sinNombre_devuelve400() throws Exception {
        Usuario invalido = new Usuario("", "test@ejemplo.com", "pass123");

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_emailInvalido_devuelve400() throws Exception {
        Usuario invalido = new Usuario("Juan", "no-es-email", "pass123");

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_existente_devuelve200() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Perez")))
                .andExpect(jsonPath("$.email", is("juan@ejemplo.com")));
    }

    @Test
    void buscarPorId_noExistente_devuelve400() throws Exception {
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado con id: 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Usuario no encontrado")));
    }

    @Test
    void listarTodos_devuelve200ConLista() throws Exception {
        Usuario otro = new Usuario("Maria Lopez", "maria@ejemplo.com", "pass456");
        otro.setId(2L);
        otro.setFechaRegistro(LocalDateTime.of(2026, 3, 6, 11, 0));
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuario, otro));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan Perez")))
                .andExpect(jsonPath("$[1].nombre", is("Maria Lopez")));
    }

    @Test
    void listarTodos_listaVacia_devuelve200() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
