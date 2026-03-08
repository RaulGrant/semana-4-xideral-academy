package com.techshop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Valida que schema-h2.sql (espejo de db/mysql/init.sql) y las entidades JPA
 * sean consistentes en nombres de tablas y columnas.
 *
 * Hibernate corre con ddl-auto=validate: si cualquier @Column o @Table no
 * coincide con el SQL real, el contexto falla y este test lo detecta.
 *
 * Cuando modifiques init.sql, actualiza tambien schema-h2.sql.
 */
@SpringBootTest
@ActiveProfiles("schemaval")
@DisplayName("Validacion: schema SQL vs entidades JPA")
class SchemaValidacionTest {

    @Test
    @DisplayName("El contexto arranca correctamente con el schema de init.sql")
    void schemaCoincideConEntidadesJPA() {
        // Si el contexto Spring arranca sin excepcion, el schema y las
        // entidades son consistentes. No se necesita codigo adicional.
    }
}
