# Arquitectura REST con Spring Boot - Guia paso a paso

Esta guia explica como se construyo la aplicacion REST de alumnos. Siguiendo estos pasos puedes crear un CRUD para cualquier otra tabla.

---

## Estructura del proyecto

```
restSpringMysql/
├── pom.xml                                    ← Dependencias
├── src/main/java/com/academia/rest/
│   ├── RestSpringMysqlApplication.java        ← Punto de entrada
│   ├── entity/Alumno.java                     ← Representa la tabla
│   ├── repository/AlumnoRepository.java       ← Acceso a base de datos
│   ├── service/AlumnoService.java             ← Logica de negocio
│   └── controller/AlumnoController.java       ← Endpoints HTTP
└── src/main/resources/
    └── application.properties                 ← Configuracion
```

## Flujo de una peticion

```
Cliente (Postman/curl)
    ↓
Controller  →  Recibe la peticion HTTP
    ↓
Service     →  Ejecuta la logica de negocio
    ↓
Repository  →  Ejecuta el query en la base de datos
    ↓
MySQL       →  Devuelve los datos
    ↑
(El resultado regresa por el mismo camino)
```

Cada capa tiene una responsabilidad unica. El controller no toca la base de datos. El repository no decide logica. El service conecta ambos.

---

## Paso 1: pom.xml - Dependencias

El `pom.xml` define que librerias necesita el proyecto.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.2</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```

Hereda de Spring Boot 3.2.2 y usa Java 17.

### Dependencias necesarias

| Dependencia | Para que sirve |
|-------------|----------------|
| `spring-boot-starter-web` | Crear endpoints REST (GET, POST, PUT, DELETE) |
| `spring-boot-starter-data-jpa` | Conectar Java con la base de datos usando JPA |
| `mysql-connector-j` | Driver para comunicarse con MySQL |

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

**Para replicar:** No necesitas cambiar nada aqui. Estas dependencias sirven para cualquier tabla.

---

## Paso 2: application.properties - Configuracion

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/academia
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```

| Propiedad | Que hace |
|-----------|----------|
| `datasource.url` | URL de conexion a MySQL (host, puerto, base de datos) |
| `datasource.username` | Usuario de MySQL |
| `datasource.password` | Contraseña de MySQL |
| `hibernate.ddl-auto=update` | Si la tabla no existe, la crea automaticamente |
| `show-sql=true` | Muestra los queries SQL en consola (util para aprender) |
| `server.port` | Puerto donde corre la aplicacion |

**Para replicar:** Solo cambia la base de datos en la URL si usas otra diferente.

---

## Paso 3: Entity - Representar la tabla

La entity es una clase Java que representa una tabla de MySQL. Cada atributo es una columna.

**Tabla MySQL:**
```sql
CREATE TABLE alumnos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    fecha_registro DATE
);
```

**Entity Java:**
```java
package com.academia.rest.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(length = 150)
    private String email;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    // Constructor vacio (obligatorio para JPA)
    public Alumno() {
    }

    // Getters y Setters de cada atributo...
}
```

### Anotaciones clave

| Anotacion | Que hace |
|-----------|----------|
| `@Entity` | Le dice a JPA que esta clase representa una tabla |
| `@Table(name = "alumnos")` | Nombre de la tabla en MySQL |
| `@Id` | Esta columna es la llave primaria |
| `@GeneratedValue(IDENTITY)` | El valor lo genera MySQL (AUTO_INCREMENT) |
| `@Column(name = "fecha_registro")` | Mapea el atributo `fechaRegistro` a la columna `fecha_registro` |
| `@Column(nullable = false)` | La columna no acepta NULL |

### Para replicar con otra tabla

Ejemplo: si tienes una tabla `productos`:

```sql
CREATE TABLE productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2),
    stock INT
);
```

Tu entity seria:

```java
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private BigDecimal precio;

    private Integer stock;

    public Producto() {}

    // Getters y Setters...
}
```

**Regla:** un atributo por cada columna, con su getter y setter.

---

## Paso 4: Repository - Acceso a datos

El repository es una interfaz que hereda de `JpaRepository`. Spring genera automaticamente los metodos CRUD.

```java
package com.academia.rest.repository;

import com.academia.rest.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
}
```

`JpaRepository<Alumno, Integer>`:
- `Alumno` → La entity que maneja
- `Integer` → El tipo de dato de la llave primaria (id)

