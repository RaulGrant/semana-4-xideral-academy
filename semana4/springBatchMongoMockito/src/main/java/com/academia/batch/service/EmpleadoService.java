package com.academia.batch.service;

import com.academia.batch.model.Empleado;
import com.academia.batch.model.EmpleadoReporte;
import com.academia.batch.processor.EmpleadoProcessor;
import com.academia.batch.processor.ReporteProcessor;
import com.academia.batch.repository.EmpleadoReporteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que orquesta el procesamiento de empleados.
 *
 * Combina los dos processors del batch (EmpleadoProcessor y ReporteProcessor)
 * con el repositorio para procesar y persistir reportes.
 * Este servicio es el vehiculo principal para aprender Mockito.
 */
public class EmpleadoService {

    private final EmpleadoProcessor empleadoProcessor;
    private final ReporteProcessor reporteProcessor;
    private final EmpleadoReporteRepository repository;

    public EmpleadoService(EmpleadoProcessor empleadoProcessor,
                           ReporteProcessor reporteProcessor,
                           EmpleadoReporteRepository repository) {
        this.empleadoProcessor = empleadoProcessor;
        this.reporteProcessor = reporteProcessor;
        this.repository = repository;
    }

    /**
     * Procesa un empleado (mayusculas + bono) y guarda el reporte.
     */
    public EmpleadoReporte procesarYGuardar(Empleado empleado) throws Exception {
        // Paso 1: aplicar transformacion (mayusculas + bono 10%)
        Empleado procesado = empleadoProcessor.process(empleado);

        // Paso 2: convertir a reporte (calcula salarioTotal)
        EmpleadoReporte reporte = reporteProcessor.process(procesado);

        // Paso 3: persistir en el repositorio
        return repository.save(reporte);
    }

    /**
     * Procesa una lista de empleados y guarda todos los reportes.
     */
    public List<EmpleadoReporte> procesarLote(List<Empleado> empleados) throws Exception {
        List<EmpleadoReporte> reportes = new ArrayList<>();
        for (Empleado empleado : empleados) {
            reportes.add(procesarYGuardar(empleado));
        }
        return reportes;
    }

    /**
     * Busca un reporte por su ID.
     */
    public Optional<EmpleadoReporte> obtenerReporte(String id) {
        return repository.findById(id);
    }

    /**
     * Retorna todos los reportes almacenados.
     */
    public List<EmpleadoReporte> obtenerTodosLosReportes() {
        return repository.findAll();
    }

    /**
     * Elimina un reporte si existe.
     */
    public boolean eliminarReporte(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
