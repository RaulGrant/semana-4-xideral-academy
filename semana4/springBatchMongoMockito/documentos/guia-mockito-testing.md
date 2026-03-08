# Mockito - Por que existen los mocks y como usarlos

## El problema del fake manual

En el proyecto anterior (`restSpringMongoJUnit`) aprendimos a testear con JUnit 5 usando un **fake manual**: el `FakeAlumnoRepository`. Funciono bien, pero tiene un problema importante.

`AlumnoRepository` extiende `MongoRepository`, que hereda metodos de `ListCrudRepository`, `ListPagingAndSortingRepository` y `QueryByExampleExecutor`. El resultado: **23 metodos** en la interface. Nuestro servicio solo usa 5, pero el fake tuvo que declarar los 23:

```
5 metodos implementados (los que usa AlumnoService)
    findAll, findById, save, existsById, deleteById

18 metodos con UnsupportedOperationException (los que NO usa)
    saveAll, findAllById, count, delete, deleteAllById, deleteAll,
    findAll(Sort), findAll(Pageable), insert, findOne, findAll(Example),
    findAll(Example, Sort), findAll(Example, Pageable), count(Example),
    exists(Example), findBy...
```

Escribir 18 metodos que solo dicen `throw new UnsupportedOperationException("No implementado")` es tedioso, fragil (si la interface cambia hay que actualizar el fake), y distrae del proposito real: probar el servicio.

---

## Que es Mockito

Mockito es una libreria que **crea fakes automaticos** (llamados mocks) en una sola linea. En lugar de escribir 23 metodos a mano:

```java
// Sin Mockito: escribir una clase entera de 161 lineas
FakeAlumnoRepository fakeRepository = new FakeAlumnoRepository();

// Con Mockito: una sola linea
AlumnoRepository mockRepository = mock(AlumnoRepository.class);
```

El mock que crea Mockito:
- Implementa todos los metodos de la interface automaticamente
- Por defecto retorna `null` (objetos), `0` (numeros), `false` (booleanos), colecciones vacias
- Tu le dices **que retornar** cuando le llamen un metodo especifico (`when/thenReturn`)
- Puedes **verificar** que se llamo un metodo (`verify`)

---

## Que hay de nuevo en este proyecto

Este proyecto es el Spring Batch v2 con MongoDB que ya conoces, pero ahora incluye **tests unitarios** y un **servicio nuevo** disenado especificamente para practicar Mockito.

### Archivos nuevos de produccion

| Archivo | Que hace |
|---------|---------|
| `src/main/.../service/EmpleadoService.java` | Servicio que orquesta processors + repositorio |
| `src/main/.../repository/EmpleadoReporteRepository.java` | Interface de repositorio para MongoDB |

### Archivos nuevos de test

| Archivo | Que prueba | Tests | Usa Mockito? |
|---------|-----------|-------|-------------|
| `src/test/.../model/EmpleadoTest.java` | Modelo Empleado | 7 | No |
| `src/test/.../model/EmpleadoReporteTest.java` | Modelo EmpleadoReporte | 6 | No |
| `src/test/.../processor/EmpleadoProcessorTest.java` | Transformacion: mayusculas + bono | 7 | No |
| `src/test/.../processor/ReporteProcessorTest.java` | Transformacion: Empleado a EmpleadoReporte | 7 | No |
| `src/test/.../service/EmpleadoServiceTest.java` | Servicio con 3 dependencias | 10 | **Si** |

### Archivos existentes (no cambian)

| Archivo | Rol |
|---------|-----|
| `src/main/.../model/Empleado.java` | Modelo del Step 1 |
| `src/main/.../model/EmpleadoReporte.java` | Modelo del Step 2 con `@Document` |
| `src/main/.../processor/EmpleadoProcessor.java` | Mayusculas + bono 10% |
| `src/main/.../processor/ReporteProcessor.java` | Empleado a EmpleadoReporte |
| `src/main/.../config/BatchConfig.java` | Configuracion del Job |

**Total: 37 tests, todos pasan.** Solo 10 usan Mockito. Los otros 27 son JUnit 5 puro.

---

## Tests SIN mock primero

