package com.tienda;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de arranque del contexto de Spring.
 * Verifica que toda la configuracion carga correctamente.
 * @ActiveProfiles("test") activa el perfil test para usar H2 y deshabilitar DataLoader.
 */
@SpringBootTest
@ActiveProfiles("test")
class TiendaApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto de Spring arranca sin errores, el test pasa.
        // Detecta errores de configuracion, beans faltantes, etc.
    }
}

