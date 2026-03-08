package com.techshop.controlador;

import com.techshop.modelo.Categoria;
import com.techshop.servicio.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para gestionar categorias de productos (RF011).
 * Expone operaciones CRUD consumidas por el administrador de la tienda.
 * La documentacion interactiva de cada endpoint se genera via Swagger (@Operation).
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Gestion de categorias de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorias")
    public ResponseEntity<List<Categoria>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoria por ID")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable String id) {
        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva categoria")
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        Categoria nueva = categoriaService.crear(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoria existente")
    public ResponseEntity<Categoria> actualizar(@PathVariable String id,
                                                @Valid @RequestBody Categoria categoria) {
        Categoria actualizada = categoriaService.actualizar(id, categoria);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoria")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Captura errores de validacion del servicio y responde con 400 Bad Request */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
