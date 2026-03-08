package com.tienda.dto;

import com.tienda.model.Customer;

import java.time.LocalDateTime;

/**
 * EJERCICIO 5 - DTO de salida para clientes.
 */
public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt
) {
    public static CustomerResponse fromEntity(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getEmail(),
                c.getPhone(), c.getCreatedAt());
    }
}

