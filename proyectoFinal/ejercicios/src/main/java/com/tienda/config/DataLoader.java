package com.tienda.config;

import com.tienda.model.Category;
import com.tienda.model.Customer;
import com.tienda.model.Product;
import com.tienda.repository.CustomerRepository;
import com.tienda.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EJERCICIO 6 - DataLoader: carga datos iniciales al arrancar la aplicacion.
 *
 * CommandLineRunner: interfaz de Spring Boot que ejecuta el metodo run()
 * despues de que el contexto de aplicacion esta completamente inicializado.
 * Es ideal para seeds de datos, validaciones de configuracion o migraciones.
 *
 * @Profile("!test"): evita que este cargador corra en el perfil de pruebas,
 * para no contaminar la BD en memoria de los tests con datos extra.
 *
 * Patron de verificacion count() == 0: idempotente, solo inserta si la tabla
 * esta vacia. Evita duplicados en reinicios de la aplicacion.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        loadProducts();
        loadCustomers();
    }

    private void loadProducts() {
        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                Product.builder().name("Laptop Pro 15").price(1299.99)
                    .description("Laptop de alto rendimiento con Intel Core i7, 16GB RAM, SSD 512GB")
                    .category(Category.ELECTRONICS).stock(50).build(),
                Product.builder().name("Monitor 4K 27\"").price(449.99)
                    .description("Monitor 4K UHD con panel IPS y 144Hz").category(Category.ELECTRONICS).stock(30).build(),
                Product.builder().name("Teclado Mecanico RGB").price(89.99)
                    .description("Teclado mecanico con switches Cherry MX").category(Category.ELECTRONICS).stock(100).build(),
                Product.builder().name("Mouse Gamer Pro").price(59.99)
                    .description("Mouse gaming 16000 DPI con 7 botones").category(Category.ELECTRONICS).stock(120).build(),
                Product.builder().name("Clean Code - Robert Martin").price(49.99)
                    .description("El libro definitivo para escribir codigo limpio y profesional")
                    .category(Category.BOOKS).stock(200).build(),
                Product.builder().name("Spring Boot in Action").price(44.99)
                    .description("Guia completa de Spring Boot para desarrollo profesional")
                    .category(Category.BOOKS).stock(150).build(),
                Product.builder().name("Effective Java - Bloch").price(54.99)
                    .description("Mejores practicas y patrones para Java").category(Category.BOOKS).stock(180).build(),
                Product.builder().name("Zapatillas Running Air").price(119.99)
                    .description("Zapatillas de running con amortiguacion avanzada").category(Category.SPORTS).stock(75).build(),
                Product.builder().name("Camiseta Deportiva Dry-Fit").price(29.99)
                    .description("Camiseta tecnica de alto rendimiento").category(Category.CLOTHING).stock(200).build(),
                Product.builder().name("Balon de Futbol Pro").price(39.99)
                    .description("Balon oficial talla 5 FIFA approved").category(Category.SPORTS).stock(60).build()
            );
            productRepository.saveAll(products);
            log.info(">>> DataLoader: {} productos cargados correctamente", products.size());
        } else {
            log.info(">>> DataLoader: productos ya existen, saltando carga inicial");
        }
    }

    private void loadCustomers() {
        if (customerRepository.count() == 0) {
            List<Customer> customers = List.of(
                Customer.builder().name("Ana Garcia").email("ana.garcia@email.com").phone("555-0101").build(),
                Customer.builder().name("Carlos Lopez").email("carlos.lopez@email.com").phone("555-0102").build(),
                Customer.builder().name("Maria Rodriguez").email("maria.rodriguez@email.com").phone("555-0103").build()
            );
            customerRepository.saveAll(customers);
            log.info(">>> DataLoader: {} clientes cargados correctamente", customers.size());
        }
    }
}