Antes de usar Mockito, veamos los tests que **no lo necesitan**. El principio es simple: si una clase no tiene dependencias externas, no necesita mocks.

### EmpleadoProcessorTest — funcion pura

`EmpleadoProcessor` recibe un `Empleado` y lo transforma: nombre a mayusculas + bono del 10%. No depende de base de datos, ni de otros servicios, ni de nada externo. Es una **funcion pura**.

```java
class EmpleadoProcessorTest {

    private EmpleadoProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new EmpleadoProcessor();
    }

    @Test
    @DisplayName("process: convierte el nombre a mayusculas")
    void process_nombreMinusculas_retornaMayusculas() throws Exception {
        // Arrange
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        // Act
        Empleado resultado = processor.process(empleado);

        // Assert
        assertEquals("JUAN PEREZ", resultado.getNombre());
    }

    @Test
    @DisplayName("process: calcula el bono como 10% del salario")
    void process_salario25000_bonoEs2500() throws Exception {
        Empleado empleado = crearEmpleado("Juan Perez", "Ventas", 25000.0);

        Empleado resultado = processor.process(empleado);

        assertEquals(2500.0, resultado.getBono());
    }
}
```

No hay `@Mock`, no hay `when/thenReturn`, no hay `verify`. Solo JUnit 5 puro con patron AAA.

### ReporteProcessorTest — otra funcion pura

`ReporteProcessor` transforma un `Empleado` en un `EmpleadoReporte`, copiando campos y calculando `salarioTotal = salario + bono`. Tampoco tiene dependencias.

```java
@Test
@DisplayName("process: calcula salarioTotal como salario + bono")
void process_salarioYBono_totalEsLaSuma() throws Exception {
    Empleado empleado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0, 2500.0);

    EmpleadoReporte reporte = processor.process(empleado);

    assertEquals(27500.0, reporte.getSalarioTotal());
}
```

**Regla de oro:** si puedes probar sin mock, hazlo sin mock. Los tests sin mock son mas simples, mas rapidos y mas faciles de entender.

---

## Anatomia de un test con Mockito

`EmpleadoService` tiene **3 dependencias** que se inyectan por constructor:

```java
public class EmpleadoService {

    private final EmpleadoProcessor empleadoProcessor;
    private final ReporteProcessor reporteProcessor;
    private final EmpleadoReporteRepository repository;

    public EmpleadoService(EmpleadoProcessor empleadoProcessor,
                           ReporteProcessor reporteProcessor,
                           EmpleadoReporteRepository repository) {
        this.empleadoProcessor = empleadoProcessor;
        this.reporteProcessor = reporteProcessor;
        this.repository = repository;
    }
}
```

Si quisieramos crear fakes manuales, necesitariamos 3 clases falsas. Con Mockito, usamos 3 anotaciones:

```java
@ExtendWith(MockitoExtension.class)       // 1. Activa Mockito para JUnit 5
class EmpleadoServiceTest {

    @Mock                                   // 2. Crea un fake automatico
    private EmpleadoProcessor empleadoProcessor;

    @Mock
    private ReporteProcessor reporteProcessor;

    @Mock
    private EmpleadoReporteRepository repository;

    @InjectMocks                            // 3. Crea el objeto REAL e inyecta los mocks
    private EmpleadoService service;
```

### Las 3 anotaciones clave

| Anotacion | Que hace | Equivalente sin Mockito |
|-----------|---------|------------------------|
| `@ExtendWith(MockitoExtension.class)` | Activa Mockito. Sin esto, `@Mock` y `@InjectMocks` no funcionan | — |
| `@Mock` | Crea un objeto falso que no hace nada por defecto | Escribir `FakeAlumnoRepository` a mano (161 lineas) |
| `@InjectMocks` | Crea el objeto **real** bajo prueba y le inyecta los mocks en el constructor | `new AlumnoService(fakeRepository)` |

Diagrama de lo que hace Mockito automaticamente:

```
@Mock empleadoProcessor  ──┐
@Mock reporteProcessor   ──┼──>  @InjectMocks service = new EmpleadoService(mock1, mock2, mock3)
@Mock repository         ──┘
```

