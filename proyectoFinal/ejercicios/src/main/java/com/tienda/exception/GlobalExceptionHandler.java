package com.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * EJERCICIO 3 - Manejador global de excepciones.
 *
 * @RestControllerAdvice combina @ControllerAdvice + @ResponseBody.
 * Permite centralizar el manejo de errores en un solo lugar en vez
 * de repetir try-catch en cada controlador.
 *
 * Patron: intercepta excepciones lanzadas desde cualquier controlador
 * y las transforma en respuestas HTTP con el codigo y cuerpo adecuados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja recursos no encontrados -> HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja conflictos de negocio (duplicados, stock insuficiente, etc.) -> HTTP 409 Conflict.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja errores de validacion de @Valid en DTOs -> HTTP 400 Bad Request.
     * MethodArgumentNotValidException se lanza cuando un campo falla una constraint
     * de Bean Validation (@NotBlank, @Positive, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", "Error de validacion en los datos de entrada");
        response.put("errors", fieldErrors);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Captura cualquier otra excepcion no controlada -> HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Clase interna record para la respuesta de error estandarizada.
     */
    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}