### Metodos que obtienes gratis (sin escribir codigo)

| Metodo | Query SQL equivalente |
|--------|-----------------------|
| `findAll()` | `SELECT * FROM alumnos` |
| `findById(id)` | `SELECT * FROM alumnos WHERE id = ?` |
| `save(alumno)` | `INSERT INTO alumnos ...` o `UPDATE alumnos SET ...` |
| `deleteById(id)` | `DELETE FROM alumnos WHERE id = ?` |
| `existsById(id)` | `SELECT COUNT(*) FROM alumnos WHERE id = ?` |

### Para replicar

```java
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
```

Solo cambia `Alumno` por tu entity y `Integer` por el tipo de su ID.

---

## Paso 5: Service - Logica de negocio

El service usa el repository para ejecutar las operaciones. Aqui va la logica que no es ni HTTP ni base de datos.

```java
package com.academia.rest.service;

import com.academia.rest.entity.Alumno;
import com.academia.rest.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {

    private final AlumnoRepository repository;

    // Spring inyecta el repository automaticamente
    public AlumnoService(AlumnoRepository repository) {
        this.repository = repository;
    }

    public List<Alumno> listarTodos() {
        return repository.findAll();
    }

    public Optional<Alumno> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Alumno crear(Alumno alumno) {
        if (alumno.getFechaRegistro() == null) {
            alumno.setFechaRegistro(LocalDate.now());
        }
        return repository.save(alumno);
    }

    public Optional<Alumno> actualizar(Integer id, Alumno datos) {
        return repository.findById(id).map(alumno -> {
            alumno.setNombre(datos.getNombre());
            alumno.setApellido(datos.getApellido());
            alumno.setEmail(datos.getEmail());
            return repository.save(alumno);
        });
    }

    public boolean eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

| Anotacion/Concepto | Que hace |
|---------------------|----------|
| `@Service` | Le dice a Spring que esta clase es un servicio (la detecta automaticamente) |
| Constructor con `AlumnoRepository` | Spring inyecta la dependencia (no usas `new`) |
| `Optional<Alumno>` | Puede contener un alumno o estar vacio (evita NullPointerException) |

### Para replicar

Crea `ProductoService`, cambia `Alumno` por `Producto`, `AlumnoRepository` por `ProductoRepository`, y ajusta los campos en el metodo `actualizar`.

---

## Paso 6: Controller - Endpoints HTTP

El controller recibe las peticiones HTTP y llama al service.

```java
package com.academia.rest.controller;

import com.academia.rest.entity.Alumno;
import com.academia.rest.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    private final AlumnoService service;

    public AlumnoController(AlumnoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alumno> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alumno> crear(@RequestBody Alumno alumno) {
        Alumno creado = service.crear(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizar(@PathVariable Integer id,
                                              @RequestBody Alumno alumno) {
        return service.actualizar(id, alumno)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### Anotaciones clave

| Anotacion | Que hace |
|-----------|----------|
| `@RestController` | Esta clase maneja peticiones HTTP y devuelve JSON |
| `@RequestMapping("/api/alumnos")` | Ruta base para todos los endpoints |
| `@GetMapping` | Responde a peticiones GET |
| `@PostMapping` | Responde a peticiones POST |
| `@PutMapping("/{id}")` | Responde a peticiones PUT con un parametro en la URL |
| `@DeleteMapping("/{id}")` | Responde a peticiones DELETE |
| `@PathVariable` | Captura el `{id}` de la URL |
| `@RequestBody` | Convierte el JSON del body en un objeto Java |
| `ResponseEntity` | Permite controlar el codigo HTTP de respuesta (200, 201, 404, etc.) |

### Para replicar

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    // Misma estructura, cambiando Alumno por Producto
    // y AlumnoService por ProductoService
}
```

---

## Resumen: como replicar para otra tabla

1. **Entity** → Una clase con `@Entity` y un atributo por columna
2. **Repository** → Una interfaz que extiende `JpaRepository<TuEntity, TipoDelId>`
3. **Service** → Una clase con `@Service` que usa el repository
4. **Controller** → Una clase con `@RestController` que usa el service

Los nombres siguen el patron:

| Capa | Nombre |
|------|--------|
| Entity | `Producto.java` |
| Repository | `ProductoRepository.java` |
| Service | `ProductoService.java` |
| Controller | `ProductoController.java` |

Cada capa solo habla con la siguiente. El controller nunca toca el repository directamente.