---

## when/thenReturn: programar el comportamiento de un mock

Un mock recien creado no hace nada util: todos sus metodos retornan `null`. Para que un test funcione, necesitas **programar** que debe retornar:

```java
@Test
@DisplayName("procesarYGuardar: procesa empleado y guarda el reporte")
void procesarYGuardar_empleadoValido_guardaReporte() throws Exception {
    // Arrange
    Empleado original = crearEmpleado("Juan Perez", "Ventas", 25000.0);
    Empleado procesado = crearEmpleado("JUAN PEREZ", "Ventas", 25000.0);
    procesado.setBono(2500.0);

    EmpleadoReporte reporte = crearReporte("rpt1", "JUAN PEREZ", "Ventas",
            25000.0, 2500.0, 27500.0);

    // Programamos cada mock:
    // "cuando llamen a process(original), retorna procesado"
    when(empleadoProcessor.process(original)).thenReturn(procesado);
    when(reporteProcessor.process(procesado)).thenReturn(reporte);
    when(repository.save(reporte)).thenReturn(reporte);

    // Act
    EmpleadoReporte resultado = service.procesarYGuardar(original);

    // Assert
    assertNotNull(resultado);
    assertEquals("JUAN PEREZ", resultado.getNombre());
    assertEquals(27500.0, resultado.getSalarioTotal());
}
```

La sintaxis es:

```java
when( mock.metodo(argumentos) ).thenReturn( valorDeRetorno );
```

Esto le dice a Mockito: "cuando alguien llame a este metodo con estos argumentos, retorna este valor". Es como un guion de teatro — le dices al actor (mock) que debe decir en cada escena.

### Flujo de lo que pasa cuando se ejecuta `service.procesarYGuardar(original)`

```
1. service llama a empleadoProcessor.process(original)
   → Mockito intercepta la llamada
   → Ve que hay un when/thenReturn programado para process(original)
   → Retorna "procesado"

2. service llama a reporteProcessor.process(procesado)
   → Mockito intercepta
   → Retorna "reporte"

3. service llama a repository.save(reporte)
   → Mockito intercepta
   → Retorna "reporte"

4. El test verifica el resultado con assertEquals
```

---

## verify: comprobar que se llamo un metodo

`when/thenReturn` programa **que retornar**. `verify` comprueba **que se llamo** un metodo. Son complementarios:

```java
// Del test procesarYGuardar_empleadoValido_guardaReporte:

// Verificamos que se llamo cada mock exactamente 1 vez
verify(empleadoProcessor).process(original);
verify(reporteProcessor).process(procesado);
verify(repository).save(reporte);
```

`verify(mock).metodo(argumentos)` falla si el metodo **nunca se llamo** o se llamo con **argumentos diferentes**.

### Para que sirve verify?

Imagina que el servicio tiene un bug y nunca llama a `repository.save()` — el resultado seria `null` pero el test podria no detectarlo si solo verificas el retorno. Con `verify`, te aseguras de que el servicio **realmente** llamo al repositorio.

---

## Tecnicas avanzadas

El `EmpleadoServiceTest` demuestra 4 tecnicas adicionales de Mockito:

### 1. InOrder — verificar el orden de ejecucion

```java
@Test
@DisplayName("procesarYGuardar: ejecuta los pasos en el orden correcto")
void procesarYGuardar_empleadoValido_ejecutaEnOrden() throws Exception {
    // ... arrange y when/thenReturn ...

    service.procesarYGuardar(original);

    // Verifica que los metodos se llamaron EN ESTE ORDEN:
    InOrder orden = inOrder(empleadoProcessor, reporteProcessor, repository);
    orden.verify(empleadoProcessor).process(original);       // 1ro
    orden.verify(reporteProcessor).process(procesado);       // 2do
    orden.verify(repository).save(reporte);                  // 3ro
}
```

`InOrder` falla si los metodos se llamaron en un orden diferente. Util cuando el orden importa (ej: no puedes guardar el reporte antes de procesarlo).

### 2. ArgumentCaptor — capturar argumentos

