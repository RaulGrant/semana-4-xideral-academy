package com.techshop.controlador;

import com.techshop.modelo.Producto;
import com.techshop.servicio.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para el CRUD de productos del catalogo (RF009).
 * Tambien expone endpoints de consulta usados por el cliente (RF001, RF002, RF003).
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catalogo de productos de la tienda")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Lista todos los productos o filtra por categoria si se proporciona el query param.
     * GET /api/productos          → todos (RF001)
     * GET /api/productos?categoria=Laptops → filtrados (RF002)
     */
    @GetMapping
    @Operation(summary = "Listar productos, opcionalmente filtrados por categoria")
    public ResponseEntity<List<Producto>> listar(
            @Parameter(description = "Nombre de la categoria para filtrar (ej: Laptops, Smartphones)")
            @RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un producto por ID")
    public ResponseEntity<Producto> buscarPorId(@PathVariable String id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        Producto nuevo = productoService.crear(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente")
    public ResponseEntity<Producto> actualizar(@PathVariable String id,
                                               @Valid @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizar(id, producto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Captura errores de validacion del servicio y responde con 400 Bad Request */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
