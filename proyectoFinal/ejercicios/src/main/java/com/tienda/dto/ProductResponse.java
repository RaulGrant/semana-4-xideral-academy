package com.tienda.dto;

import com.tienda.model.Category;
import com.tienda.model.Product;

import java.time.LocalDateTime;

/**
 * EJERCICIO 2 - DTO de salida para exponer datos de un producto.
 *
 * Por que separar ProductRequest de ProductResponse:
 * - Request: lo que el cliente ENVIA (validaciones de entrada).
 * - Response: lo que el servidor RETORNA (puede incluir campos calculados,
 *   ocultar campos sensibles, o renombrar campos para el cliente).
 *
 * El metodo estatico fromEntity() es un patron factory que convierte
 * la entidad JPA al DTO de salida en un solo lugar.
 */
public record ProductResponse(
        Long id,
        String name,
        Double price,
        String description,
        Category category,
        Integer stock,
        LocalDateTime createdAt
) {
    /**
     * Factory method estatico: convierte una entidad Product en su DTO.
     * Centraliza el mapeo para no repetir conversion en cada controlador/servicio.
     */
    public static ProductResponse fromEntity(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getDescription(),
                p.getCategory(),
                p.getStock(),
                p.getCreatedAt()
        );
    }
}

