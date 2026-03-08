package com.techshop.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrdenTest {

    private Orden orden;

    @BeforeEach
    void setUp() {
        orden = new Orden(1L, "PENDIENTE", new BigDecimal("1500.00"));
    }

    @Test
    void constructorConParametros_asignaValores() {
        assertEquals(1L, orden.getUsuarioId());
        assertEquals("PENDIENTE", orden.getEstado());
        assertEquals(new BigDecimal("1500.00"), orden.getTotal());
        assertNotNull(orden.getFechaCreacion());
    }

    @Test
    void constructorVacio_creaInstancia() {
        Orden ordenVacia = new Orden();
        assertNotNull(ordenVacia);
        assertNull(ordenVacia.getId());
        assertNull(ordenVacia.getUsuarioId());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        orden.setId(10L);
        orden.setUsuarioId(2L);
        orden.setEstado("CONFIRMADA");
        orden.setTotal(new BigDecimal("2000.00"));
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 1, 10, 0);
        orden.setFechaCreacion(fecha);

        assertEquals(10L, orden.getId());
        assertEquals(2L, orden.getUsuarioId());
        assertEquals("CONFIRMADA", orden.getEstado());
        assertEquals(new BigDecimal("2000.00"), orden.getTotal());
        assertEquals(fecha, orden.getFechaCreacion());
    }

    @Test
    void agregarDetalle_agregaYAsignaRelacion() {
        OrdenDetalle detalle = new OrdenDetalle("prod1", "Laptop", 2,
                new BigDecimal("750.00"), new BigDecimal("1500.00"));

        orden.agregarDetalle(detalle);

        assertEquals(1, orden.getDetalles().size());
        assertSame(orden, detalle.getOrden());
    }

    @Test
    void agregarDetalle_multiples_mantieneOrden() {
        OrdenDetalle detalle1 = new OrdenDetalle("prod1", "Laptop", 1,
                new BigDecimal("1000.00"), new BigDecimal("1000.00"));
        OrdenDetalle detalle2 = new OrdenDetalle("prod2", "Mouse", 2,
                new BigDecimal("250.00"), new BigDecimal("500.00"));

        orden.agregarDetalle(detalle1);
        orden.agregarDetalle(detalle2);

        assertEquals(2, orden.getDetalles().size());
        assertSame(orden, detalle1.getOrden());
        assertSame(orden, detalle2.getOrden());
    }

    @Test
    void setDetalles_reemplazaLista() {
        OrdenDetalle detalle = new OrdenDetalle("prod1", "Laptop", 1,
                new BigDecimal("1000.00"), new BigDecimal("1000.00"));
        orden.agregarDetalle(detalle);

        orden.setDetalles(new java.util.ArrayList<>());

        assertTrue(orden.getDetalles().isEmpty());
    }
}
