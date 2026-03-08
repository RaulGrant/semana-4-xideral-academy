# Guia de Scrum - Aprendiendo mientras desarrollamos TechShop

## Que es Scrum?

Scrum es un marco de trabajo agil para desarrollar software de forma incremental.
En lugar de intentar construir todo de una vez, divides el trabajo en ciclos cortos
llamados **sprints**, y al final de cada uno entregas algo funcional.

---

## Conceptos aprendidos

### Epic

Un **Epic** es un objetivo grande que NO puedes completar en un solo sprint.
Funciona como un "tema" o "categoria de trabajo" que agrupa varias historias de usuario.

Ejemplo en TechShop:
- TECH-1: "Sprint 1 - Los cimientos y el catalogo" (Epic)
- TECH-2: "Sprint 2 - La experiencia de compra" (Epic)
- TECH-3: "Sprint 3 - Batch, testing y calidad" (Epic)

### Historia de Usuario

Un pedazo de trabajo concreto que SI puedes completar en un sprint.
Se escribe desde la perspectiva de quien la necesita:

> *"Como **[quien]**, quiero **[que]** para **[por que]**"*

Ejemplos en TechShop:
- TECH-5: "Como admin, quiero gestionar categorias para organizar los productos"
- TECH-7: "Como cliente, quiero ver el catalogo de productos"

Nota: A veces hay historias tecnicas que no siguen el formato clasico,
como TECH-4: "Estructura del proyecto Spring Boot". Son trabajo necesario
pero no representan funcionalidad directa para un usuario.

### Epic vs Historia de Usuario

```
Epic (grande, no cabe en un sprint)
├── Historia de Usuario (mediana, cabe en un sprint)
├── Historia de Usuario
└── Historia de Usuario
```

La jerarquia en nuestro proyecto:

```
TECH-1 (Epic: "Catalogo")
├── TECH-4  Estructura Spring Boot
├── TECH-5  Gestionar categorias
├── TECH-6  CRUD productos
├── TECH-7  Ver catalogo
└── TECH-8  Configurar Swagger

TECH-2 (Epic: "Compra")
├── TECH-9  Registro de usuarios
├── TECH-10 Carrito de compras
├── TECH-11 Realizar pedido
├── TECH-12 Consultar ordenes
└── TECH-13 Swagger Sprint 2

TECH-3 (Epic: "Batch + Calidad")
├── TECH-14 Carga masiva CSV
├── TECH-15 Tests unitarios
├── TECH-16 Swagger final
└── TECH-17 Demo final
```

### Sprint

Periodo fijo de trabajo (tipicamente 1 a 4 semanas) donde el equipo se compromete
a entregar un conjunto de historias terminadas. Al final del sprint se hace una **demo**
para mostrar lo que funciona.

En TechShop tenemos 3 sprints:
- Sprint 1: Catalogo (MongoDB, productos, categorias)
- Sprint 2: Compra (usuarios, carrito, ordenes con MySQL)
- Sprint 3: Batch + Calidad (Spring Batch, testing, Swagger completo)

### Product Backlog

Lista **priorizada** de TODO el trabajo pendiente del proyecto. Lo mantiene
el Product Owner y contiene todas las historias de usuario ordenadas por prioridad.

En TechShop: las 14 historias (TECH-4 a TECH-17).

### Sprint Backlog

Subconjunto del Product Backlog que el equipo elige para trabajar en UN sprint especifico.

Ejemplo: el Sprint Backlog del Sprint 1 son TECH-4, TECH-5, TECH-6, TECH-7 y TECH-8.

### Estado de una Historia

Cada historia pasa por estos estados en Jira:

```
Tareas por hacer  -->  En progreso  -->  Listo
```

- **Tareas por hacer**: Aun no se empieza
- **En progreso**: Alguien esta trabajando en ella
- **Listo**: Completada y funcionando

### Demo

Sesion al final de cada sprint donde se muestra el software funcionando.
No es una presentacion de PowerPoint, es una demostracion en vivo.

Ejemplo Sprint 1: abrir Swagger, crear categorias, crear productos, listar por categoria.

### Incremento

El **Incremento** es el resultado tangible de un sprint: software funcionando que se
puede entregar al usuario. No es codigo a medias ni "falta solo el deploy" — es algo
que alguien puede ejecutar y probar.

