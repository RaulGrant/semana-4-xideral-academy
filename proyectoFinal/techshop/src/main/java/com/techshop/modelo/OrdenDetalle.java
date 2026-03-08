package com.techshop.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa una linea de detalle dentro de un pedido ({@link Orden}).
 *
 * <p>Cada detalle registra un producto comprado con su cantidad, precio unitario y
 * subtotal. El campo {@code productoId} es una referencia al documento MongoDB del
 * catalogo (coleccion {@code productos}), mientras que {@code nombreProducto} se
 * desnormaliza para conservar el nombre al momento de la compra, incluso si el
 * producto se modifica o elimina posteriormente del catalogo.</p>
 *
 * <p>Se almacena en MySQL (tabla {@code orden_detalles}) junto con la orden padre,
 * manteniendo una relacion {@code @ManyToOne} con {@link Orden}.</p>
 */
@Entity
@Table(name = "orden_detalles")
public class OrdenDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Referencia al documento MongoDB del producto en el catalogo */
    @NotBlank(message = "El ID de producto es obligatorio")
    @Column(name = "producto_id", nullable = false)
    private String productoId;

    /** Nombre del producto al momento de la compra (desnormalizado) */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre_producto", nullable = false)
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    public OrdenDetalle() {
    }

    public OrdenDetalle(String productoId, String nombreProducto, Integer cantidad,
                        BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }
}
