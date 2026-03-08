package com.techshop.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrdenDetalleTest {

    @Test
    void constructorConParametros_asignaValores() {
        OrdenDetalle detalle = new OrdenDetalle("prod1", "Laptop Gaming", 2,
                new BigDecimal("15000.00"), new BigDecimal("30000.00"));

        assertEquals("prod1", detalle.getProductoId());
        assertEquals("Laptop Gaming", detalle.getNombreProducto());
        assertEquals(2, detalle.getCantidad());
        assertEquals(new BigDecimal("15000.00"), detalle.getPrecioUnitario());
        assertEquals(new BigDecimal("30000.00"), detalle.getSubtotal());
    }

    @Test
    void constructorVacio_creaInstancia() {
        OrdenDetalle detalle = new OrdenDetalle();
        assertNotNull(detalle);
        assertNull(detalle.getId());
        assertNull(detalle.getProductoId());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        OrdenDetalle detalle = new OrdenDetalle();
        detalle.setId(5L);
        detalle.setProductoId("abc123");
        detalle.setNombreProducto("Teclado Mecanico");
        detalle.setCantidad(3);
        detalle.setPrecioUnitario(new BigDecimal("899.99"));
        detalle.setSubtotal(new BigDecimal("2699.97"));

        assertEquals(5L, detalle.getId());
        assertEquals("abc123", detalle.getProductoId());
        assertEquals("Teclado Mecanico", detalle.getNombreProducto());
        assertEquals(3, detalle.getCantidad());
        assertEquals(new BigDecimal("899.99"), detalle.getPrecioUnitario());
        assertEquals(new BigDecimal("2699.97"), detalle.getSubtotal());
    }

    @Test
    void setOrden_asignaRelacion() {
        OrdenDetalle detalle = new OrdenDetalle();
        Orden orden = new Orden(1L, "PENDIENTE", new BigDecimal("500.00"));

        detalle.setOrden(orden);

        assertSame(orden, detalle.getOrden());
    }
}
