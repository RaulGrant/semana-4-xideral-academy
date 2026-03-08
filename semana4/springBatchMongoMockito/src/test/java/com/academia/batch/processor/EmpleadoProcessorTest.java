package com.academia.batch.processor;

import com.academia.batch.model.Empleado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del EmpleadoProcessor SIN mocks.
 *
 * El processor es una funcion pura: recibe un Empleado y retorna
 * el mismo Empleado transformado (nombre en mayusculas + bono del 10%).
 * No tiene dependencias externas, asi que no necesitamos Mockito.
 */
class EmpleadoProcessorTest {

    private EmpleadoProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new EmpleadoProcessor();
    }

    @Test
    @DisplayName("process: convierte el nombre a mayusculas")
    void process_nombreMinusculas_retornaMayusculas() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals("JUAN PEREZ", resultado.getNombre(),
                "El nombre debe estar en mayusculas");
    }

    @Test
    @DisplayName("process: calcula el bono como 10% del salario")
    void process_salario25000_bonoEs2500() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals(2500.0, resultado.getBono(),
                "El bono debe ser el 10% del salario");
    }

    @Test
    @DisplayName("process: nombre que ya esta en mayusculas no cambia")
    void process_nombreYaEnMayusculas_permaneceIgual() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("MARIA LOPEZ", "TI", 35000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals("MARIA LOPEZ", resultado.getNombre());
    }

    @Test
    @DisplayName("process: el departamento no se modifica")
    void process_departamento_noSeModifica() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals("Ventas", resultado.getDepartamento(),
                "El departamento no debe cambiar");
    }

    @Test
    @DisplayName("process: el salario original no se modifica")
    void process_salario_noSeModifica() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals(25000.0, resultado.getSalario(),
                "El salario original no debe cambiar");
    }

    @Test
    @DisplayName("process: salario cero produce bono cero")
    void process_salarioCero_bonoCero() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Pedro", "RRHH", 0.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals(0.0, resultado.getBono(), "Bono de salario 0 debe ser 0");
    }

    @Test
    @DisplayName("process: verifica todas las transformaciones a la vez")
    void process_empleadoCompleto_todasLasTransformaciones() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Carlos Garcia", "RRHH", 28000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertAll("Transformaciones del processor",
            () -> assertEquals("CARLOS GARCIA", resultado.getNombre()),
            () -> assertEquals("RRHH", resultado.getDepartamento()),
            () -> assertEquals(28000.0, resultado.getSalario()),
            () -> assertEquals(2800.0, resultado.getBono())
        );
    }

    // =====================================================================
    //  Metodo auxiliar
    // =====================================================================

    private Empleado crearEmpleado(String nombre, String departamento, double salario) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setDepartamento(departamento);
        empleado.setSalario(salario);
        return empleado;
    }
}