```java
@Test
@DisplayName("procesarYGuardar: captura el reporte que se intenta guardar")
void procesarYGuardar_empleadoValido_capturaReporteGuardado() throws Exception {
    // ... arrange ...
    when(repository.save(any(EmpleadoReporte.class))).thenReturn(reporte);

    service.procesarYGuardar(original);

    // Captura el argumento real que recibio repository.save()
    ArgumentCaptor<EmpleadoReporte> captor = ArgumentCaptor.forClass(EmpleadoReporte.class);
    verify(repository).save(captor.capture());

    EmpleadoReporte reporteCapturado = captor.getValue();
    assertAll("Reporte capturado",
        () -> assertEquals("CARLOS GARCIA", reporteCapturado.getNombre()),
        () -> assertEquals("RRHH", reporteCapturado.getDepartamento()),
        () -> assertEquals(30800.0, reporteCapturado.getSalarioTotal())
    );
}
```

`ArgumentCaptor` "atrapa" el argumento que se paso a un metodo del mock. Util cuando quieres inspeccionar **que exactamente** se envio al repositorio, no solo que se llamo.

### 3. times(n) — verificar numero de invocaciones

```java
@Test
@DisplayName("procesarLote: procesa cada empleado de la lista")
void procesarLote_listaConDosEmpleados_procesaCadaUno() throws Exception {
    // ... arrange con 2 empleados ...

    List<EmpleadoReporte> resultados = service.procesarLote(Arrays.asList(emp1, emp2));

    assertEquals(2, resultados.size());

    // Verifica que cada processor se llamo exactamente 2 veces
    verify(empleadoProcessor, times(2)).process(any(Empleado.class));
    verify(reporteProcessor, times(2)).process(any(Empleado.class));
    verify(repository, times(2)).save(any(EmpleadoReporte.class));
}
```

`times(2)` verifica que el metodo se llamo exactamente 2 veces. Sin argumento, `verify` asume `times(1)`.

### 4. never() — verificar que NO se llamo un metodo

```java
@Test
@DisplayName("procesarLote: lista vacia no invoca ningun mock")
void procesarLote_listaVacia_noInvocaMocks() throws Exception {
    List<EmpleadoReporte> resultados = service.procesarLote(List.of());

    assertTrue(resultados.isEmpty());

    // Verifica que NUNCA se llamo a ningun mock
    verify(empleadoProcessor, never()).process(any());
    verify(reporteProcessor, never()).process(any());
    verify(repository, never()).save(any());
}
```

`never()` falla si el metodo se llamo al menos una vez. Util para verificar que una lista vacia no produce llamadas innecesarias.

### Tabla resumen de tecnicas

| Tecnica | Sintaxis | Para que sirve |
|---------|---------|---------------|
| `when/thenReturn` | `when(mock.metodo(args)).thenReturn(valor)` | Programar que retorna un mock |
| `verify` | `verify(mock).metodo(args)` | Comprobar que se llamo un metodo (1 vez) |
| `times(n)` | `verify(mock, times(n)).metodo(args)` | Comprobar cuantas veces se llamo |
| `never()` | `verify(mock, never()).metodo(args)` | Comprobar que NUNCA se llamo |
| `InOrder` | `inOrder(m1, m2).verify(m1).metodo()` | Verificar el orden de las llamadas |
| `ArgumentCaptor` | `captor.capture()` + `captor.getValue()` | Inspeccionar que argumento se paso |
| `any()` | `when(mock.save(any()))` | Coincidir con cualquier argumento |

---

## Recorrido por los tests

### EmpleadoTest (7 tests) — modelo basico

Prueba getters, setters, `toString()` y `assertAll`. No usa mocks. Mismo patron que `AlumnoTest` del proyecto anterior.

Tests principales:
- Constructor vacio tiene valores por defecto (`null` para Strings, `0.0` para doubles)
- Cada setter guarda el valor correctamente
- `toString()` incluye nombre, departamento, salario y bono
- `assertAll` verifica todas las propiedades a la vez

### EmpleadoReporteTest (6 tests) — modelo con campos adicionales

