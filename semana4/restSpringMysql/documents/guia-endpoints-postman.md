# API REST Alumnos - Guia para probar con Postman

Base URL: `http://localhost:8080`

---

## 1. Listar todos los alumnos

```
GET http://localhost:8080/api/alumnos
```

Respuesta esperada (200 OK):
```json
[
    {
        "id": 1,
        "nombre": "Juan",
        "apellido": "Lopez",
        "email": "juan@mail.com",
        "fechaRegistro": "2026-02-13"
    },
    {
        "id": 2,
        "nombre": "Maria",
        "apellido": "Garcia",
        "email": "maria@mail.com",
        "fechaRegistro": "2026-02-13"
    }
]
```

---

## 2. Buscar alumno por ID

```
GET http://localhost:8080/api/alumnos/1
```

Respuesta esperada (200 OK):
```json
{
    "id": 1,
    "nombre": "Juan",
    "apellido": "Lopez",
    "email": "juan@mail.com",
    "fechaRegistro": "2026-02-13"
}
```

Si el ID no existe devuelve **404 Not Found**.

---

## 3. Crear un alumno

```
POST http://localhost:8080/api/alumnos
```

En Postman:
1. Selecciona metodo **POST**
2. Ve a la pestaña **Body**
3. Selecciona **raw** y formato **JSON**
4. Pega el JSON:

```json
{
    "nombre": "Pedro",
    "apellido": "Ruiz",
    "email": "pedro@mail.com"
}
```

Respuesta esperada (201 Created):
```json
{
    "id": 4,
    "nombre": "Pedro",
    "apellido": "Ruiz",
    "email": "pedro@mail.com",
    "fechaRegistro": "2026-02-13"
}
```

La `fechaRegistro` se asigna automaticamente.

---

## 4. Actualizar un alumno

```
PUT http://localhost:8080/api/alumnos/4
```

En Postman:
1. Selecciona metodo **PUT**
2. Ve a la pestaña **Body**
3. Selecciona **raw** y formato **JSON**
4. Pega el JSON con los datos actualizados:

```json
{
    "nombre": "Pedro",
    "apellido": "Ruiz Martinez",
    "email": "pedro.nuevo@mail.com"
}
```

Respuesta esperada (200 OK):
```json
{
    "id": 4,
    "nombre": "Pedro",
    "apellido": "Ruiz Martinez",
    "email": "pedro.nuevo@mail.com",
    "fechaRegistro": "2026-02-13"
}
```

Si el ID no existe devuelve **404 Not Found**.

---

## 5. Eliminar un alumno

```
DELETE http://localhost:8080/api/alumnos/4
```

Respuesta esperada: **204 No Content** (sin body).

Si el ID no existe devuelve **404 Not Found**.

---

## Resumen de codigos HTTP

| Codigo | Significado | Cuando ocurre |
|--------|-------------|---------------|
| 200 | OK | GET y PUT exitosos |
| 201 | Created | POST exitoso |
| 204 | No Content | DELETE exitoso |
| 404 | Not Found | ID no existe |
