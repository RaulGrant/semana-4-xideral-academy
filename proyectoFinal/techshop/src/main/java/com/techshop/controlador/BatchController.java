package com.techshop.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones de carga masiva (batch).
 * Permite disparar el job de importacion de productos desde CSV bajo demanda.
 */
@RestController
@RequestMapping("/api/batch")
@Tag(name = "Batch", description = "Operaciones de carga masiva de datos")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job importarProductosJob;

    public BatchController(JobLauncher jobLauncher, Job importarProductosJob) {
        this.jobLauncher = jobLauncher;
        this.importarProductosJob = importarProductosJob;
    }

    @PostMapping("/cargar-productos")
    @Operation(summary = "Cargar productos masivamente desde CSV",
               description = "Dispara el job de Spring Batch que lee productos.csv y los inserta en MongoDB. "
                       + "Cada ejecucion usa un timestamp unico para permitir re-ejecuciones.")
    public ResponseEntity<String> cargarProductos() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importarProductosJob, params);

            return ResponseEntity.ok("Job de carga de productos iniciado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al iniciar el job: " + e.getMessage());
        }
    }
}
