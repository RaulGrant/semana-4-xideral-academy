package com.academia.batch.processor;

import com.academia.batch.model.Empleado;
import com.academia.batch.model.EmpleadoReporte;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del ReporteProcessor SIN mocks.
 *
 * El processor transforma un Empleado en un EmpleadoReporte,
 * copiando los campos y calculando el salarioTotal (salario + bono).
 * Es una funcion pura sin dependencias externas.
 */
class ReporteProcessorTest {

    private ReporteProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ReporteProcessor();
    }

    @Test
    @DisplayName("process: copia el nombre del empleado al reporte")
    void process_nombre_seCopiAlReporte() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0, 2500.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertEquals("JUAN PEREZ", reporte.getNombre());
    }

    @Test
    @DisplayName("process: copia el departamento del empleado al reporte")
    void process_departamento_seCopiAlReporte() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0, 2500.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertEquals("Ventas", reporte.getDepartamento());
    }

    @Test
    @DisplayName("process: calcula salarioTotal como salario + bono")
    void process_salarioYBono_totalEsLaSuma() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0, 2500.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertEquals(27500.0, reporte.getSalarioTotal(),
                "salarioTotal debe ser salario + bono");
    }

    @Test
    @DisplayName("process: copia salario y bono al reporte")
    void process_salarioYBono_seCopiAnAlReporte() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("MARIA LOPEZ", "TI", 35000.0, 3500.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertAll("Salario y bono copiados",
            () -> assertEquals(35000.0, reporte.getSalario()),
            () -> assertEquals(3500.0, reporte.getBono())
        );
    }

    @Test
    @DisplayName("process: bono cero produce total igual al salario")
    void process_bonoCero_totalIgualSalario() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("PEDRO", "RRHH", 30000.0, 0.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertEquals(30000.0, reporte.getSalarioTotal(),
                "Sin bono, el total debe ser igual al salario");
    }

    @Test
    @DisplayName("process: verifica todas las propiedades del reporte")
    void process_empleadoCompleto_reporteCompletoYCorrecto() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("CARLOS GARCIA", "RRHH", 28000.0, 2800.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert
        assertAll("Reporte generado correctamente",
            () -> assertEquals("CARLOS GARCIA", reporte.getNombre()),
            () -> assertEquals("RRHH", reporte.getDepartamento()),
            () -> assertEquals(28000.0, reporte.getSalario()),
            () -> assertEquals(2800.0, reporte.getBono()),
            () -> assertEquals(30800.0, reporte.getSalarioTotal())
        );
    }

    @Test
    @DisplayName("process: el reporte es una instancia nueva (no el mismo objeto)")
    void process_empleado_retornaObjetoNuevo() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("ANA", "Ventas", 27000.0, 2700.0);

        // Act
        EmpleadoReporte reporte = processor.process(empleado);

        // Assert - el tipo de retorno es EmpleadoReporte, diferente de Empleado
        assertNotNull(reporte, "El reporte no debe ser null");
        assertNull(reporte.getId(), "El ID del reporte nuevo debe ser null");
    }

    // =====================================================================
    //  Metodo auxiliar
    // =====================================================================

    private Empleado crearEmpleado(String nombre, String departamento,
                                    double salario, double bono) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setDepartamento(departamento);
        empleado.setSalario(salario);
        empleado.setBono(bono);
        return empleado;
    }
}
