package com.techshop.controlador;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(BatchController.class)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean
    private Job importarProductosJob;

    @Test
    void cargarProductos_jobExitoso_retorna200() throws Exception {
        JobExecution jobExecution = mock(JobExecution.class);
        when(jobLauncher.run(eq(importarProductosJob), any(JobParameters.class)))
                .thenReturn(jobExecution);

        mockMvc.perform(post("/api/batch/cargar-productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("iniciado exitosamente")));
    }

    @Test
    void cargarProductos_jobFalla_retorna500() throws Exception {
        when(jobLauncher.run(eq(importarProductosJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("Error de conexion a MongoDB"));

        mockMvc.perform(post("/api/batch/cargar-productos"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error al iniciar el job")));
    }
}
