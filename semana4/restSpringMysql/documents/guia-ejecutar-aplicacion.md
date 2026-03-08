# Guia para ejecutar la aplicacion REST Alumnos

---

## 1. Verificar prerequisitos

### Java 17

```bash
java -version
```

Debe mostrar version 17.x.x. Si no esta instalado, descargalo de [https://adoptium.net/](https://adoptium.net/).

### Maven

```bash
mvn -version
```

Debe mostrar version 3.x.x.

### Docker

```bash
docker --version
```

### Contenedor MySQL corriendo

```bash
docker ps --filter name=mysql-academia
```

Debe mostrar el contenedor con estado `Up`. Si no esta corriendo:

```bash
docker start mysql-academia
```

Si no existe el contenedor, crealo:

```bash
docker run --name mysql-academia -e MYSQL_ROOT_PASSWORD=root123 -p 3306:3306 -d mysql:8
```

---

## 2. Compilar el proyecto

Desde la carpeta del proyecto `restSpringMysql`:

```bash
mvn clean compile
```

Si es la primera vez, Maven descargara las dependencias (puede tardar unos minutos).

---

## 3. Ejecutar la aplicacion

```bash
mvn spring-boot:run
```

Espera hasta ver en consola:

```
Started RestSpringMysqlApplication in X.XX seconds
```

La aplicacion queda corriendo en `http://localhost:8080`.

---

## 4. Probar los endpoints desde otra terminal

Abre una nueva terminal (sin cerrar la anterior) y ejecuta:

### Listar todos los alumnos

```bash
curl http://localhost:8080/api/alumnos
```

### Buscar por ID

```bash
curl http://localhost:8080/api/alumnos/1
```

### Crear un alumno

```bash
curl -X POST http://localhost:8080/api/alumnos -H "Content-Type: application/json" -d "{\"nombre\":\"Pedro\",\"apellido\":\"Ruiz\",\"email\":\"pedro@mail.com\"}"
```

### Actualizar un alumno

```bash
curl -X PUT http://localhost:8080/api/alumnos/1 -H "Content-Type: application/json" -d "{\"nombre\":\"Juan\",\"apellido\":\"Lopez\",\"email\":\"juan.nuevo@mail.com\"}"
```

### Eliminar un alumno

```bash
curl -X DELETE http://localhost:8080/api/alumnos/4
```

---

## 5. Detener la aplicacion

En la terminal donde esta corriendo, presiona **Ctrl + C**.

---

## 6. Detener el contenedor MySQL

```bash
docker stop mysql-academia
```

Para levantarlo despues:

```bash
docker start mysql-academia
```

---

## Resolucion de problemas

| Error | Causa | Solucion |
|-------|-------|----------|
| `Connection refused` al ejecutar curl | La aplicacion no esta corriendo | Ejecuta `mvn spring-boot:run` |
| `Communications link failure` al iniciar | MySQL no esta corriendo | Ejecuta `docker start mysql-academia` |
| `Port 8080 already in use` | Otro proceso usa el puerto | Cierra el proceso o cambia el puerto en `application.properties` |
| `JAVA_HOME not set` | Java no esta configurado | Configura la variable de entorno JAVA_HOME apuntando a tu JDK 17 |
