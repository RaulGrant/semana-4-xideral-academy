package com.tienda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI 3.
 * Acceso: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda API REST")
                        .version("1.0.0")
                        .description("API REST completa para tienda en linea - Ejercicios Semana 4")
                        .contact(new Contact()
                                .name("Academia BBVA")
                                .email("academia@bbva.com")));
    }
}

