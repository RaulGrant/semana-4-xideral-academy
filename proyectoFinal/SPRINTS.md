# Planificacion Scrum - TechShop Ecommerce

## Metodologia
Se implementa Scrum con 3 sprints incrementales. Cada sprint entrega funcionalidad
demostrable a traves de Swagger.

---

## Product Backlog (priorizado por el Product Owner)

| Prioridad | Historia de Usuario | Requisitos |
|---|---|---|
| 1 | Como admin, quiero gestionar productos y categorias | RF009, RF011 |
| 2 | Como cliente, quiero ver el catalogo de productos | RF001, RF002, RF003 |
| 3 | Como cliente, quiero un carrito de compras | RF004, RF005, RF006 |
| 4 | Como cliente, quiero realizar y consultar pedidos | RF007, RF008 |
| 5 | Como admin, quiero cargar productos masivamente | RF010 |
| 6 | Como usuario, quiero registrarme en la tienda | (registro) |
| 7 | Como desarrollador, quiero tests y documentacion | (calidad) |

---

## SPRINT 1 — "Los cimientos y el catalogo"

**Objetivo:** Tener el proyecto funcionando con el modulo de productos completo (MongoDB)

### Sprint Backlog

| # | Historia de Usuario | Tareas tecnicas | Requisitos |
|---|---|---|---|
| 1 | Estructura del proyecto | Crear proyecto Spring Boot, pom.xml con todas las dependencias (incluye JUnit 5, Mockito, JaCoCo con umbral 80%), configurar conexion a MySQL y MongoDB, estructura de paquetes | - |
| 2 | Como admin, quiero gestionar categorias | Modelo `Categoria`, repositorio MongoDB, servicio, controlador REST (`/api/categorias`). Tests unitarios de CategoriaService (Mockito), tests de integracion del controlador (MockMvc) | RF011 |
| 3 | Como admin, quiero crear/editar/eliminar productos | Modelo `Producto`, repositorio MongoDB, servicio, controlador REST (`/api/productos`). Tests unitarios de ProductoService (Mockito), tests de integracion del controlador (MockMvc) | RF009 |
| 4 | Como cliente, quiero ver el catalogo | Listar todos, filtrar por categoria, ver detalle. Tests de integracion de los endpoints de consulta (MockMvc) | RF001, RF002, RF003 |
| 5 | Configurar Swagger | Documentacion interactiva de los endpoints del Sprint 1 | - |

### Demo Sprint 1
Abrir Swagger, crear categorias, crear productos, listar por categoria, ver detalle.
Mostrar reporte de JaCoCo con coverage >= 80%.

### Tecnologias demostradas
Spring Boot, Spring Data MongoDB, REST API, Maven, Swagger, JUnit 5, Mockito, MockMvc, JaCoCo

### Estado: COMPLETADO

---

## SPRINT 2 — "La experiencia de compra"

**Objetivo:** El flujo completo de compra: registro → carrito → orden

### Sprint Backlog

| # | Historia de Usuario | Tareas tecnicas | Requisitos |
|---|---|---|---|
| 1 | Como usuario, quiero registrarme | Entidad `Usuario` JPA, repositorio MySQL, servicio, controlador REST (`/api/usuarios`). Tests unitarios de UsuarioService (Mockito), tests de integracion del controlador (MockMvc) | - |
| 2 | Como cliente, quiero agregar productos a mi carrito | Entidad `CarritoItem` JPA, repositorio, servicio (valida producto en MongoDB), controlador REST (`/api/carrito`). Tests unitarios de CarritoService (Mockito), tests de integracion del controlador (MockMvc) | RF004, RF005, RF006 |
| 3 | Como cliente, quiero realizar un pedido | Entidades `Orden` y `OrdenDetalle` JPA, repositorio, servicio (convierte carrito en orden), controlador REST (`/api/ordenes`). Tests unitarios de OrdenService (Mockito), tests de integracion del controlador (MockMvc) | RF007 |
| 4 | Como cliente, quiero consultar mis ordenes | Endpoint para listar ordenes por usuario y ver detalle. Tests de integracion de los endpoints de consulta (MockMvc) | RF008 |
| 5 | Swagger actualizado | Documentar los nuevos endpoints del Sprint 2 | - |

### Demo Sprint 2
Registrar usuario → buscar productos → agregar al carrito → confirmar pedido → consultar orden.
Flujo completo de compra. Reporte de coverage mantenido >= 80%.

### Tecnologias demostradas
Spring Data JPA, MySQL, relaciones entre entidades, integracion MySQL + MongoDB, JUnit 5, Mockito, MockMvc

### Estado: COMPLETADO

---

## SPRINT 3 — "Batch, testing y calidad"

**Objetivo:** Carga masiva, tests unitarios y pulir el proyecto

### Sprint Backlog

| # | Historia de Usuario | Tareas tecnicas | Requisitos |
|---|---|---|---|
| 1 | Como admin, quiero cargar productos desde CSV | Job de Spring Batch: reader (CSV) → processor (validar/transformar) → writer (MongoDB), archivo CSV de ejemplo con 50+ productos, endpoint para disparar el job. Tests unitarios del processor (Mockito), tests de integracion del job y del endpoint (MockMvc) | RF010 |
| 2 | Reforzar cobertura y casos edge | Reforzar casos edge en servicios existentes, validar coverage global >= 80% (JaCoCo) | - |
| 3 | Documentacion final Swagger | Revisar que todos los endpoints esten documentados con descripciones y ejemplos | - |
| 4 | Demo final | Preparar demo completa del flujo end-to-end | - |

### Demo Sprint 3
Cargar CSV con 50 productos → verificar que se cargaron → flujo de compra completo →
mostrar Swagger completo → mostrar reporte de JaCoCo con coverage global >= 80%.

### Tecnologias demostradas
Spring Batch, JUnit 5, Mockito, Swagger completo, JaCoCo

### Estado: COMPLETADO

---

## Resumen visual

```
Sprint 1                Sprint 2                Sprint 3
"Catalogo"              "Compra"                "Batch + Calidad"
┌──────────────┐       ┌──────────────┐        ┌──────────────┐
│ Spring Boot  │       │ Usuarios     │        │ Spring Batch │
│ MongoDB      │  ──>  │ Carrito (JPA)│  ──>   │ + sus tests  │
│ Productos    │       │ Ordenes (JPA)│        │ Reforzar edge│
│ Categorias   │       │ MySQL + Mongo│        │ Swagger final│
│ Swagger v1   │       │ Swagger v2   │        │ Demo final   │
│ Tests + 80%  │       │ Tests + 80%  │        │ Tests + 80%  │
└──────────────┘       └──────────────┘        └──────────────┘
   MongoDB only         MySQL + MongoDB        Batch + Refuerzo
```

---

## Cobertura de tecnologias por sprint

| Tecnologia | Sprint 1 | Sprint 2 | Sprint 3 |
|---|---|---|---|
| Spring Boot | X | X | X |
| Spring MVC (REST) | X | X | X |
| Spring Data JPA | | X | |
| Spring Data MongoDB | X | | |
| MySQL | | X | |
| MongoDB | X | | |
| Spring Batch | | | X |
| JUnit 5 | X | X | X |
| Mockito | X | X | X |
| MockMvc | X | X | X |
| JaCoCo (>= 80%) | X | X | X |
| Swagger/OpenAPI | X | X | X |
| Maven | X | X | X |
| Git | X | X | X |
| Docker Compose | X | X | X |
