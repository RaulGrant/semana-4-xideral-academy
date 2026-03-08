package com.academia.batch.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basicos del modelo Empleado.
 *
 * Estos tests verifican que los getters, setters y toString
 * funcionan correctamente. No se necesita mock para modelos simples.
 */
class EmpleadoTest {

    @Test
    @DisplayName("Constructor vacio crea empleado con valores por defecto")
    void constructorVacio_sinParametros_valoresPorDefecto() {
        // Arrange & Act
        Empleado empleado = new Empleado();

        // Assert
        assertAll("Estado inicial del empleado",
            () -> assertNull(empleado.getNombre(), "nombre debe ser null"),
            () -> assertNull(empleado.getDepartamento(), "departamento debe ser null"),
            () -> assertEquals(0.0, empleado.getSalario(), "salario debe ser 0.0"),
            () -> assertEquals(0.0, empleado.getBono(), "bono debe ser 0.0")
        );
    }

    @Test
    @DisplayName("setNombre y getNombre guardan y devuelven el nombre")
    void setNombre_valorValido_retornaMismoValor() {
        // Arrange
        Empleado empleado = new Empleado();

        // Act
        empleado.setNombre("Juan Perez");

        // Assert
        assertEquals("Juan Perez", empleado.getNombre());
    }

    @Test
    @DisplayName("setDepartamento y getDepartamento guardan y devuelven el departamento")
    void setDepartamento_valorValido_retornaMismoValor() {
        // Arrange
        Empleado empleado = new Empleado();

        // Act
        empleado.setDepartamento("Ventas");

        // Assert
        assertEquals("Ventas", empleado.getDepartamento());
    }

    @Test
    @DisplayName("setSalario y getSalario guardan y devuelven el salario")
    void setSalario_valorValido_retornaMismoValor() {
        // Arrange
        Empleado empleado = new Empleado();

        // Act
        empleado.setSalario(25000.0);

        // Assert
        assertEquals(25000.0, empleado.getSalario());
    }

    @Test
    @DisplayName("setBono y getBono guardan y devuelven el bono")
    void setBono_valorValido_retornaMismoValor() {
        // Arrange
        Empleado empleado = new Empleado();

        // Act
        empleado.setBono(2500.0);

        // Assert
        assertEquals(2500.0, empleado.getBono());
    }

    @Test
    @DisplayName("toString incluye nombre, departamento, salario y bono")
    void toString_empleadoCompleto_contieneTodasLasPropiedades() {
        // Arrange
        Empleado empleado = new Empleado();
        empleado.setNombre("Juan Perez");
        empleado.setDepartamento("Ventas");
        empleado.setSalario(25000.0);
        empleado.setBono(2500.0);

        // Act
        String texto = empleado.toString();

        // Assert
        assertAll("toString contiene los datos",
            () -> assertTrue(texto.contains("Juan Perez"), "Debe contener el nombre"),
            () -> assertTrue(texto.contains("Ventas"), "Debe contener el departamento"),
            () -> assertTrue(texto.contains("25000"), "Debe contener el salario"),
            () -> assertTrue(texto.contains("2500"), "Debe contener el bono")
        );
    }

    @Test
    @DisplayName("Empleado completo tiene todos sus campos correctos")
    void empleadoCompleto_todasLasPropiedades_verificaConAssertAll() {
        // Arrange
        Empleado empleado = new Empleado();

        // Act
        empleado.setNombre("Maria Lopez");
        empleado.setDepartamento("TI");
        empleado.setSalario(35000.0);
        empleado.setBono(3500.0);

        // Assert
        assertAll("Propiedades del empleado",
            () -> assertEquals("Maria Lopez", empleado.getNombre()),
            () -> assertEquals("TI", empleado.getDepartamento()),
            () -> assertEquals(35000.0, empleado.getSalario()),
            () -> assertEquals(3500.0, empleado.getBono())
        );
    }
}
