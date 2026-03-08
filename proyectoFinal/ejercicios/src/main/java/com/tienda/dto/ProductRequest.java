package com.tienda.dto;

import com.tienda.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * EJERCICIO 2 - DTO de entrada para crear o actualizar un producto.
 *
 * Por que usar DTOs en lugar de la entidad directamente:
 * - Desacopla el contrato de la API del modelo de base de datos.
 * - Permite validar datos de entrada sin contaminar la entidad JPA.
 * - Si la entidad cambia internamente, el contrato de la API puede mantenerse estable.
 *
 * record de Java 16+: clase inmutable con constructor, getters, equals, hashCode
 * y toString generados automaticamente por el compilador.
 */
public record ProductRequest(

        @NotBlank(message = "El nombre no puede estar vacio")
        String name,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        Double price,

        String description,

        Category category,

        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock
) {}