Cada incremento se construye sobre el anterior:

```
Sprint 1 → Incremento v1.0.0 (catalogo funcionando)
Sprint 2 → Incremento v2.0.0 (catalogo + compra funcionando)
Sprint 3 → Incremento v3.0.0 (todo el ecommerce funcionando)
```

En TechShop, el incremento se entrega como un Docker Compose completo.
El evaluador ejecuta `docker compose up -d`, abre Swagger, y prueba todo lo
que se construyo hasta ese sprint. No necesita instalar Java, Maven ni bases de datos.

Esto se alinea con el principio de Scrum: *"El software funcionando es la medida
principal de progreso"*.

### Definition of Done (DoD)

Lista de criterios que una historia de usuario debe cumplir para considerarse **terminada**.
No basta con que "el codigo funcione" — debe cumplir un estandar de calidad.

En TechShop, nuestra Definition of Done es:

1. **Codigo implementado** — la funcionalidad que pide la historia esta completa
2. **Tests unitarios** — el servicio tiene tests con Mockito
3. **Tests de integracion** — el controlador tiene tests con MockMvc
4. **Coverage >= 80%** — medido con JaCoCo; si baja de 80%, el build falla
5. **Sin errores de compilacion** — `mvn clean verify` pasa correctamente
6. **Historia movida a "Listo"** en Jira

Por que importa el DoD? Porque evita el clasico "ya esta, solo falta testearlo"
que en realidad significa "no esta terminado". Si no tiene tests, no esta listo.

### Sprint Review (Revision del Sprint)

Ceremonia al final del sprint donde el equipo muestra el incremento al Product Owner
(o evaluador). No es una presentacion — es una **demostracion en vivo** del software
funcionando.

En TechShop Sprint 1:
- Abrir Swagger UI
- Crear categorias, crear productos, listar por categoria, ver detalle
- Mostrar reporte de JaCoCo con coverage >= 80%
- Mostrar que `docker compose up -d` levanta todo

### Sprint Retrospective (Retrospectiva)

Ceremonia interna del equipo (despues del Review) donde se reflexiona sobre el
**proceso**, no sobre el producto. Se responden 3 preguntas:

1. **Que salio bien?** (seguir haciendolo)
2. **Que salio mal?** (identificar problemas)
3. **Que podemos mejorar?** (acciones concretas)

Ejemplo Sprint 1:
- Bien: testing desde el inicio, Docker facilito la validacion
- Mal: el filtro por categoria se implemento diferente a la especificacion
  (path variable vs query param)
- Mejorar: revisar la descripcion de cada historia en Jira antes de implementar

### Velocity (Velocidad)

Cantidad de trabajo completado en un sprint, medido en **historias terminadas**
(o story points si se usan). Sirve para predecir cuanto trabajo cabe en sprints futuros.

Sprint 1: 5 historias completadas (TECH-4 a TECH-8).
Sprint 2: 5 historias completadas (TECH-9 a TECH-13).
Sprint 3: 4 historias completadas (TECH-14 a TECH-17).

### Cierre del Sprint

Pasos al terminar un sprint:

1. Todas las historias en "Hecho"
2. Epic del sprint movido a "Finalizada"
3. Sprint Review (demo)
4. Sprint Retrospective
5. Tag de version en Git (ej: `v1.0.0`)
6. Cerrar el sprint en Jira (las historias incompletas se mueven al siguiente)

### Transicion entre Sprints: del cierre al inicio

Cuando terminas un sprint y arrancas el siguiente, hay una secuencia de ceremonias
y artefactos que se ejecutan. No es simplemente "terminar uno y empezar otro" — hay
un proceso definido que asegura que el equipo aprenda del pasado y planifique bien
el futuro.

#### Ceremonias de cierre (Sprint que termina)

Ya las conocemos, se ejecutan en este orden:

1. **Sprint Review** — Demo en vivo del incremento al Product Owner/evaluador
2. **Sprint Retrospective** — Reflexion interna del equipo (que salio bien, mal, mejorar)

#### Ceremonias de inicio (Sprint que comienza)

**1. Backlog Refinement (Refinamiento del Backlog)**

Antes de planificar el nuevo sprint, el equipo revisa las historias candidatas del
Product Backlog para asegurarse de que estan listas:

