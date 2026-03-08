package com.techshop.controlador;

import com.techshop.modelo.CarritoItem;
import com.techshop.servicio.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para la gestion del carrito de compras (RF004, RF005, RF006).
 * Permite agregar productos al carrito, ver su contenido y eliminar items.
 * Valida la existencia del usuario (MySQL) y del producto (MongoDB).
 */
@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestion del carrito de compras")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    @Operation(summary = "Agregar un producto al carrito")
    public ResponseEntity<CarritoItem> agregar(
            @Parameter(description = "ID del usuario dueno del carrito") @RequestParam Long usuarioId,
            @Parameter(description = "ID del producto en MongoDB") @RequestParam String productoId,
            @Parameter(description = "Cantidad a agregar (default: 1)") @RequestParam(defaultValue = "1") int cantidad) {
        CarritoItem item = carritoService.agregarItem(usuarioId, productoId, cantidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping
    @Operation(summary = "Ver el carrito de un usuario")
    public ResponseEntity<List<CarritoItem>> verCarrito(
            @Parameter(description = "ID del usuario para consultar su carrito") @RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Eliminar un item del carrito")
    public ResponseEntity<Void> eliminar(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
