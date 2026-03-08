# Guia de Docker Compose para el Proyecto Final

## Que es Docker?

Docker es una herramienta que permite ejecutar aplicaciones dentro de **contenedores**.
Un contenedor es como una "caja" aislada que contiene todo lo necesario para ejecutar
una aplicacion: sistema operativo, librerias, configuracion y la aplicacion misma.

**Analogia:** Imagina que compras un mueble de IKEA. La caja trae todas las piezas,
tornillos e instrucciones. No necesitas ir a la ferreteria por piezas adicionales.
Un contenedor Docker funciona igual: trae todo incluido.

### Ventajas de usar Docker

- **No contaminas tu maquina** — MySQL y MongoDB corren dentro de contenedores, no se instalan en tu sistema
- **Reproducible** — Todos los estudiantes tienen exactamente el mismo ambiente
- **Facil de iniciar/detener** — Un comando levanta todo, otro comando lo detiene
- **Datos persistentes** — Los datos se guardan en volumenes aunque detengas los contenedores

---

## Que es Docker Compose?

Docker Compose es una herramienta que permite definir y ejecutar **multiples contenedores**
con un solo archivo de configuracion (`docker-compose.yml`).

Sin Docker Compose, tendrias que ejecutar un comando largo por cada contenedor:

```bash
# Sin Docker Compose (tedioso y propenso a errores)
docker run -d --name techshop-mysql -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=techshop -p 3307:3306 mysql:8.0
docker run -d --name techshop-mongodb -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=root123 -p 27017:27017 mongo:7.0
```

Con Docker Compose, todo se define en un archivo YAML y se levanta con un solo comando:

```bash
# Con Docker Compose (simple y limpio)
docker compose up -d
```

---

## Estructura de nuestro docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:                                    # Nombre del servicio
    image: mysql:8.0                        # Imagen de Docker Hub
    container_name: techshop-mysql          # Nombre del contenedor
    environment:                            # Variables de entorno
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: techshop
      MYSQL_USER: techshop_user
      MYSQL_PASSWORD: techshop_pass
    ports:
      - "3307:3306"                         # Puerto local:puerto contenedor
    volumes:
      - mysql_data:/var/lib/mysql           # Volumen para persistir datos
      - ./db/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql  # Script inicial

  mongodb:
    image: mongo:7.0
    container_name: techshop-mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: root123
      MONGO_INITDB_DATABASE: techshop
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db
      - ./db/mongodb/init.js:/docker-entrypoint-initdb.d/init.js

volumes:                                    # Definicion de volumenes
  mysql_data:
  mongo_data:
```

### Conceptos clave del archivo

| Concepto | Que hace | Ejemplo |
|---|---|---|
| `image` | Imagen base del contenedor (se descarga de Docker Hub) | `mysql:8.0` |
| `container_name` | Nombre que le das al contenedor | `techshop-mysql` |
| `environment` | Variables de configuracion (usuario, password, etc.) | `MYSQL_DATABASE: techshop` |
| `ports` | Mapeo de puertos: `"local:contenedor"` | `"3307:3306"` |
| `volumes` | Donde se guardan los datos para que persistan | `mysql_data:/var/lib/mysql` |
| `docker-entrypoint-initdb.d` | Carpeta especial: scripts aqui se ejecutan solo la primera vez | `init.sql`, `init.js` |

### Por que el puerto 3307 y no 3306?

MySQL por defecto usa el puerto 3306. Si ya tuvieras un MySQL instalado en tu maquina,
habria un conflicto de puertos. Usamos 3307 en el host para evitar eso.
Dentro del contenedor sigue siendo 3306, pero desde tu maquina te conectas al 3307.

```
Tu maquina (localhost:3307) --> Contenedor Docker (mysql:3306)
```

---

## Comandos esenciales de Docker Compose

**IMPORTANTE:** Todos los comandos se ejecutan desde la carpeta donde esta el archivo
`docker-compose.yml`:

```bash
cd proyectos/proyectoFinal
```

### Levantar los contenedores

```bash
# Levantar en segundo plano (-d = detached)
docker compose up -d

# Levantar viendo los logs en tiempo real (util para depurar)
docker compose up
```

La primera vez descarga las imagenes de MySQL y MongoDB (puede tardar unos minutos).
Las siguientes veces inicia en segundos.

### Ver el estado de los contenedores

```bash
# Ver contenedores corriendo
docker compose ps