- Las historias tienen descripcion clara y criterios de aceptacion?
- Son lo suficientemente pequenas para completarse en un sprint?
- Hay dependencias tecnicas que resolver primero?

En TechShop, antes de iniciar el Sprint 2 revisamos:
- TECH-9 a TECH-13 — estan bien definidas? tienen criterios claros?
- El Sprint 1 dejo algo pendiente que impacte al Sprint 2?

> En equipos grandes esto se hace en una sesion separada. En equipos pequenos
> (como el nuestro) se hace como parte de la planificacion.

**2. Sprint Planning (Planificacion del Sprint)**

La ceremonia mas importante del inicio. El equipo decide **que** va a construir
y **como** lo va a hacer. Tiene dos partes:

| Parte | Pregunta | Resultado |
|-------|----------|-----------|
| Parte 1 | **Que** vamos a entregar? | Sprint Goal + historias seleccionadas |
| Parte 2 | **Como** lo vamos a hacer? | Tareas tecnicas, dependencias, orden de trabajo |

**Parte 1 — Que:** Se seleccionan historias del Product Backlog y se establece
el **Sprint Goal** (objetivo del sprint).

**Parte 2 — Como:** Para cada historia se identifican las tareas tecnicas,
dependencias entre historias, y el orden optimo de desarrollo.

#### Artefactos que se producen

| Artefacto | Descripcion | Ejemplo Sprint 2 |
|-----------|-------------|-------------------|
| **Sprint Goal** | Objetivo en una frase que da direccion al sprint | "Implementar el flujo completo de compra" |
| **Sprint Backlog** | Historias seleccionadas para el sprint | TECH-9, TECH-10, TECH-11, TECH-12, TECH-13 |
| **Definition of Done** | Se revisa y ajusta si es necesario | Agregar nuevos criterios si aprendimos algo |

#### Flujo completo de la transicion

```
Sprint N terminado
    |
    v
[Sprint Review]        --> Demo del incremento
    |
    v
[Sprint Retrospective] --> Que mejorar para el siguiente
    |
    v
[Backlog Refinement]   --> Revisar historias candidatas
    |
    v
[Sprint Planning]      --> Seleccionar historias + definir Sprint Goal
    |
    v
Sprint N+1 inicia
    |
    v
[Desarrollo diario]    --> Tomar historia, implementar, mover a "Listo"
```

#### En Jira, los pasos concretos son:

1. **Cerrar Sprint N** — las historias incompletas se mueven automaticamente al backlog
2. **Mover Epic del Sprint N a "Finalizada"** (si todas sus historias estan listas)
3. **Iniciar Sprint N+1** — activar el sprint desde el board
4. **Mover las historias del Sprint N+1 a "En progreso"** conforme se van trabajando

#### Por que importa esta transicion?

Sin estas ceremonias, el equipo cae en el error de "sprint infinito" — donde un
sprint se funde con el siguiente sin detenerse a reflexionar ni planificar.
La pausa entre sprints es corta pero critica: te obliga a entregar (Review),
aprender (Retrospective) y planificar (Planning) antes de volver a construir.

---

## Flujo de trabajo que seguimos

```
1. Tomar una historia       -->  Moverla a "En progreso" en Jira
2. Desarrollar el codigo    -->  Implementar lo que pide la historia
3. Completar                -->  Moverla a "Listo" en Jira
4. Siguiente historia       -->  Repetir
5. Al terminar el sprint    -->  Demo de lo construido
```

---

## Diario de aprendizaje

| Fecha | Concepto | Contexto |
|---|---|---|
| 2026-03-05 | Epic, Historia de Usuario, Sprint, Backlog, Estado, Demo | Antes de iniciar TECH-4 |
| 2026-03-05 | Definition of Done (DoD) | Decidimos estrategia de testing desde Sprint 1 |
| 2026-03-05 | Incremento | Decidimos Dockerizar la app para entregas incrementales por sprint |
| 2026-03-05 | Sprint Review, Retrospectiva, Velocity, Cierre del Sprint | Al completar Sprint 1 |
| 2026-03-06 | Transicion entre Sprints, Refinamiento, Sprint Planning, Sprint Goal | Antes de iniciar Sprint 2 |
| 2026-03-06 | Proyecto completado: 3 sprints, 14 historias, 127 tests, JaCoCo >= 80% | Sprint 3 completado |