`EmpleadoReporte` tiene los mismos campos que `Empleado` mas `id` y `salarioTotal`. Los tests verifican que estos campos adicionales funcionan y que `salarioTotal` puede ser la suma de salario + bono.

### EmpleadoProcessorTest (7 tests) — transformacion sin mock

Verifica las dos transformaciones del processor:
1. Nombre a mayusculas: `"Juan Perez"` se convierte en `"JUAN PEREZ"`
2. Bono = 10% del salario: salario `25000.0` produce bono `2500.0`

Tambien verifica lo que NO debe cambiar: el departamento y el salario original no se modifican.

Tests clave:

| Test | Que verifica |
|------|-------------|
| `process_nombreMinusculas_retornaMayusculas` | `"Juan Perez"` se convierte en `"JUAN PEREZ"` |
| `process_salario25000_bonoEs2500` | Bono = 10% del salario |
| `process_departamento_noSeModifica` | El departamento no cambia |
| `process_salarioCero_bonoCero` | Caso limite: salario 0 produce bono 0 |

### ReporteProcessorTest (7 tests) — transformacion sin mock

Verifica que la transformacion de `Empleado` a `EmpleadoReporte` copia todos los campos y calcula `salarioTotal = salario + bono`.

Tests clave:

| Test | Que verifica |
|------|-------------|
| `process_salarioYBono_totalEsLaSuma` | `25000 + 2500 = 27500` |
| `process_bonoCero_totalIgualSalario` | Sin bono, total = salario |
| `process_empleado_retornaObjetoNuevo` | El reporte es una instancia nueva, no el mismo objeto |

### EmpleadoServiceTest (10 tests) — con Mockito

Este es el archivo central de la guia. `EmpleadoService` orquesta 3 dependencias:

```
EmpleadoProcessor  →  ReporteProcessor  →  EmpleadoReporteRepository
```

Como las 3 son interfaces/clases que en produccion necesitan Spring y bases de datos, usamos mocks:

| Metodo probado | Tests | Tecnicas de Mockito usadas |
|---------------|-------|---------------------------|
| `procesarYGuardar` | 3 | `when/thenReturn`, `verify`, `InOrder`, `ArgumentCaptor` |
| `procesarLote` | 2 | `times(n)`, `never()`, `any()` |
| `obtenerReporte` | 2 | `when/thenReturn` con `Optional` |
| `obtenerTodosLosReportes` | 1 | `when/thenReturn` con listas |
| `eliminarReporte` | 2 | `verify`, `InOrder`, `never()` |

El test `eliminarReporte_idInexistente_retornaFalseSinEliminar` es un buen ejemplo de `never()`:

```java
@Test
@DisplayName("eliminarReporte: no elimina cuando el reporte no existe")
void eliminarReporte_idInexistente_retornaFalseSinEliminar() {
    when(repository.existsById("noExiste")).thenReturn(false);

    boolean resultado = service.eliminarReporte("noExiste");

    assertFalse(resultado);

    // Verifica que existsById SI se llamo, pero deleteById NUNCA
    verify(repository).existsById("noExiste");
    verify(repository, never()).deleteById(anyString());
}
```

Verifica no solo que el resultado es `false`, sino que el servicio **no intento eliminar** algo que no existe.

---

## Como ejecutar los tests

Desde la terminal, en la carpeta del proyecto (`springBatchV2MongoMockito/`):

```bash
mvn test
```

**No necesitas que MySQL ni MongoDB esten corriendo.** Los tests usan mocks — no tocan ninguna base de datos.

### Salida esperada

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.academia.batch.model.EmpleadoReporteTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.academia.batch.model.EmpleadoTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.academia.batch.processor.EmpleadoProcessorTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.academia.batch.processor.ReporteProcessorTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.academia.batch.service.EmpleadoServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] Results:
[INFO]
[INFO] Tests run: 37, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## Cuando usar mock y cuando no

