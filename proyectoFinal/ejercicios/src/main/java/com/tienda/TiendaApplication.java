package com.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EJERCICIO 1 - Clase principal de la aplicacion Spring Boot.
 *
 * @SpringBootApplication combina tres anotaciones:
 * - @Configuration: la clase es fuente de beans Spring.
 * - @EnableAutoConfiguration: activa la autoconfiguracion de Spring Boot
 *   basada en las dependencias del classpath.
 * - @ComponentScan: escanea el paquete actual y subpaquetes en busca de
 *   @Component, @Service, @Repository, @Controller, etc.
 */
@SpringBootApplication
public class TiendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaApplication.class, args);
    }
}

