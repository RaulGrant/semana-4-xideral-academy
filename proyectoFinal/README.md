# Proyecto Final - TechShop Ecommerce

## Descripcion
TechShop Ecommerce es el proyecto final del curso, desarrollado en Java 17 con Spring Boot. La solucion integra API REST para gestion de usuarios, productos, categorias, carrito y ordenes, procesos batch para carga/procesamiento de datos y persistencia hibrida con base de datos relacional (MySQL) y NoSQL (MongoDB).

Este README documenta **como se implementaron las tecnologias** solicitadas, tomando evidencia de:
- `proyectoFinal/techshop`
- `semana4/restSpringMysql`
- `semana4/springBatchMongoMockito`

## Objetivos del proyecto
- Construir servicios RESTful en Java para un caso ecommerce.
- Aplicar arquitectura Spring (Core + MVC + Boot + Data).
- Implementar integracion con MySQL y MongoDB.
- Ejecutar procesamiento por lotes con Spring Batch.
- Aplicar testing con JUnit, assertions y Mockito.
- Gestionar el ciclo de vida con Maven y control de versiones con Git.

## Stack tecnologico
- Java 17
- Spring Boot 3.x
- Spring MVC (`spring-boot-starter-web`)
- Spring Data JPA (`spring-boot-starter-data-jpa`)
- Spring Data MongoDB (`spring-boot-starter-data-mongodb`)
- Spring Batch (`spring-boot-starter-batch`)
- Spring Validation (`spring-boot-starter-validation`)
- Swagger/OpenAPI (`springdoc-openapi-starter-webmvc-ui`)
- Maven
- JUnit 5, Mockito, Spring Boot Test, Spring Batch Test
- JaCoCo (cobertura)
- MySQL, MongoDB, H2 (tests)
- Docker Compose

## Evidencia de implementacion por tecnologia

### 1) JUnit, assertions y Mockito
**Proyecto final (`proyectoFinal/techshop`)**
- Dependencias de testing en `techshop/pom.xml`:
  - `spring-boot-starter-test`
  - `spring-batch-test`
  - `h2` para pruebas en memoria
- Pruebas presentes en:
  - `techshop/src/test/java/com/techshop/TechShopApplicationTests.java`
  - `techshop/src/test/java/com/techshop/SchemaValidacionTest.java`
  - Paquetes de pruebas por capa: `batch/`, `controlador/`, `modelo/`, `servicio/`
- Configuracion dedicada para test:
  - `techshop/src/test/resources/application-schemaval.properties`
  - `techshop/src/test/resources/schema-h2.sql`
  - `techshop/src/main/resources/application-test.properties`
- Assertions: se usan via JUnit/Spring Test (por ejemplo, validaciones de estado, nullidad, igualdad y comportamiento esperado en pruebas unitarias/integracion).

**Semana4 (`springBatchMongoMockito`)**
- `springBatchMongoMockito/pom.xml` incluye:
  - `org.junit.jupiter:junit-jupiter`
  - `org.mockito:mockito-core`
  - `org.mockito:mockito-junit-jupiter`
- Se consolida la practica de pruebas unitarias con mocks para aislar servicios/procesadores batch de infraestructura externa.

### 2) Spring Core
- Inyeccion de dependencias y gestion de beans por anotaciones (`@Service`, `@Repository`, `@Configuration`, etc.) en la estructura:
  - `techshop/src/main/java/com/techshop/servicio/`
  - `techshop/src/main/java/com/techshop/repositorio/`
  - `techshop/src/main/java/com/techshop/config/`
- Ejemplos de configuracion en:
  - `techshop/src/main/java/com/techshop/config/BatchConfig.java`
  - `techshop/src/main/java/com/techshop/config/SwaggerConfig.java`

### 3) Spring MVC
- Controladores REST implementados en:
  - `techshop/src/main/java/com/techshop/controlador/ProductoController.java`
  - `techshop/src/main/java/com/techshop/controlador/CategoriaController.java`
  - `techshop/src/main/java/com/techshop/controlador/UsuarioController.java`
  - `techshop/src/main/java/com/techshop/controlador/CarritoController.java`
  - `techshop/src/main/java/com/techshop/controlador/OrdenController.java`
  - `techshop/src/main/java/com/techshop/controlador/BatchController.java`
