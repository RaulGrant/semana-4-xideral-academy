package com.academia.batch.repository;

import com.academia.batch.model.EmpleadoReporte;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de reportes de empleados.
 *
 * Definimos una interfaz simple (no extiende MongoRepository) para que
 * sea facil de entender y de mockear en los tests con Mockito.
 */
public interface EmpleadoReporteRepository {

    EmpleadoReporte save(EmpleadoReporte reporte);

    Optional<EmpleadoReporte> findById(String id);

    List<EmpleadoReporte> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
