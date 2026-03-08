package com.tienda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EJERCICIO 1 - Entidad JPA que representa un producto de la tienda.
 *
 * Conceptos clave:
 * - @Entity: marca la clase como entidad gestionada por JPA/Hibernate.
 * - @Table: permite personalizar el nombre de la tabla en la BD.
 * - @Id + @GeneratedValue: define la clave primaria con auto-incremento.
 * - @Column: permite configurar restricciones de columna (nullable, length).
 * - @Enumerated(EnumType.STRING): persiste el nombre del enum como texto.
 * - @PrePersist: callback que se ejecuta antes de insertar un nuevo registro.
 * - Lombok @Data: genera getters, setters, equals, hashCode y toString.
 * - Lombok @Builder: patron builder para construccion fluida de objetos.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double price;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Category category;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * @PrePersist garantiza que createdAt se asigne automaticamente
     * justo antes de que JPA ejecute el INSERT en la base de datos.
     */
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

