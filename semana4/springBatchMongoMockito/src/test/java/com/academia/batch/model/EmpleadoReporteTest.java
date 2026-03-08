package com.academia.batch.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basicos del modelo EmpleadoReporte.
 *
 * EmpleadoReporte extiende a Empleado con id y salarioTotal.
 * Verificamos que todos los campos se almacenan correctamente.
 */
class EmpleadoReporteTest {

    @Test
    @DisplayName("Constructor vacio crea reporte con valores por defecto")
    void constructorVacio_sinParametros_valoresPorDefecto() {
        // Arrange & Act
        EmpleadoReporte reporte = new EmpleadoReporte();

        // Assert
        assertAll("Estado inicial del reporte",
            () -> assertNull(reporte.getId(), "id debe ser null"),
            () -> assertNull(reporte.getNombre(), "nombre debe ser null"),
            () -> assertNull(reporte.getDepartamento(), "departamento debe ser null"),
            () -> assertEquals(0.0, reporte.getSalario(), "salario debe ser 0.0"),
            () -> assertEquals(0.0, reporte.getBono(), "bono debe ser 0.0"),
            () -> assertEquals(0.0, reporte.getSalarioTotal(), "salarioTotal debe ser 0.0")
        );
    }

    @Test
    @DisplayName("setId y getId guardan y devuelven el ID")
    void setId_valorValido_retornaMismoValor() {
        // Arrange
        EmpleadoReporte reporte = new EmpleadoReporte();

        // Act
        reporte.setId("abc123");

        // Assert
        assertEquals("abc123", reporte.getId());
    }

    @Test
    @DisplayName("setSalarioTotal y getSalarioTotal guardan y devuelven el total")
    void setSalarioTotal_valorValido_retornaMismoValor() {
        // Arrange
        EmpleadoReporte reporte = new EmpleadoReporte();

        // Act
        reporte.setSalarioTotal(27500.0);

        // Assert
        assertEquals(27500.0, reporte.getSalarioTotal());
    }

    @Test
    @DisplayName("Reporte completo tiene todos sus campos correctos")
    void reporteCompleto_todasLasPropiedades_verificaConAssertAll() {
        // Arrange
        EmpleadoReporte reporte = new EmpleadoReporte();

        // Act
        reporte.setId("rpt001");
        reporte.setNombre("JUAN PEREZ");
        reporte.setDepartamento("Ventas");
        reporte.setSalario(25000.0);
        reporte.setBono(2500.0);
        reporte.setSalarioTotal(27500.0);

        // Assert
        assertAll("Propiedades del reporte",
            () -> assertEquals("rpt001", reporte.getId()),
            () -> assertEquals("JUAN PEREZ", reporte.getNombre()),
            () -> assertEquals("Ventas", reporte.getDepartamento()),
            () -> assertEquals(25000.0, reporte.getSalario()),
            () -> assertEquals(2500.0, reporte.getBono()),
            () -> assertEquals(27500.0, reporte.getSalarioTotal())
        );
    }

    @Test
    @DisplayName("toString incluye nombre, departamento, salario, bono y total")
    void toString_reporteCompleto_contieneTodasLasPropiedades() {
        // Arrange
        EmpleadoReporte reporte = new EmpleadoReporte();
        reporte.setNombre("MARIA LOPEZ");
        reporte.setDepartamento("TI");
        reporte.setSalario(35000.0);
        reporte.setBono(3500.0);
        reporte.setSalarioTotal(38500.0);

        // Act
        String texto = reporte.toString();

        // Assert
        assertAll("toString contiene los datos",
            () -> assertTrue(texto.contains("MARIA LOPEZ"), "Debe contener el nombre"),
            () -> assertTrue(texto.contains("TI"), "Debe contener el departamento"),
            () -> assertTrue(texto.contains("35000"), "Debe contener el salario"),
            () -> assertTrue(texto.contains("3500"), "Debe contener el bono"),
            () -> assertTrue(texto.contains("38500"), "Debe contener el total")
        );
    }

    @Test
    @DisplayName("salarioTotal puede ser la suma de salario + bono")
    void salarioTotal_sumaDeSalarioYBono_esConsistente() {
        // Arrange
        EmpleadoReporte reporte = new EmpleadoReporte();
        double salario = 30000.0;
        double bono = 3000.0;

        // Act
        reporte.setSalario(salario);
        reporte.setBono(bono);
        reporte.setSalarioTotal(salario + bono);

        // Assert
        assertEquals(salario + bono, reporte.getSalarioTotal(),
                "El total debe ser la suma del salario y el bono");
    }
}
