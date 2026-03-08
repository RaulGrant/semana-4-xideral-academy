package com.techshop.controlador;

import com.techshop.modelo.Orden;
import com.techshop.servicio.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para la gestion de ordenes/pedidos (RF007, RF008).
 * Permite crear una orden desde el carrito y consultar ordenes existentes.
 */
@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Ordenes", description = "Creacion y consulta de pedidos")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    @Operation(summary = "Crear una orden desde el carrito del usuario")
    public ResponseEntity<Orden> crearOrden(
            @Parameter(description = "ID del usuario cuyo carrito se convierte en orden") @RequestParam Long usuarioId) {
        Orden orden = ordenService.crearOrden(usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de una orden por ID")
    public ResponseEntity<Orden> buscarPorId(@PathVariable Long id) {
        Orden orden = ordenService.buscarPorId(id);
        return ResponseEntity.ok(orden);
    }

    @GetMapping
    @Operation(summary = "Listar ordenes de un usuario")
    public ResponseEntity<List<Orden>> listarPorUsuario(
            @Parameter(description = "ID del usuario para consultar sus ordenes") @RequestParam Long usuarioId) {
        return ResponseEntity.ok(ordenService.listarPorUsuario(usuarioId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