- En `semana4/restSpringMysql` se refuerza el patron MVC para CRUD REST con MySQL.

### 4) Spring Boot
- Aplicaciones bootstrap por clase principal:
  - `techshop/src/main/java/com/techshop/TechShopApplication.java`
- Parent y plugin de Spring Boot en `pom.xml` (proyecto final y proyectos de semana4), lo que facilita autoconfiguracion, arranque y empaquetado.

### 5) Spring Data JPA
- Uso en `techshop/pom.xml` con `spring-boot-starter-data-jpa`.
- Persistencia relacional via repositorios en:
  - `techshop/src/main/java/com/techshop/repositorio/`
- Modelado de entidades para dominio ecommerce:
  - `techshop/src/main/java/com/techshop/modelo/Producto.java`
  - `techshop/src/main/java/com/techshop/modelo/Categoria.java`
  - `techshop/src/main/java/com/techshop/modelo/Usuario.java`
  - `techshop/src/main/java/com/techshop/modelo/Orden.java`
  - `techshop/src/main/java/com/techshop/modelo/OrdenDetalle.java`
  - `techshop/src/main/java/com/techshop/modelo/CarritoItem.java`

### 6) Spring Batch
- Dependencia en `techshop/pom.xml` y `springBatchMongoMockito/pom.xml`.
- Configuracion y procesamiento batch en:
  - `techshop/src/main/java/com/techshop/config/BatchConfig.java`
  - `techshop/src/main/java/com/techshop/batch/ProductoItemProcessor.java`
  - `techshop/src/main/java/com/techshop/controlador/BatchController.java`
  - `techshop/src/main/resources/productos.csv`

### 7) Maven
- Gestion de dependencias y ciclo de build en:
  - `proyectoFinal/techshop/pom.xml`
  - `semana4/restSpringMysql/pom.xml`
  - `semana4/springBatchMongoMockito/pom.xml`
- Plugins relevantes:
  - `spring-boot-maven-plugin`
  - `jacoco-maven-plugin` (cobertura minima definida en proyectos)

### 8) Git (CLI y GUI)
- Evidencia de trabajo colaborativo y estandarizacion en:
  - `proyectoFinal/guias/guiaGit.md`
  - `proyectoFinal/guias/guiaGitHooks.md`
- Flujo tipico aplicado:
  - CLI: `git status`, `git add`, `git commit`, `git push`, `git pull`
  - GUI: apoyo visual para ramas, historial, conflictos y PRs (segun herramienta instalada: IDE/GitHub Desktop/cliente Git).

### 9) Base de datos relacional (MySQL) y MongoDB
- Orquestacion de servicios con Docker Compose:
  - `proyectoFinal/docker-compose.yml`
- Inicializacion de BD:
  - MySQL: `proyectoFinal/db/mysql/init.sql`
  - MongoDB: `proyectoFinal/db/mongodb/init.js`
- Integracion en app principal por dependencias Spring Data JPA + MongoDB (`techshop/pom.xml`).

### 10) HTTP/HTTPS, verbos HTTP y response codes
- Las APIs del proyecto se exponen sobre HTTP (local/dev) y pueden desplegarse tras proxy TLS para HTTPS en ambientes productivos.
- Verbos HTTP usados en servicios REST:
  - `GET` (consultar)
  - `POST` (crear)
  - `PUT/PATCH` (actualizar)
  - `DELETE` (eliminar)
- Response codes esperados/estandar en una API REST Spring:
  - `200 OK`, `201 Created`, `204 No Content`
  - `400 Bad Request`, `404 Not Found`, `409 Conflict`, `500 Internal Server Error`
- Referencia conceptual adicional en:
  - `semana4/restSpringMysql/documents/guia-conceptos-rest-http.md`
  - `semana4/restSpringMysql/documents/guia-endpoints-postman.md`

### 11) REST vs SOAP
- En el alcance del proyecto se implementa **REST** (recursos + JSON + HTTP + stateless) por simplicidad, interoperabilidad y ajuste al stack Spring Web.
- **SOAP** no fue el enfoque de implementacion, pero se contrasta academicamente como protocolo con contrato estricto (WSDL/XML) y orientado a integraciones empresariales especificas.

