package com.tienda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.dto.ProductRequest;
import com.tienda.dto.ProductResponse;
import com.tienda.exception.GlobalExceptionHandler;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Category;
import com.tienda.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integracion web para ProductController.
 *
 * @WebMvcTest: levanta solo la capa web de Spring MVC sin base de datos.
 *   Carga el controlador especificado, filtros, convertidores JSON, etc.
 *   Es mas rapido que @SpringBootTest porque no levanta toda la aplicacion.
 *
 * MockMvc: cliente HTTP de prueba que simula peticiones HTTP sin un servidor real.
 *   Permite probar: metodo HTTP, URL, body, headers, status de respuesta y body.
 *
 * @MockBean: reemplaza el bean de Spring con un mock de Mockito en el contexto.
 *   A diferencia de @Mock, este se registra en el ApplicationContext.
 */
@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProductController - Tests de capa Web")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    private ProductResponse buildResponse(Long id, String name, Double price) {
        return new ProductResponse(id, name, price, "Desc", Category.ELECTRONICS, 10, LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/v1/products - deberia retornar 200 con pagina de productos")
    void shouldReturnPagedProducts() throws Exception {
        var page = new PageImpl<>(List.of(buildResponse(1L, "Laptop", 999.0)),
                PageRequest.of(0, 10), 1);
        when(service.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - deberia retornar 200 si existe")
    void shouldReturnProductById() throws Exception {
        when(service.findById(1L)).thenReturn(buildResponse(1L, "Laptop Pro", 1299.0));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - deberia retornar 404 si no existe")
    void shouldReturn404WhenNotFound() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Producto", 99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/products - deberia retornar 201 al crear producto valido")
    void shouldCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest("Nuevo Prod", 49.99, "Desc", Category.BOOKS, 100);
        when(service.create(any())).thenReturn(buildResponse(5L, "Nuevo Prod", 49.99));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("POST /api/v1/products - deberia retornar 400 si nombre esta vacio")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        ProductRequest invalid = new ProductRequest("", 49.99, "Desc", Category.BOOKS, 10);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - deberia retornar 204 sin cuerpo")
    void shouldDeleteAndReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }
}

