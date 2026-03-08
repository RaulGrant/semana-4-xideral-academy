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
 * EJERCICIO 5 - Entidad Orden de compra.
 *
 * Conceptos clave:
 * - @ManyToOne: muchos pedidos pueden pertenecer a un mismo cliente.
 *   @JoinColumn define el nombre de la columna FK en esta tabla.
 * - @OneToMany con cascade ALL: los items de la orden se crean/eliminan
 *   junto con la orden (orphanRemoval = true limpia huerfanos).
 * - El total se calcula en la capa de servicio y se persiste para evitar
 *   recalcular en cada lectura.
 */
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne: la FK customer_id vive en la tabla orders.
     * FetchType.LAZY: no carga el cliente hasta que se accede explicitamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * @OneToMany con orphanRemoval: si un item se elimina de la lista,
     * tambien se elimina de la base de datos automaticamente.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

