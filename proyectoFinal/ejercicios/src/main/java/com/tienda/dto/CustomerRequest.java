package com.tienda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * EJERCICIO 5 - DTO de entrada para crear un cliente.
 */
public record CustomerRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email invalido")
        String email,

        String phone
) {}

