package com.tienda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EJERCICIO 5 - Item dentro de una orden.
 *
 * Representa la linea de detalle: que producto, cuantas unidades
 * y a que precio unitario se compro en ese momento.
 * Se guarda el precio en el item para conservar el precio historico
 * (el precio del producto puede cambiar despues).
 */
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    /** Precio en el momento de la compra, independiente de cambios futuros. */
    @Column(nullable = false)
    private Double unitPrice;

    public Double getSubtotal() {
        return unitPrice * quantity;
    }
}

