# Guia de Git - Aprendiendo mientras desarrollamos TechShop

## Para que esta guia?

Durante el desarrollo de TechShop usamos Git no solo para guardar codigo, sino como
herramienta de colaboracion entre multiples desarrolladores (o agentes). Esta guia
documenta los conceptos que vamos aprendiendo en la practica.

---

## Conceptos basicos (usados desde Sprint 1)

### Repositorio

Un directorio que Git rastrea. Contiene todo el historial de cambios del proyecto.
Nuestro repositorio es `academiafebrero26CDMX/`.

### Commit

Una "foto" del estado del proyecto en un momento dado. Cada commit tiene:
- Un hash unico (ej: `318fbba`)
- Un mensaje que explica *que* se hizo y *por que*
- Un autor y una fecha

```bash
git commit -m "TECH-5: Modulo de categorias con tests (MongoDB)"
```

### Branch (Rama)

Una linea independiente de desarrollo. Por defecto existe `master` (o `main`).
Hasta el Sprint 1, trabajamos todo en `master` porque eramos un solo desarrollador.

```
master: ──o──o──o──o──o  (todos los commits en una sola linea)
```

---

## Conceptos de colaboracion (Sprint 2)

### El problema: multiples desarrolladores en el mismo codigo

Cuando dos personas (o dos agentes) trabajan al mismo tiempo en el mismo proyecto,
pueden pisar el trabajo del otro. Imagina que ambos editan el mismo archivo
simultaneamente — al final, quien "gana"?

Git resuelve esto con **branches** y **merges**, pero hay un problema adicional:
ambos desarrolladores necesitan su propia copia de los archivos para compilar,
probar y trabajar sin interferir con el otro.

### Feature Branch (Rama de funcionalidad)

En lugar de que todos trabajen en `master`, cada desarrollador crea una rama
para su historia de usuario. Trabaja ahi de forma aislada y cuando termina,
integra sus cambios de vuelta a `master`.

```
master:             ──o──o──────────────o── (merge)
                          \            /
feature/tech-9:            o──o──o──o─  (rama del desarrollador A)
```

Convencion de nombres: `feature/tech-9-usuario`, `feature/tech-11-entidades-orden`

```bash
# Crear una rama nueva desde master
git checkout -b feature/tech-9-usuario

# Trabajar, hacer commits...
git add .
git commit -m "TECH-9: entidad Usuario JPA"

# Volver a master
git checkout master
```

### Por que no trabajar directo en master?

| Directo en master | Con feature branches |
|---|---|
| Un error rompe todo para todos | El error esta aislado en la rama |
| No se puede hacer code review | Se puede revisar antes de integrar |
| Conflictos constantes | Conflictos solo al momento del merge |
| No hay forma de "deshacer" una feature | Se borra la rama y listo |

### Git Worktree (Arbol de trabajo)

Un **worktree** es un segundo directorio de trabajo que comparte el mismo repositorio
Git. Es como tener dos copias del proyecto, pero sin duplicar el historial — ambas
apuntan al mismo `.git/`.

```
Repositorio compartido (.git/)
│
├── /proyecto/                    ← worktree principal (master)
├── /proyecto-tech-9/             ← worktree del Agente A (feature/tech-9)
└── /proyecto-tech-11/            ← worktree del Agente B (feature/tech-11)
```

Cada worktree:
- Tiene su propia copia de los archivos
- Puede estar en una rama diferente
- Puede compilar y ejecutar tests independientemente
- Los commits que hagas en un worktree son visibles en los demas (comparten `.git/`)

```bash
# Crear un worktree en un directorio nuevo, con una rama nueva
git worktree add ../proyecto-tech-9 -b feature/tech-9-usuario

# Listar worktrees activos
git worktree list

# Eliminar un worktree cuando ya no se necesita
git worktree remove ../proyecto-tech-9
```

### Worktree vs Clone

| | Worktree | Clone |
|---|---|---|
| Espacio en disco | Solo los archivos (comparte `.git/`) | Duplica todo, incluyendo historial |
| Sincronizacion | Automatica (mismo repo) | Requiere push/pull |
| Ramas | Compartidas | Independientes |
| Uso ideal | Trabajo paralelo temporal | Repos separados permanentes |

