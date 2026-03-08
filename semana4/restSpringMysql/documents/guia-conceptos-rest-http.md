# Conceptos REST y HTTP

---

## ¿Que es una API REST?

Una API REST es un servicio que permite a diferentes aplicaciones comunicarse entre si a traves de HTTP (el mismo protocolo que usa tu navegador).

Ejemplo real: cuando una app movil muestra una lista de alumnos, no tiene los datos guardados localmente. Le pide los datos a un servidor a traves de una API REST.

```
App movil  →  GET /api/alumnos  →  Servidor  →  MySQL
App movil  ←  JSON con alumnos  ←  Servidor  ←  MySQL
```

REST no es una libreria ni un framework. Es un estilo de arquitectura que define reglas sobre como deben comunicarse el cliente y el servidor.

---

## ¿Que es HTTP?

HTTP (HyperText Transfer Protocol) es el protocolo de comunicacion. Define:
- **Como pedir** datos (metodos)
- **A donde** pedirlos (URL)
- **Que responder** (codigos de estado + body)

---

## Metodos HTTP

Cada metodo representa una accion diferente sobre un recurso.

| Metodo | Accion | Ejemplo | ¿Envia body? |
|--------|--------|---------|--------------|
| GET | Obtener datos | Dame todos los alumnos | No |
| POST | Crear un recurso nuevo | Crea este alumno | Si |
| PUT | Actualizar un recurso existente | Actualiza este alumno | Si |
| DELETE | Eliminar un recurso | Elimina este alumno | No |

### GET - Obtener

No modifica nada. Solo lee.

```
GET /api/alumnos          →  Todos los alumnos
GET /api/alumnos/1        →  El alumno con id 1
```

### POST - Crear

Envia datos en el body para crear un recurso nuevo.

```
POST /api/alumnos
Body: { "nombre": "Pedro", "apellido": "Ruiz", "email": "pedro@mail.com" }

→ Crea el alumno y devuelve el objeto con su id asignado
```

### PUT - Actualizar

Envia datos en el body para reemplazar un recurso existente.

```
PUT /api/alumnos/1
Body: { "nombre": "Juan", "apellido": "Lopez", "email": "juan.nuevo@mail.com" }

→ Actualiza el alumno con id 1
```

### DELETE - Eliminar

No necesita body. Solo el id en la URL.

```
DELETE /api/alumnos/1

→ Elimina el alumno con id 1
```

---

## ¿Que es una URL en REST?

La URL identifica el recurso con el que quieres interactuar.

```
http://localhost:8080/api/alumnos/1
│        │         │    │    │     │
│        │         │    │    │     └─ ID del recurso
│        │         │    │    └─────── Recurso (alumnos)
│        │         │    └──────────── Prefijo de la API
│        │         └───────────────── Puerto
│        └─────────────────────────── Host (servidor)
└──────────────────────────────────── Protocolo
```

### Convencion de nombres

| URL | Representa |
|-----|------------|
| `/api/alumnos` | La coleccion completa de alumnos |
| `/api/alumnos/1` | Un alumno especifico |
| `/api/cursos` | La coleccion completa de cursos |
| `/api/cursos/5` | Un curso especifico |

Reglas:
- Siempre en **plural** (alumnos, no alumno)
- Siempre en **minusculas**
- Sin verbos en la URL (la accion la define el metodo HTTP)

Correcto: `GET /api/alumnos`
Incorrecto: `GET /api/obtenerAlumnos`

---

## Codigos de estado HTTP

El servidor responde con un codigo numerico que indica que paso con la peticion.

### 2xx - Todo bien

| Codigo | Nombre | Cuando se usa |
|--------|--------|---------------|
| 200 | OK | GET y PUT exitosos |
| 201 | Created | POST exitoso, se creo el recurso |
| 204 | No Content | DELETE exitoso, no hay nada que devolver |

### 4xx - Error del cliente

| Codigo | Nombre | Cuando se usa |
|--------|--------|---------------|
| 400 | Bad Request | El JSON esta mal formado o faltan campos obligatorios |
| 404 | Not Found | El recurso no existe (ejemplo: alumno con id 999) |
| 405 | Method Not Allowed | Usaste un metodo no soportado (ejemplo: PATCH cuando solo existe PUT) |

### 5xx - Error del servidor

| Codigo | Nombre | Cuando se usa |
|--------|--------|---------------|
| 500 | Internal Server Error | Error en el codigo del servidor (excepcion no controlada) |
| 503 | Service Unavailable | El servidor esta caido o sobrecargado |

### Como recordarlos

- **2xx** = Todo salio bien
- **4xx** = Tu (el cliente) hiciste algo mal
- **5xx** = El servidor tiene un problema

---

## ¿Que es JSON?

JSON (JavaScript Object Notation) es el formato en que viajan los datos entre el cliente y el servidor.

### Un objeto

```json
{
    "id": 1,
    "nombre": "Juan",
    "apellido": "Lopez",
    "email": "juan@mail.com"
}
```

### Una lista de objetos

```json
[
    { "id": 1, "nombre": "Juan", "apellido": "Lopez" },
    { "id": 2, "nombre": "Maria", "apellido": "Garcia" }
]
```

### Reglas de JSON

- Las llaves (keys) van entre **comillas dobles**: `"nombre"`
- Los textos van entre **comillas dobles**: `"Juan"`
- Los numeros van **sin comillas**: `1`, `25.5`
- Los booleanos son `true` o `false` (sin comillas)
- Null es `null` (sin comillas)

### JSON vs Java

| JSON | Java |
|------|------|
| `{ "nombre": "Juan" }` | `alumno.getNombre()` → `"Juan"` |
| `"id": 1` | `alumno.getId()` → `1` |
| `null` | `null` |
| `[...]` | `List<Alumno>` |

Spring Boot convierte automaticamente entre JSON y objetos Java. Tu no escribes ese codigo.

---

## Headers importantes

Los headers son metadatos que acompañan la peticion o la respuesta.

| Header | Valor | Para que sirve |
|--------|-------|----------------|
| `Content-Type` | `application/json` | Indica que el body esta en formato JSON |
| `Accept` | `application/json` | Indica que el cliente espera recibir JSON |

En Postman, cuando seleccionas **raw > JSON** en el body, el header `Content-Type: application/json` se agrega automaticamente.

En curl se agrega manualmente:

```bash
curl -X POST http://localhost:8080/api/alumnos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro","apellido":"Ruiz"}'
```

---

## Resumen

| Concepto | Que es |
|----------|--------|
| API REST | Servicio que permite comunicar aplicaciones via HTTP |
| HTTP | Protocolo de comunicacion (metodos + URLs + codigos) |
| GET/POST/PUT/DELETE | Acciones sobre los recursos |
| URL | Identificador del recurso |
| Codigo de estado | Numero que indica el resultado (200, 404, 500...) |
| JSON | Formato de los datos que viajan entre cliente y servidor |
| Header | Metadatos de la peticion/respuesta |
