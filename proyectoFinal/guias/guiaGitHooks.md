# Git Hooks - Automatizando la calidad del codigo

## Para que esta guia?

Durante el desarrollo de TechShop tuvimos un problema real: el schema de MySQL
(`init.sql`) y las entidades JPA se desincronizaron. La app no arrancaba en
produccion porque las columnas tenian nombres diferentes entre el SQL y el codigo Java.

Lo mas grave es que **teniamos tests y no lo detectaron**. Esta guia explica por que
fallaron los tests, como lo resolvimos con un Git Hook, y que es un Git Hook en general.

---

## El problema que originó todo esto

### Que paso en el commit `b562b99`

Teniamos tres desajustes entre `init.sql` (MySQL) y las entidades JPA (Java):

| Tabla | SQL tenia | La entidad JPA esperaba |
|---|---|---|
| `carrito_items` | `producto_nombre` | `nombre_producto` |
| `carrito_items` | `producto_precio` | `precio_unitario` |
| `carrito_items` | columna `fecha_agregado` | no existia en la entidad |
| `ordenes` | `estatus` | `estado` |
| tabla | `orden_detalle` | `orden_detalles` |

Hibernate al arrancar compara las entidades con el schema real de la base de datos.
Si encuentra una columna con nombre diferente, lanza una excepcion y la app no inicia.

### Por que los tests no lo detectaron?

Teniamos mas de 100 tests. Pero ninguno usaba el schema real:

**Tests de controlador (`@WebMvcTest`):**
```java
@WebMvcTest(CarritoController.class)
class CarritoControllerTest {
    @MockBean
    private CarritoService carritoService; // servicio simulado, no hay BD
}
```
Prueban solo la capa HTTP. No tocan base de datos.

**Tests de servicio (`@ExtendWith(MockitoExtension.class)`):**
```java
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {
    @Mock
    private CarritoItemRepository carritoItemRepository; // repositorio simulado
}
```
Mockean los repositorios. No tocan base de datos.

**Test de integracion (`TechShopApplicationTests`):**
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop  ← EL PROBLEMA
```
Con `create-drop`, Hibernate **genera el schema leyendo las entidades JPA**,
ignorando completamente `init.sql`. Entonces nunca habia conflicto — Hibernate
creaba exactamente lo que las entidades pedian.

**Conclusion:** Los tests eran correctos para lo que probaban, pero ninguno
verificaba que `init.sql` y las entidades JPA fueran consistentes.

---

## La solucion: dos partes

### Parte 1 — SchemaValidacionTest

Creamos un test dedicado con un nuevo perfil `schemaval`:

```properties
# application-schemaval.properties
spring.sql.init.schema-locations=classpath:schema-h2.sql  ← carga el SQL real
spring.jpa.hibernate.ddl-auto=validate                    ← Hibernate solo valida
```

Con `validate`, Hibernate no genera nada. Lee el schema que ya existe (creado desde
`schema-h2.sql`) y verifica que cada `@Column` y `@Table` de las entidades
corresponda exactamente a una columna/tabla en la BD. Si no coincide, falla.

```java
@SpringBootTest
@ActiveProfiles("schemaval")
class SchemaValidacionTest {

    @Test
    void schemaCoincideConEntidadesJPA() {
        // Si el contexto arranca sin excepcion, todo es consistente
    }
}
```

Este test habria detectado el error de `b562b99` inmediatamente.

Pero quedaba un problema: cada vez que alguien modifica `init.sql`, tiene que
acordarse de actualizar tambien `schema-h2.sql`. Eso es propenso al error humano.

### Parte 2 — El Git Hook (automatizacion)

---

## Que es un Git Hook?

Un **Git Hook** es un script que Git ejecuta automaticamente en momentos especificos
del flujo de trabajo: antes de un commit, despues de un push, antes de un merge, etc.

Son archivos ejecutables que viven en la carpeta oculta `.git/hooks/` del repositorio:

```
academiafebrero26CDMX/
└── .git/
    └── hooks/
        ├── pre-commit        ← se ejecuta ANTES de cada commit
        ├── commit-msg        ← se ejecuta al escribir el mensaje del commit
        ├── pre-push          ← se ejecuta antes de hacer push
        └── ...otros
