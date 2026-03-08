package com.techshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger/OpenAPI para documentacion interactiva de la API.
 * Accesible en /swagger-ui.html una vez levantada la aplicacion.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI techShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechShop Ecommerce API")
                        .description("API REST para tienda de electronica - Proyecto final academia BBVA. "
                                + "Sprint 1: Catalogo (categorias, productos). "
                                + "Sprint 2: Compra (usuarios, carrito, ordenes). "
                                + "Sprint 3: Carga masiva con Spring Batch, testing y calidad.")
                        .version("3.0.0")
                        .contact(new Contact()
                                .name("Miguel Rugerio")
                                .email("miguel.rugerio@outlook.com")));
    }
}
