package com.tienda.exception;

/**
 * EJERCICIO 2 - Excepcion de negocio para recurso no encontrado.
 *
 * Extiende RuntimeException (unchecked) para no obligar al compilador
 * a manejarla en cada firma de metodo. Se captura en el GlobalExceptionHandler.
 *
 * En entrevistas: la diferencia entre checked y unchecked es importante.
 * Unchecked es apropiado cuando el error representa un fallo de programacion
 * o un estado invalido del que no se puede recuperar facilmente.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " con id " + id + " no encontrado");
    }
}

