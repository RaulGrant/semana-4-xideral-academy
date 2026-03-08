# Ejercicios Semana 4 — Tienda API REST

> **API REST completa** para una tienda en línea construida de forma progresiva en 6 ejercicios.  
> Stack: Java 17 · Spring Boot 3.2 · Spring MVC · Spring Data JPA · H2 · JUnit 5 · Mockito · Lombok · Swagger

---

## Resumen de ejercicios implementados

| # | Ejercicio | Tecnología clave | Dificultad |
|---|-----------|-----------------|------------|
| 1 | Proyecto base y modelo de datos | Spring Boot, JPA, H2, Lombok | Principiante |
| 2 | Repository y Service Layer | Spring Data JPA, DTOs (records), excepciones | Intermedio |
| 3 | REST Controller CRUD completo | Spring MVC, ResponseEntity, @Valid, @RestControllerAdvice | Intermedio |
| 4 | Unit Tests con JUnit 5 y Mockito | @Mock, @InjectMocks, @Nested, ArgumentCaptor, AssertJ | Intermedio |
| 5 | Relaciones JPA y lógica de negocio | @OneToMany, @ManyToOne, transacciones, stock | Avanzado |
| 6 | DataLoader, paginación y Swagger | CommandLineRunner, Pageable, OpenAPI | Avanzado |

---

## Cómo ejecutar

### Prerrequisitos
- JDK 17+
- Maven 3.9+

### Ejecutar la aplicación
```powershell
cd C:\Users\D E L L\IdeaProjects\semana4\proyectoFinal\ejercicios
mvn clean spring-boot:run
```

### Ejecutar los tests
```powershell
mvn clean test
```

### Ver reporte de cobertura (JaCoCo)
```powershell
mvn clean verify
# Abrir: target/site/jacoco/index.html
```

---

## URLs de acceso (aplicación corriendo)

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs JSON | http://localhost:8080/api-docs |
| Consola H2 | http://localhost:8080/h2-console |

> H2 Console: JDBC URL = `jdbc:h2:mem:tiendadb` · User = `sa` · Password = *(vacío)*

---

## Endpoints disponibles

### Productos — `/api/v1/products`

| Método | URL | Descripción | Response |
|--------|-----|-------------|----------|
| `GET` | `/api/v1/products` | Lista paginada de productos | `200 Page<ProductResponse>` |
| `GET` | `/api/v1/products/{id}` | Producto por ID | `200` / `404` |
| `POST` | `/api/v1/products` | Crear producto | `201 ProductResponse` |
| `PUT` | `/api/v1/products/{id}` | Actualizar producto | `200` / `404` |
| `DELETE` | `/api/v1/products/{id}` | Eliminar producto | `204 No Content` |
| `GET` | `/api/v1/products/search?name=laptop` | Buscar por nombre | `200 List` |
| `GET` | `/api/v1/products/available` | Solo con stock > 0 | `200 List` |
| `GET` | `/api/v1/products/category/{cat}` | Filtrar por categoría | `200 List` |
| `GET` | `/api/v1/products/best-sellers` | Top 5 más vendidos | `200 List` |

**Parámetros de paginación** (GET `/api/v1/products`):

| Param | Default | Descripción |
|-------|---------|-------------|
| `page` | `0` | Número de página |
| `size` | `10` | Elementos por página |
| `sortBy` | `name` | Campo de ordenación |

### Clientes — `/api/v1/customers`

| Método | URL | Descripción | Response |
|--------|-----|-------------|----------|
| `GET` | `/api/v1/customers` | Lista de clientes | `200` |
| `GET` | `/api/v1/customers/{id}` | Cliente por ID | `200` / `404` |
| `POST` | `/api/v1/customers` | Crear cliente | `201` |
| `GET` | `/api/v1/customers/{id}/orders` | Órdenes del cliente | `200` |

### Órdenes — `/api/v1/orders`

| Método | URL | Descripción | Response |
|--------|-----|-------------|----------|
| `POST` | `/api/v1/orders` | Crear orden (reduce stock) | `201` / `409` si stock insuficiente |
| `GET` | `/api/v1/orders/{id}` | Detalle de orden | `200` / `404` |

---

## Ejemplos de uso con curl

### Crear un producto
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Gaming","price":1199.99,"description":"Laptop para gaming","category":"ELECTRONICS","stock":15}'
```

### Listar productos (página 0, 5 por página, ordenado por precio)
```bash
curl "http://localhost:8080/api/v1/products?page=0&size=5&sortBy=price"
```

### Crear cliente
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Perez","email":"juan@email.com","phone":"555-1234"}'
```