```

Git trae ejemplos desactivados (con extension `.sample`). Para activar uno,
se crea el archivo sin esa extension y se le dan permisos de ejecucion.

### Tipos de hooks mas utiles

| Hook | Cuando se ejecuta | Uso comun |
|---|---|---|
| `pre-commit` | Antes de crear el commit | Linters, formateo, validaciones |
| `commit-msg` | Al escribir el mensaje | Validar formato del mensaje |
| `pre-push` | Antes de hacer push | Correr tests completos |
| `post-commit` | Despues del commit | Notificaciones |

Si un hook termina con codigo de salida distinto de 0 (`exit 1`), **Git aborta
la operacion**. Esto permite bloquear commits que no cumplan ciertas condiciones.

---

## El hook que implementamos: `pre-commit`

### Ubicacion
```
.git/hooks/pre-commit
```

### Que hace

1. Detecta si `init.sql` esta siendo commiteado (en el stage)
2. Si si, ejecuta una transformacion automatica para generar `schema-h2.sql`
3. Agrega `schema-h2.sql` al mismo commit

Si `init.sql` no esta en el stage, el hook no hace nada (termina con `exit 0`).

### El script completo

```bash
#!/usr/bin/env bash
# Sincroniza schema-h2.sql con init.sql automaticamente.

INIT_SQL="proyectos/proyectoFinal/db/mysql/init.sql"
SCHEMA_H2="proyectos/proyectoFinal/techshop/src/test/resources/schema-h2.sql"

# Solo actuar si init.sql esta siendo commiteado
if ! git diff --cached --name-only | grep -q "$INIT_SQL"; then
  exit 0
fi

echo "[pre-commit] init.sql modificado — regenerando schema-h2.sql..."

# Transformar init.sql a sintaxis H2:
#   1. Eliminar "USE <db>;"
#   2. Eliminar COMMENT '...' de definiciones de columna
sed \
  -e '/^USE /d' \
  -e "s/ COMMENT '[^']*'//" \
  "$INIT_SQL" \
| awk '...' > "$SCHEMA_H2"

git add "$SCHEMA_H2"
echo "[pre-commit] schema-h2.sql actualizado y agregado al commit."
```

### Por que H2 necesita una version diferente del SQL?

`init.sql` usa sintaxis especifica de MySQL que H2 no entiende:

```sql
-- MySQL (init.sql) — no funciona en H2
USE techshop;

CREATE TABLE carrito_items (
    producto_id VARCHAR(24) NOT NULL COMMENT 'ObjectId de MongoDB',
    ...
);
```

```sql
-- H2 (schema-h2.sql) — generado automaticamente por el hook
CREATE TABLE carrito_items (
    producto_id VARCHAR(24) NOT NULL,
    ...
);
```

Las diferencias que el hook corrige automaticamente:

| MySQL | H2 | Como lo resuelve el hook |
|---|---|---|
| `USE techshop;` | no existe | `sed -e '/^USE /d'` lo elimina |
| `COMMENT 'texto'` en columnas | no soportado | `sed` lo elimina |
| Encabezado del archivo | diferente | `awk` lo reemplaza |

---

## Flujo completo con el hook activo

```
1. Modificas init.sql (agregas una tabla, renombras una columna, etc.)

2. git add init.sql
   git commit -m "feat: agregar tabla pagos"

3. Git ejecuta .git/hooks/pre-commit automaticamente:
   → Detecta init.sql en el stage
   → Genera schema-h2.sql (sin sintaxis MySQL)
   → Hace git add schema-h2.sql

4. El commit queda con AMBOS archivos:
   - init.sql (modificado por ti)
   - schema-h2.sql (generado automaticamente)

5. mvn test ejecuta SchemaValidacionTest:
   → H2 crea las tablas desde schema-h2.sql
   → Hibernate valida entidades contra ese schema
   → Si hay desajuste → TEST FALLA → detectado antes del deploy
   → Si todo coincide → TEST PASA → seguro hacer deploy
```

---

## Como instalar el hook en una maquina nueva

Los hooks viven en `.git/hooks/` que **no se sube a GitHub** (es una carpeta local
de Git). Si clonas el repositorio en otra maquina, el hook no estara instalado.

Para instalarlo manualmente:

```bash
# Opcion 1: copiar el script directamente
cp scripts/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# Opcion 2: crear un symlink (se actualiza automaticamente si el script cambia)
ln -s ../../scripts/pre-commit .git/hooks/pre-commit
```

> **Nota para el curso:** En proyectos de equipo grandes se usan herramientas como
> `husky` (JavaScript) o `pre-commit` (Python) para distribuir los hooks junto
> con el repositorio. Para un proyecto Java escolar, el enfoque manual es suficiente.

---

## Leccion aprendida

**Los tests unitarios y de controlador prueban el comportamiento del codigo,
no la infraestructura.** Para detectar errores de schema necesitamos un test
de integracion que use el SQL real con `ddl-auto=validate`.

La combinacion que implementamos garantiza que el error de `b562b99` nunca
vuelva a pasar desapercibido:

```
init.sql  ──[hook]──▶  schema-h2.sql  ──[SchemaValidacionTest]──▶  Hibernate validate
   ↑                         ↑                                              ↑
fuente de                generado                                    falla si no
  verdad               automaticamente                                  coincide
```
