# Proyecto Final - Ecommerce

## Contexto
Proyecto final del curso de Java (academia BBVA). El objetivo es integrar todas las tecnologias
aprendidas en los modulos 5 y 6 en una aplicacion real: un ecommerce.

Autor: Miguel Rugerio (miguel.rugerio@outlook.com)
Fecha de inicio: 2026-03-04

---

## Decisiones Tomadas

### D001 - Stack tecnologico base
- **Fecha:** 2026-03-04
- **决:** Java 17 + Spring Boot 3.x + Maven
- **Razon:** Java 17 es LTS, compatible con Spring Boot 3.x, y es la version usada durante el curso.

### D002 - Bases de datos: MySQL + MongoDB
- **Fecha:** 2026-03-04
- **Decision:** Usar ambas bases de datos
  - **MySQL** → Datos transaccionales (usuarios, carrito, ordenes/pedidos)
  - **MongoDB** → Catalogo de productos (esquema flexible por categoria)
- **Razon:** Demostrar dominio de ambas tecnologias vistas en el curso. MongoDB permite que
  cada categoria de producto tenga atributos distintos sin alterar el esquema.

### D003 - Incluir Spring Batch
- **Fecha:** 2026-03-04
- **Decision:** Si, incluir un job de Spring Batch para carga masiva de productos desde CSV.
- **Razon:** Integrar Spring Batch en un caso de uso real del ecommerce.

### D004 - Documentacion con Swagger/OpenAPI
- **Fecha:** 2026-03-04
- **Decision:** Incluir Swagger para documentacion interactiva de la API REST.
- **Razon:** Ya se uso en ejercicios anteriores del curso. Facilita la prueba y demostracion de endpoints.

### D005 - Testing con JUnit 5 + Mockito
- **Fecha:** 2026-03-04
- **Decision:** Tests unitarios con Mockito para la capa de servicios.
- **Razon:** Tecnologias cubiertas en el modulo 5.

### D006 - Infraestructura con Docker Compose
- **Fecha:** 2026-03-04
- **Decision:** Usar Docker Compose para levantar MySQL 8.0 y MongoDB 7.0 en contenedores.
  - Puerto MySQL: 3307 (para no conflictar con instalaciones locales)
  - Puerto MongoDB: 27017
  - Volumenes persistentes para que los datos sobrevivan reinicios
- **Razon:** No contaminar el ambiente local. Un solo comando (`docker-compose up -d`) levanta
  toda la infraestructura. Ya se tiene Docker funcionando en el equipo.

### D007 - Scripts de inicializacion automatica de BD
- **Fecha:** 2026-03-04
- **Decision:** Scripts en `db/mysql/init.sql` y `db/mongodb/init.js` que se ejecutan
  automaticamente solo la primera vez que se crean los contenedores (via docker-entrypoint-initdb.d).
  - MySQL: crea tablas `usuarios`, `carrito_items`, `ordenes`, `orden_detalle`
  - MongoDB: crea usuario de app, collections `categorias` y `productos`, indices y categorias iniciales
- **Razon:** Automatizar la creacion de estructuras de BD sin pasos manuales.
  Los datos persisten en volumenes Docker y los scripts no se re-ejecutan en reinicios.

### D008 - Estrategia de testing desde Sprint 1
- **Fecha:** 2026-03-05
- **Decision:** Adoptar testing continuo desde el Sprint 1, no dejarlo para el final.
  - **JUnit 5 + Mockito** para tests unitarios de servicios (capa con logica de negocio)
  - **MockMvc** para tests de integracion de controladores REST
  - **JaCoCo** como plugin Maven para medir cobertura, con umbral minimo de **80%** (el build falla si no se cumple)
  - **No se usa Testcontainers** por ahora — los tests de repositorios no son prioridad ya que Spring Data genera la implementacion
- **Prioridad de testing por capa:**
  - Servicios: 90%+ (logica de negocio)
  - Controladores: 70-80% (happy path + errores principales)
  - Modelos/DTOs: no se persigue coverage (son datos, no logica)
- **Definition of Done:** Cada historia de usuario debe incluir sus tests unitarios correspondientes con coverage >= 80% para considerarse terminada
- **Razon:** Shift-left testing — detectar bugs cuando son baratos de corregir. El testing no es una fase separada, es parte del desarrollo diario.

### D009 - Liberaciones incrementales con Docker
- **Fecha:** 2026-03-05
- **Decision:** Dockerizar la aplicacion Spring Boot para entregar un incremento funcional al final de cada sprint.
  - **Dockerfile** multi-stage: etapa de build (Maven) + etapa de ejecucion (JRE)
  - **Servicio `app`** agregado al docker-compose.yml — un solo `docker compose up -d` levanta app + MySQL + MongoDB
  - **Versionado por sprint:** tag de Maven y tag de Git alineados
    - Sprint 1: `v1.0.0` (catalogo)
    - Sprint 2: `v2.0.0` (compra)
    - Sprint 3: `v3.0.0` (batch + calidad)
  - **Flujo de entrega:** el evaluador/usuario final solo necesita Docker instalado, clona el repo, ejecuta `docker compose up -d` y abre Swagger para probar
- **Razon:** En Scrum, cada sprint debe producir un incremento potencialmente entregable. Dockerizar la app permite que cualquier persona pruebe el producto sin instalar Java, Maven ni configurar bases de datos.

---

## Modulos del Ecommerce

| Modulo | BD | Descripcion |
|---|---|---|
| Usuarios | MySQL | Registro y autenticacion basica |
| Productos | MongoDB | CRUD completo con categorias |
| Carrito | MySQL | Agregar/quitar items, asociado a usuario |
| Ordenes | MySQL | Crear pedido desde carrito |
| Batch | - | Importar productos desde CSV |

## Endpoints REST planificados

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | /api/usuarios/registro | Registrar usuario |
| GET | /api/productos | Listar productos |
| GET | /api/productos/{id} | Detalle de producto |
| POST | /api/productos | Crear producto |
| PUT | /api/productos/{id} | Actualizar producto |
| DELETE | /api/productos/{id} | Eliminar producto |
| POST | /api/carrito/agregar | Agregar item al carrito |
| DELETE | /api/carrito/{itemId} | Quitar item del carrito |
| GET | /api/carrito/{usuarioId} | Ver carrito del usuario |
| POST | /api/ordenes | Crear orden desde carrito |
| GET | /api/ordenes/{id} | Consultar orden |

---

## Registro de Progreso

- [x] Docker Compose + scripts de inicializacion de BD
- [x] Estructura inicial del proyecto (Maven, dependencias)
- [x] Entidades y repositorios
- [x] Servicios
- [x] Controladores REST
- [x] Spring Batch - job de carga de productos
- [x] Tests unitarios (127 tests, coverage >= 80%)
- [x] Configuracion Swagger (v3.0.0 completa)
- [x] Pruebas de integracion
