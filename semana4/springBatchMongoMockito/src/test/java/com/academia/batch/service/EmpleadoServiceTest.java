package com.academia.batch.service;

import com.academia.batch.model.Empleado;
import com.academia.batch.model.EmpleadoReporte;
import com.academia.batch.processor.EmpleadoProcessor;
import com.academia.batch.processor.ReporteProcessor;
import com.academia.batch.repository.EmpleadoReporteRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests del EmpleadoService usando Mockito.
 *
 * Conceptos de Mockito que se practican aqui:
 *
 * 1. @Mock          - crea un objeto falso que no hace nada por defecto
 * 2. @InjectMocks   - crea el objeto real e inyecta los mocks automaticamente
 * 3. @ExtendWith    - activa la extension de Mockito para JUnit 5
 * 4. when/thenReturn - programa el comportamiento de un mock
 * 5. verify         - comprueba que se llamo un metodo del mock
 * 6. ArgumentCaptor - captura el argumento con el que se llamo un metodo
 * 7. InOrder        - verifica el ORDEN en que se llamaron los metodos
 * 8. never()        - verifica que un metodo NUNCA se llamo
 * 9. times(n)       - verifica cuantas veces se llamo un metodo
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    // Mocks: objetos falsos que simulan las dependencias del servicio
    @Mock
    private EmpleadoProcessor empleadoProcessor;

    @Mock
    private ReporteProcessor reporteProcessor;

    @Mock
    private EmpleadoReporteRepository repository;

    // Objeto real bajo prueba: Mockito inyecta los 3 mocks automaticamente
    @InjectMocks
    private EmpleadoService service;

    // =====================================================================
    //  procesarYGuardar() - when/thenReturn + verify
    // =====================================================================

    @Test
    @DisplayName("procesarYGuardar: procesa empleado y guarda el reporte")
    void procesarYGuardar_empleadoValido_guardaReporte() throws Exception {
        // Arrange - preparamos los datos y programamos los mocks
        Empleado original = crearEmpleado("Juan Perez", "Ventas", 25000.0);
        Empleado procesado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0);
        procesado.setBono(2500.0);

        EmpleadoReporte reporte = crearReporte("rpt1", "JUAN PEREZ", "Ventas",
                25000.0, 2500.0, 27500.0);

        // Programamos el comportamiento de cada mock:
        // "cuando llamen a process(original), retorna procesado"
        when(empleadoProcessor.process(original)).thenReturn(procesado);
        when(reporteProcessor.process(procesado)).thenReturn(reporte);
        when(repository.save(reporte)).thenReturn(reporte);

        // Act - ejecutamos el metodo real del servicio
        EmpleadoReporte resultado = service.procesarYGuardar(original);

        // Assert - verificamos el resultado
        assertNotNull(resultado);
        assertEquals("JUAN PEREZ", resultado.getNombre());
        assertEquals(27500.0, resultado.getSalarioTotal());

        // Verify - comprobamos que se llamo cada mock exactamente 1 vez
        verify(empleadoProcessor).process(original);
        verify(reporteProcessor).process(procesado);
        verify(repository).save(reporte);
    }

    // =====================================================================
    //  procesarYGuardar() - InOrder (verificar orden de ejecucion)
    // =====================================================================

    @Test
    @DisplayName("procesarYGuardar: ejecuta los pasos en el orden correcto")
    void procesarYGuardar_empleadoValido_ejecutaEnOrden() throws Exception {
        // Arrange
        Empleado original = crearEmpleado("Maria Lopez", "TI", 35000.0);
        Empleado procesado = crearEmpleado("MARIA LOPEZ", "TI", 35000.0);
        procesado.setBono(3500.0);

        EmpleadoReporte reporte = crearReporte("rpt2", "MARIA LOPEZ", "TI",
                35000.0, 3500.0, 38500.0);

        when(empleadoProcessor.process(original)).thenReturn(procesado);
        when(reporteProcessor.process(procesado)).thenReturn(reporte);
        when(repository.save(reporte)).thenReturn(reporte);

        // Act
        service.procesarYGuardar(original);

        // Assert - InOrder verifica que los metodos se llamaron EN ESTE ORDEN:
        // primero empleadoProcessor, luego reporteProcessor, finalmente repository
        InOrder orden = inOrder(empleadoProcessor, reporteProcessor, repository);
        orden.verify(empleadoProcessor).process(original);       // 1ro
        orden.verify(reporteProcessor).process(procesado);       // 2do
        orden.verify(repository).save(reporte);                  // 3ro
    }

    // =====================================================================
    //  procesarYGuardar() - ArgumentCaptor (capturar argumentos)
    // =====================================================================

    @Test
    @DisplayName("procesarYGuardar: captura el reporte que se intenta guardar")
    void procesarYGuardar_empleadoValido_capturaReporteGuardado() throws Exception {
        // Arrange
        Empleado original = crearEmpleado("Carlos Garcia", "RRHH", 28000.0);
        Empleado procesado = crearEmpleado("CARLOS GARCIA", "RRHH", 28000.0);
        procesado.setBono(2800.0);

        EmpleadoReporte reporte = crearReporte(null, "CARLOS GARCIA", "RRHH",
                28000.0, 2800.0, 30800.0);

        when(empleadoProcessor.process(original)).thenReturn(procesado);
        when(reporteProcessor.process(procesado)).thenReturn(reporte);
        when(repository.save(any(EmpleadoReporte.class))).thenReturn(reporte);

        // Act
        service.procesarYGuardar(original);

        // Assert - ArgumentCaptor "captura" el argumento real que recibio repository.save()
        ArgumentCaptor<EmpleadoReporte> captor = ArgumentCaptor.forClass(EmpleadoReporte.class);
        verify(repository).save(captor.capture());

        EmpleadoReporte reporteCapturado = captor.getValue();
        assertAll("Reporte capturado",
            () -> assertEquals("CARLOS GARCIA", reporteCapturado.getNombre()),
            () -> assertEquals("RRHH", reporteCapturado.getDepartamento()),
            () -> assertEquals(30800.0, reporteCapturado.getSalarioTotal())
        );
    }

    // =====================================================================
    //  procesarLote() - times() (verificar numero de invocaciones)
    // =====================================================================

    @Test
    @DisplayName("procesarLote: procesa cada empleado de la lista")
    void procesarLote_listaConDosEmpleados_procesaCadaUno() throws Exception {
        // Arrange
        Empleado emp1 = crearEmpleado("Juan", "Ventas", 25000.0);
        Empleado emp2 = crearEmpleado("Maria", "TI", 35000.0);

        Empleado proc1 = crearEmpleado("JUAN", "Ventas", 25000.0);
        proc1.setBono(2500.0);
        Empleado proc2 = crearEmpleado("MARIA", "TI", 35000.0);
        proc2.setBono(3500.0);

        EmpleadoReporte rpt1 = crearReporte("r1", "JUAN", "Ventas", 25000.0, 2500.0, 27500.0);
        EmpleadoReporte rpt2 = crearReporte("r2", "MARIA", "TI", 35000.0, 3500.0, 38500.0);

        when(empleadoProcessor.process(emp1)).thenReturn(proc1);
        when(empleadoProcessor.process(emp2)).thenReturn(proc2);
        when(reporteProcessor.process(proc1)).thenReturn(rpt1);
        when(reporteProcessor.process(proc2)).thenReturn(rpt2);
        when(repository.save(rpt1)).thenReturn(rpt1);
        when(repository.save(rpt2)).thenReturn(rpt2);

        // Act
        List<EmpleadoReporte> resultados = service.procesarLote(Arrays.asList(emp1, emp2));

        // Assert
        assertEquals(2, resultados.size(), "Debe procesar 2 empleados");

        // times(2) verifica que cada processor se llamo exactamente 2 veces
        verify(empleadoProcessor, times(2)).process(any(Empleado.class));
        verify(reporteProcessor, times(2)).process(any(Empleado.class));
        verify(repository, times(2)).save(any(EmpleadoReporte.class));
    }

    @Test
    @DisplayName("procesarLote: lista vacia no invoca ningun mock")
    void procesarLote_listaVacia_noInvocaMocks() throws Exception {
        // Arrange
        List<Empleado> listaVacia = List.of();

        // Act
        List<EmpleadoReporte> resultados = service.procesarLote(listaVacia);

        // Assert
        assertTrue(resultados.isEmpty(), "Lista vacia no produce reportes");

        // never() verifica que NUNCA se llamo al metodo
        verify(empleadoProcessor, never()).process(any());
        verify(reporteProcessor, never()).process(any());
        verify(repository, never()).save(any());
    }

    // =====================================================================
    //  obtenerReporte() - when/thenReturn con Optional
    // =====================================================================

    @Test
    @DisplayName("obtenerReporte: retorna el reporte cuando existe")
    void obtenerReporte_idExistente_retornaReporte() {
        // Arrange
        EmpleadoReporte reporte = crearReporte("rpt1", "JUAN", "Ventas",
                25000.0, 2500.0, 27500.0);
        when(repository.findById("rpt1")).thenReturn(Optional.of(reporte));

        // Act
        Optional<EmpleadoReporte> resultado = service.obtenerReporte("rpt1");

        // Assert
        assertTrue(resultado.isPresent(), "Debe encontrar el reporte");
        assertEquals("JUAN", resultado.get().getNombre());
        verify(repository).findById("rpt1");
    }

    @Test
    @DisplayName("obtenerReporte: retorna vacio cuando no existe")
    void obtenerReporte_idInexistente_retornaVacio() {
        // Arrange
        when(repository.findById("noExiste")).thenReturn(Optional.empty());

        // Act
        Optional<EmpleadoReporte> resultado = service.obtenerReporte("noExiste");

        // Assert
        assertTrue(resultado.isEmpty(), "No debe encontrar el reporte");
        verify(repository).findById("noExiste");
    }

    // =====================================================================
    //  obtenerTodosLosReportes()
    // =====================================================================

    @Test
    @DisplayName("obtenerTodosLosReportes: retorna la lista del repositorio")
    void obtenerTodosLosReportes_conReportes_retornaTodos() {
        // Arrange
        EmpleadoReporte rpt1 = crearReporte("r1", "JUAN", "Ventas", 25000.0, 2500.0, 27500.0);
        EmpleadoReporte rpt2 = crearReporte("r2", "MARIA", "TI", 35000.0, 3500.0, 38500.0);
        when(repository.findAll()).thenReturn(Arrays.asList(rpt1, rpt2));

        // Act
        List<EmpleadoReporte> resultado = service.obtenerTodosLosReportes();

        // Assert
        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    // =====================================================================
    //  eliminarReporte() - verify + never()
    // =====================================================================

    @Test
    @DisplayName("eliminarReporte: elimina cuando el reporte existe")
    void eliminarReporte_idExistente_eliminaYRetornaTrue() {
        // Arrange
        when(repository.existsById("rpt1")).thenReturn(true);

        // Act
        boolean resultado = service.eliminarReporte("rpt1");

        // Assert
        assertTrue(resultado, "Debe retornar true al eliminar");

        // Verificamos que se llamo existsById Y LUEGO deleteById (InOrder)
        InOrder orden = inOrder(repository);
        orden.verify(repository).existsById("rpt1");
        orden.verify(repository).deleteById("rpt1");
    }

    @Test
    @DisplayName("eliminarReporte: no elimina cuando el reporte no existe")
    void eliminarReporte_idInexistente_retornaFalseSinEliminar() {
        // Arrange
        when(repository.existsById("noExiste")).thenReturn(false);

        // Act
        boolean resultado = service.eliminarReporte("noExiste");

        // Assert
        assertFalse(resultado, "Debe retornar false si no existe");

        // never() verifica que deleteById NUNCA se llamo
        verify(repository).existsById("noExiste");
        verify(repository, never()).deleteById(anyString());
    }

    // =====================================================================
    //  Metodos auxiliares para crear datos de prueba
    // =====================================================================

    private Empleado crearEmpleado(String nombre, String departamento, double salario) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setDepartamento(departamento);
        empleado.setSalario(salario);
        return empleado;
    }

    private EmpleadoReporte crearReporte(String id, String nombre, String departamento,
                                          double salario, double bono, double salarioTotal) {
        EmpleadoReporte reporte = new EmpleadoReporte();
        reporte.setId(id);
        reporte.setNombre(nombre);
        reporte.setDepartamento(departamento);
        reporte.setSalario(salario);
        reporte.setBono(bono);
        reporte.setSalarioTotal(salarioTotal);
        return reporte;
    }
}
