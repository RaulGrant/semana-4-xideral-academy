package com.academia.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.times;

/**
 * Test para la clase principal SpringBatchApplication.
 * Usa Mockito para interceptar SpringApplication.run() y evitar
 * levantar el contexto completo de Spring.
 */
class SpringBatchApplicationTest {

    @Test
    @DisplayName("main() invoca SpringApplication.run() con la clase correcta")
    void main_invocaSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mockSpring = Mockito.mockStatic(SpringApplication.class)) {
            SpringBatchApplication.main(new String[]{});

            mockSpring.verify(
                () -> SpringApplication.run(SpringBatchApplication.class, new String[]{}),
                times(1)
            );
        }
    }
}