### Crear orden
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":2}]}'
```

---

## Estructura del proyecto

```
ejercicios/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/tienda/
    │   │   ├── TiendaApplication.java          # Ejercicio 1 - Entry point
    │   │   ├── model/
    │   │   │   ├── Product.java                # Ejercicio 1 - Entidad JPA
    │   │   │   ├── Category.java               # Ejercicio 1 - Enum
    │   │   │   ├── Customer.java               # Ejercicio 5 - @OneToMany
    │   │   │   ├── Order.java                  # Ejercicio 5 - @ManyToOne
    │   │   │   ├── OrderItem.java              # Ejercicio 5 - Detalle de orden
    │   │   │   └── OrderStatus.java            # Ejercicio 5 - Enum estado
    │   │   ├── repository/
    │   │   │   ├── ProductRepository.java      # Ejercicio 2 - JpaRepository + @Query
    │   │   │   ├── CustomerRepository.java     # Ejercicio 5
    │   │   │   └── OrderRepository.java        # Ejercicio 5
    │   │   ├── dto/
    │   │   │   ├── ProductRequest.java         # Ejercicio 2 - record de entrada
    │   │   │   ├── ProductResponse.java        # Ejercicio 2 - record de salida
    │   │   │   ├── CustomerRequest/Response    # Ejercicio 5
    │   │   │   └── OrderRequest/Response       # Ejercicio 5
    │   │   ├── service/
    │   │   │   ├── ProductService.java         # Ejercicios 2+6 - Lógica de negocio
    │   │   │   ├── CustomerService.java        # Ejercicio 5
    │   │   │   └── OrderService.java           # Ejercicio 5 - Lógica de stock
    │   │   ├── controller/
    │   │   │   ├── ProductController.java      # Ejercicios 3+6 - REST + paginación
    │   │   │   ├── CustomerController.java     # Ejercicio 5
    │   │   │   └── OrderController.java        # Ejercicio 5
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException  # Ejercicio 2
    │   │   │   ├── BusinessException           # Ejercicio 2
    │   │   │   └── GlobalExceptionHandler      # Ejercicio 3 - @RestControllerAdvice
    │   │   └── config/
    │   │       ├── OpenApiConfig.java          # Ejercicio 6 - Swagger
    │   │       └── DataLoader.java             # Ejercicio 6 - CommandLineRunner
    │   └── resources/
    │       └── application.properties          # H2, JPA, Swagger config
    └── test/
        └── java/com/tienda/
            ├── TiendaApplicationTests.java     # Context load test
            ├── service/
            │   ├── ProductServiceTest.java     # Ejercicio 4 - 13 tests unitarios
            │   └── OrderServiceTest.java       # Ejercicio 5 - 5 tests unitarios
            └── controller/
                └── ProductControllerTest.java  # @WebMvcTest - 6 tests web
```

---

## Cobertura de tests

| Clase de tests | Tests | Técnica |
|----------------|-------|---------|
| `ProductServiceTest` | 13 | `@Mock`, `@InjectMocks`, `@Nested`, `ArgumentCaptor`, AssertJ |
| `OrderServiceTest` | 5 | `@Mock`, transaccionalidad, validación de stock |
| `ProductControllerTest` | 6 | `@WebMvcTest`, `MockMvc`, `@MockBean` |
| `TiendaApplicationTests` | 1 | `@SpringBootTest` context load |
| **Total** | **25** | **JUnit 5 + Mockito + AssertJ** |

---

## Categorías disponibles

```
ELECTRONICS, BOOKS, CLOTHING, FOOD, SPORTS
```

---

## Códigos de respuesta HTTP implementados

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| `200 OK` | Éxito con cuerpo | GET, PUT exitosos |
| `201 Created` | Recurso creado | POST exitoso |
| `204 No Content` | Éxito sin cuerpo | DELETE exitoso |
| `400 Bad Request` | Datos inválidos | Falló validación `@Valid` |
| `404 Not Found` | No encontrado | ID no existe |
| `409 Conflict` | Conflicto de negocio | Nombre duplicado, stock insuficiente |
| `500 Internal Server Error` | Error no controlado | Excepción inesperada |

---

## Datos de prueba precargados (DataLoader)

Al iniciar la app se cargan automáticamente:

**Productos:** Laptop Pro 15, Monitor 4K, Teclado Mecánico, Mouse Gamer, Clean Code, Spring Boot in Action, Effective Java, Zapatillas Running, Camiseta Dry-Fit, Balón de Fútbol.

**Clientes:** Ana García, Carlos López, María Rodríguez.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 17 | Lenguaje base |
| Spring Boot | 3.2.3 | Framework y autoconfiguración |
| Spring MVC | 6.x | Controladores REST |
| Spring Data JPA | 3.x | Persistencia relacional |
| H2 Database | Runtime | BD en memoria para dev/test |
| Lombok | Latest | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.3.0 | Swagger UI automático |
| JUnit 5 | 5.x | Framework de testing |
| Mockito | 5.x | Mocks para tests unitarios |
| AssertJ | 3.x | Assertions fluidas |
| JaCoCo | 0.8.11 | Cobertura de código |
| Maven | 3.9+ | Build y dependencias |