| Situacion | Usar mock? | Ejemplo del proyecto |
|-----------|-----------|---------------------|
| Clase sin dependencias externas | **No** | `EmpleadoProcessorTest`, `ReporteProcessorTest` |
| Modelo/entidad (getters, setters) | **No** | `EmpleadoTest`, `EmpleadoReporteTest` |
| Servicio que depende de repositorios | **Si** | `EmpleadoServiceTest` |
| Servicio que depende de otros servicios | **Si** | — |
| Clase que hace llamadas HTTP | **Si** | — |
| Funcion pura (entrada → salida) | **No** | Los processors |
| Interface de Spring Data | **Si** | `EmpleadoReporteRepository` |

**Regla practica:** Si puedes crear el objeto con `new MiClase()` sin problemas, probablemente no necesitas mock. Si necesitas Spring, una base de datos, o una conexion externa, usa mock.

---

## Problemas comunes

### "Unnecessary stubbings detected"

Mockito detecta que programaste un `when/thenReturn` pero nunca se uso:

```java
// Programaste esto, pero el test nunca llama a un metodo que use el mock
when(repository.findById("id")).thenReturn(Optional.empty());
```

Solucion: elimina el `when/thenReturn` que no se necesita.

### "Wanted but not invoked"

`verify` falla porque el metodo nunca se llamo:

```java
// El test espera que se llame a save(), pero el servicio no lo hizo
verify(repository).save(reporte);   // FALLA
```

Solucion: revisa que el servicio realmente llame a ese metodo en el escenario que estas probando.

### NullPointerException en el test

Generalmente significa que te falto un `when/thenReturn`. El mock retorna `null` por defecto:

```java
// Sin when/thenReturn, process() retorna null
Empleado resultado = empleadoProcessor.process(empleado);  // null
resultado.getNombre();  // NullPointerException
```

Solucion: agrega `when(empleadoProcessor.process(empleado)).thenReturn(procesado)`.

### "Cannot resolve symbol '@Mock'"

Falta la dependencia de Mockito en el `pom.xml` o el import:

```java
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
```

### "@Mock no inyecta el mock"

Falta `@ExtendWith(MockitoExtension.class)` en la clase:

```java
// MAL - @Mock no funciona sin @ExtendWith
class MiTest {
    @Mock
    private MiRepositorio repo;
}

// BIEN
@ExtendWith(MockitoExtension.class)
class MiTest {
    @Mock
    private MiRepositorio repo;
}
```

### "Argument(s) are different!"

El `when/thenReturn` espera un argumento exacto pero recibio otro:

```java
// Programaste con "rpt1"
when(repository.findById("rpt1")).thenReturn(Optional.of(reporte));

// Pero el servicio llama con "rpt2"
service.obtenerReporte("rpt2");  // Retorna null (no coincide)
```

Solucion: usa `any()` si el argumento no importa, o asegurate de que coincidan.

### Los tests de Mockito son lentos

No deberian serlo. Si tus tests tardan mas de 1 segundo, probablemente estas levantando Spring sin querer. Verifica que tu clase de test NO tenga `@SpringBootTest`. Los tests con Mockito no necesitan Spring.

---

## Resumen

| Concepto | Que aprendimos |
|----------|---------------|
| Fake manual | Funciona pero es tedioso para interfaces grandes |
| `@Mock` | Crea un fake automatico en 1 linea |
| `@InjectMocks` | Crea el objeto real e inyecta los mocks |
| `@ExtendWith(MockitoExtension.class)` | Activa Mockito para JUnit 5 |
| `when/thenReturn` | Programa que retorna un mock |
| `verify` | Comprueba que se llamo un metodo |
| `times(n)` / `never()` | Cuantas veces se llamo |
| `InOrder` | Orden de las llamadas |
| `ArgumentCaptor` | Captura argumentos para inspeccionarlos |
| `any()` | Comodin: coincide con cualquier argumento |

**Progresion del curso:**

```
Proyecto 1 (restSpringMongoJUnit)       Proyecto 2 (springBatchV2MongoMockito)
+------------------------------+       +----------------------------------+
| JUnit 5 puro                 |       | JUnit 5 + Mockito                |
| Fake manual (FakeRepository) |  -->  | Mocks automaticos (@Mock)        |
| Tests de entidad y servicio  |       | Tests sin mock + tests con mock  |
| 20 tests                     |       | 37 tests                         |
+------------------------------+       +----------------------------------+
```
