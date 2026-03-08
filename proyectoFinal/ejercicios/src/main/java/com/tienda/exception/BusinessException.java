package com.tienda.exception;

/**
 * EJERCICIO 2 - Excepcion para conflictos de negocio.
 * Por ejemplo: intentar crear un producto con nombre duplicado,
 * o crear un cliente con email ya registrado.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

