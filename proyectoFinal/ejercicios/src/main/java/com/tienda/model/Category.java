package com.tienda.model;

/**
 * EJERCICIO 1 - Enum de categorias de productos.
 * Usar EnumType.STRING en JPA para que se guarde el nombre legible ("ELECTRONICS")
 * en la base de datos en lugar del ordinal numerico, lo cual es mas mantenible.
 */
public enum Category {
    ELECTRONICS,
    BOOKS,
    CLOTHING,
    FOOD,
    SPORTS
}