# Ejemplo de salida:
# NAME               STATUS         PORTS
# techshop-mysql     Up 2 minutes   0.0.0.0:3307->3306/tcp
# techshop-mongodb   Up 2 minutes   0.0.0.0:27017->27017/tcp
```

### Ver los logs

```bash
# Ver logs de todos los servicios
docker compose logs

# Ver logs de un servicio especifico
docker compose logs mysql
docker compose logs mongodb

# Ver logs en tiempo real (seguimiento)
docker compose logs -f mysql
```

### Detener los contenedores

```bash
# Detener sin eliminar (los datos se mantienen)
docker compose stop

# Detener y eliminar contenedores (los datos se mantienen en volumenes)
docker compose down

# Detener, eliminar contenedores Y eliminar datos (RESET COMPLETO)
docker compose down -v
```

### Reiniciar los contenedores

```bash
# Reiniciar todos
docker compose restart

# Reiniciar solo uno
docker compose restart mysql
```

---

## Ciclo de vida de los datos

```
docker compose up -d     -->  Crea contenedores, ejecuta scripts init (primera vez)
       |
docker compose stop      -->  Detiene contenedores, DATOS SE MANTIENEN
       |
docker compose up -d     -->  Levanta contenedores, NO re-ejecuta scripts, datos intactos
       |
docker compose down      -->  Elimina contenedores, DATOS SE MANTIENEN en volumenes
       |
docker compose down -v   -->  Elimina TODO incluyendo datos (reset completo)
```

---

## Conectarse a las bases de datos

### Conectarse a MySQL desde el contenedor

```bash
# Entrar al shell de MySQL
docker exec -it techshop-mysql mysql -utechshop_user -ptechshop_pass techshop

# Ya dentro de MySQL puedes ejecutar queries:
# mysql> SHOW TABLES;
# mysql> SELECT * FROM usuarios;
# mysql> exit
```

### Conectarse a MongoDB desde el contenedor

```bash
# Entrar al shell de MongoDB
docker exec -it techshop-mongodb mongosh -u techshop_user -p techshop_pass --authenticationDatabase techshop techshop

# Ya dentro de mongosh puedes ejecutar queries:
# techshop> db.categorias.find()
# techshop> db.productos.find()
# techshop> exit
```

### Datos de conexion para la aplicacion Spring Boot

| Base de datos | Propiedad | Valor |
|---|---|---|
| **MySQL** | URL | `jdbc:mysql://localhost:3307/techshop` |
| **MySQL** | Usuario | `techshop_user` |
| **MySQL** | Password | `techshop_pass` |
| **MongoDB** | URI | `mongodb://techshop_user:techshop_pass@localhost:27017/techshop` |
| **MongoDB** | Database | `techshop` |

---

## Comandos de referencia rapida

| Que quiero hacer | Comando |
|---|---|
| Levantar todo | `docker compose up -d` |
| Ver estado | `docker compose ps` |
| Ver logs | `docker compose logs -f` |
| Detener todo | `docker compose stop` |
| Detener y limpiar contenedores | `docker compose down` |
| Reset completo (borrar datos) | `docker compose down -v` |
| Reiniciar | `docker compose restart` |
| Entrar a MySQL | `docker exec -it techshop-mysql mysql -utechshop_user -ptechshop_pass techshop` |
| Entrar a MongoDB | `docker exec -it techshop-mongodb mongosh -u techshop_user -p techshop_pass --authenticationDatabase techshop techshop` |

---

## Troubleshooting (problemas comunes)

### "port is already allocated"
Otro proceso esta usando el puerto. Verifica con:
```bash
lsof -i :3307    # Para MySQL
lsof -i :27017   # Para MongoDB
```

### "Cannot connect to the Docker daemon"
Docker Desktop no esta corriendo. Abre la aplicacion Docker Desktop.

### MySQL tarda en iniciar
MySQL necesita unos 15-20 segundos para estar listo la primera vez.
Verifica con:
```bash
docker compose logs mysql | tail -5
# Busca: "ready for connections"
```

### Quiero empezar de cero
```bash
docker compose down -v    # Elimina contenedores y volumenes
docker compose up -d      # Crea todo desde cero, re-ejecuta scripts init
```
