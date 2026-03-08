package com.tienda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * EJERCICIO 5 - Entidad Cliente.
 *
 * Conceptos clave:
 * - @OneToMany: relacion uno-a-muchos con Order. Un cliente puede tener
 *   muchos pedidos. mappedBy indica que Order es el dueno de la relacion
 *   (tiene la FK en su tabla).
 * - CascadeType.ALL: operaciones (persist, merge, remove) se propagan
 *   automaticamente al hijo.
 * - FetchType.LAZY: las ordenes NO se cargan de la BD hasta que se accede
 *   explicitamente a la lista. Evita consultas innecesarias (N+1).
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * @OneToMany: un cliente tiene muchos pedidos.
     * mappedBy = "customer" apunta al campo 'customer' de la clase Order,
     * indicando que Order tiene la clave foranea (customer_id).
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