### 12) Servicios RESTful con Java
- Implementados con Spring Boot + Spring MVC en controladores del modulo `techshop`.
- Capa de acceso a datos separada en repositorios y capa de negocio en servicios.
- Documentacion de endpoints y uso (Swagger/Postman/curl) en guias del repositorio.

## Arquitectura de alto nivel
- **Controlador**: recibe peticiones HTTP y delega casos de uso.
- **Servicio**: concentra reglas de negocio.
- **Repositorio**: abstrae persistencia (JPA/Mongo).
- **Modelo**: entidades del dominio ecommerce.
- **Batch**: jobs para cargas/procesamiento de archivos.

## Instalacion y ejecucion
> Nota: estos pasos se basan en los archivos presentes (`docker-compose.yml`, `pom.xml`, `test-endpoints.sh`) y pueden ajustarse segun tu entorno local.

### 1) Prerrequisitos
- JDK 17
- Maven 3.9+
- Docker y Docker Compose
- Git

### 2) Levantar infraestructura (MySQL + MongoDB)
```powershell
cd C:\Users\D E L L\IdeaProjects\semana4\proyectoFinal
docker compose up -d
```

### 3) Ejecutar la aplicacion principal (TechShop)
```powershell
cd C:\Users\D E L L\IdeaProjects\semana4\proyectoFinal\techshop
mvn clean spring-boot:run
```

### 4) Ejecutar pruebas
```powershell
cd C:\Users\D E L L\IdeaProjects\semana4\proyectoFinal\techshop
mvn test
```

### 5) (Opcional) Probar endpoints por script
```powershell
cd C:\Users\D E L L\IdeaProjects\semana4\proyectoFinal
bash .\test-endpoints.sh
```

## Screenshots (requerido para entrega)
Agregar capturas en `proyectoFinal/screenshots/` y referenciarlas aqui.

### Sugerencia de capturas
1. `screenshots/01-swagger-ui.png` - Swagger UI con endpoints cargados.
2. `screenshots/02-postman-crud-productos.png` - CRUD de productos (GET/POST/PUT/DELETE).
3. `screenshots/03-batch-job-ejecucion.png` - ejecucion de job batch y resultado.
4. `screenshots/04-mysql-tablas.png` - tablas y registros en MySQL.
5. `screenshots/05-mongodb-colecciones.png` - colecciones y documentos en MongoDB.
6. `screenshots/06-tests-junit-mockito.png` - pruebas pasando en consola/IDE.
7. `screenshots/07-jacoco-reporte.png` - reporte de cobertura JaCoCo.
8. `screenshots/08-git-cli-gui.png` - evidencia de flujo Git por CLI y GUI.

> Insercion markdown ejemplo:
>
> `![Swagger UI](screenshots/01-swagger-ui.png)`

## Relacion con proyectos de semana4 (trazabilidad academica)

### `semana4/restSpringMysql`
- Refuerza construccion de API REST CRUD con Spring Web + Spring Data JPA + MySQL.
- Aporta documentacion conceptual y operativa:
  - `documents/guia-arquitectura-paso-a-paso.md`
  - `documents/guia-conceptos-rest-http.md`
  - `documents/guia-ejecutar-aplicacion.md`
  - `documents/guia-endpoints-postman.md`

### `semana4/springBatchMongoMockito`
- Refuerza Spring Batch con pruebas unitarias usando JUnit5 + Mockito.
- Aporta guia especifica de testing:
  - `documentos/guia-mockito-testing.md`
  - `documentos/guia-spring-batch-v2-mongo.md`

## Calidad y cobertura
- JaCoCo configurado en los proyectos Maven para generar reporte y definir umbrales de cobertura:
  - `proyectoFinal/techshop/pom.xml` (umbral 80% lineas)
  - `semana4/springBatchMongoMockito/pom.xml` (umbral 60% lineas)

## Conclusiones
Durante el proyecto final se integraron de forma practica las tecnologias del curso:
- Spring Boot como base de ejecucion y autoconfiguracion.
- Spring MVC para exponer servicios RESTful.
- Spring Data JPA y MongoDB para persistencia poliglota.
- Spring Batch para procesamiento por lotes.
- JUnit, assertions y Mockito para pruebas y calidad.
- Maven y Git para ciclo de desarrollo e integracion.

El resultado es una aplicacion backend orientada a ecommerce con arquitectura por capas, enfoque API-first y trazabilidad tecnica con los laboratorios de `semana4`.

