package com.tienda.controller;

import com.tienda.dto.ProductRequest;
import com.tienda.dto.ProductResponse;
import com.tienda.model.Category;
import com.tienda.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EJERCICIOS 3 y 6 - Controlador REST para productos.
 *
 * @RestController = @Controller + @ResponseBody.
 * Indica que todos los metodos retornan JSON directamente (no vistas).
 *
 * @RequestMapping: prefijo de ruta para todos los endpoints del controlador.
 * Versionado en URL (/api/v1) es una buena practica para no romper clientes
 * al realizar cambios incompatibles en la API.
 *
 * ResponseEntity<T>: permite controlar explicitamente el codigo HTTP de respuesta
 * y los headers, ademas del cuerpo. Diferencia importante en entrevistas:
 * - return objeto solo -> Spring decide el status (200 por defecto)
 * - return ResponseEntity -> tu controlas el status, headers y cuerpo
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    /**
     * GET /api/v1/products
     * EJERCICIO 6: soporta paginacion con parametros opcionales.
     * Retorna Page<ProductResponse> con metadatos: total, pagina actual, etc.
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    /**
     * GET /api/v1/products/{id}
     * @PathVariable extrae el {id} de la URL.
     * 200 OK si existe, 404 Not Found si no (via GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * POST /api/v1/products
     * @RequestBody deserializa el JSON del body al DTO.
     * @Valid activa Bean Validation: si falla, lanza MethodArgumentNotValidException
     * que captura GlobalExceptionHandler y retorna 400.
     * 201 Created es el codigo semanticamente correcto para recursos creados.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * PUT /api/v1/products/{id}
     * Reemplaza todos los campos del producto. 200 OK con recurso actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * DELETE /api/v1/products/{id}
     * 204 No Content: exito pero sin cuerpo en la respuesta.
     * Es la convencion REST para eliminaciones exitosas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/products/search?name=laptop
     * @RequestParam extrae parametros de query string (?name=...).
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    /** GET /api/v1/products/category/{category} - Filtra por categoria. */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> byCategory(@PathVariable Category category) {
        return ResponseEntity.ok(service.findByCategory(category));
    }

    /** GET /api/v1/products/available - Solo productos con stock > 0. */
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>> available() {
        return ResponseEntity.ok(service.findAvailable());
    }

    /** GET /api/v1/products/best-sellers - Top 5 productos mas vendidos. */
    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponse>> bestSellers() {
        return ResponseEntity.ok(service.findBestSellers());
    }
}

