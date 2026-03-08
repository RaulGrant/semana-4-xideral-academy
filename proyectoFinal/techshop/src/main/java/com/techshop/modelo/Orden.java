package com.techshop.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un pedido (orden de compra) del cliente.
 *
 * <p>Cada orden pertenece a un usuario identificado por {@code usuarioId} y contiene
 * una lista de {@link OrdenDetalle} que describen los productos comprados, cantidades
 * y precios. El campo {@code estado} indica la fase del pedido: PENDIENTE al crearse,
 * CONFIRMADA cuando se procesa el pago, o CANCELADA si el cliente la revoca.</p>
 *
 * <p>Se almacena en MySQL (tabla {@code ordenes}) porque los pedidos son datos
 * transaccionales que requieren integridad referencial y soporte ACID.</p>
 */
@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Estado del pedido: PENDIENTE, CONFIRMADA o CANCELADA.
     */
    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private String estado;

    @NotNull(message = "El total es obligatorio")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Lineas de detalle del pedido. Se gestionan en cascada: al persistir o eliminar
     * la orden, sus detalles se persisten o eliminan automaticamente.
     */
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenDetalle> detalles = new ArrayList<>();

    public Orden() {
    }

    public Orden(Long usuarioId, String estado, BigDecimal total) {
        this.usuarioId = usuarioId;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = estado;
        this.total = total;
    }

    // --- Metodo de conveniencia para manejar la relacion bidireccional ---

    /**
     * Agrega una linea de detalle al pedido manteniendo la relacion bidireccional.
     *
     * @param detalle linea de detalle a agregar
     */
    public void agregarDetalle(OrdenDetalle detalle) {
        detalles.add(detalle);
        detalle.setOrden(this);
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrdenDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenDetalle> detalles) {
        this.detalles = detalles;
    }
}