### Merge (Integracion)

Combinar los cambios de una rama en otra. Cuando dos ramas modifican archivos
**diferentes**, Git lo hace automaticamente. Cuando modifican las **mismas lineas**
del mismo archivo, hay un conflicto que se resuelve manualmente.

```bash
# Desde master, integrar los cambios de una feature branch
git checkout master
git merge feature/tech-9-usuario
```

Tipos de merge:

```
Fast-forward (la rama esta "adelante" de master, sin divergencia):
master:   ──o──o
                \
feature:         o──o──o
                        ↓
master:   ──o──o──o──o──o  (simplemente avanza el puntero)


Merge commit (ambas ramas tienen commits nuevos):
master:   ──o──o──o──────o── (merge commit)
                \        /
feature:         o──o──o─
```

### Merge sin conflictos (el caso ideal)

Cuando cada desarrollador trabaja en **modulos separados** (archivos distintos),
el merge es limpio. Esto es lo que buscamos con buena arquitectura:

```
Agente A toco:                    Agente B toco:
  modelo/Usuario.java              modelo/Orden.java
  servicio/UsuarioService.java     modelo/OrdenDetalle.java
  controlador/UsuarioCtrl.java     repositorio/OrdenRepo.java
  tests de Usuario                 repositorio/OrdenDetalleRepo.java

Archivos en comun: NINGUNO → merge limpio garantizado
```

### Merge con conflictos (cuando hay solapamiento)

Si dos desarrolladores editan la misma linea del mismo archivo, Git no sabe
cual version elegir y marca un **conflicto**:

```
<<<<<<< HEAD
// version de master
private String nombre;
=======
// version de la feature branch
private String nombreCompleto;
>>>>>>> feature/tech-9
```

El desarrollador debe elegir cual version mantener (o combinar ambas) y hacer
un nuevo commit.

---

## Flujo completo: desarrollo paralelo con worktrees

Este es el flujo que seguimos en TechShop para el Sprint 2:

```
Paso 1: Crear feature branches desde master
        $ git checkout -b feature/tech-9-usuario
        $ git checkout master
        $ git checkout -b feature/tech-11-entidades-orden

Paso 2: Crear worktrees (un directorio por branch)
        $ git worktree add ../techshop-tech-9 feature/tech-9-usuario
        $ git worktree add ../techshop-tech-11 feature/tech-11-entidades-orden

Paso 3: Cada agente trabaja en su worktree
        Agente A en ../techshop-tech-9/  → desarrolla, testea, commitea
        Agente B en ../techshop-tech-11/ → desarrolla, testea, commitea

Paso 4: Integrar ambas ramas en master
        $ git checkout master
        $ git merge feature/tech-9-usuario
        $ git merge feature/tech-11-entidades-orden

Paso 5: Verificar que todo funciona junto
        $ mvn clean verify    (en master, con ambos modulos)

Paso 6: Limpiar worktrees y branches
        $ git worktree remove ../techshop-tech-9
        $ git worktree remove ../techshop-tech-11
        $ git branch -d feature/tech-9-usuario
        $ git branch -d feature/tech-11-entidades-orden
```

---

## Buenas practicas aprendidas

### Commits
- Mensajes en espanol, concisos, que expliquen el *que* y el *por que*
- Prefijo con la historia de Jira: `TECH-9: registro de usuarios con validacion`
- Un commit por unidad logica de trabajo, no un mega-commit al final

### Branches
- Nombre descriptivo: `feature/tech-9-usuario` (no `rama1` o `fix`)
- Vida corta: crear, desarrollar, mergear, eliminar
- Siempre partir de `master` actualizado

### Worktrees
- Usarlos para trabajo paralelo temporal, no como estructura permanente
- Eliminarlos al terminar para no dejar directorios huerfanos
- Cada worktree debe poder compilar independientemente

---

## Diario de aprendizaje

| Fecha | Concepto | Contexto |
|---|---|---|
| 2026-03-05 | Commits, mensajes descriptivos | Desarrollo del Sprint 1 (TECH-4 a TECH-8) |
| 2026-03-06 | Feature branches, worktrees, merge, desarrollo paralelo | Planificacion del Sprint 2 con agentes paralelos |
