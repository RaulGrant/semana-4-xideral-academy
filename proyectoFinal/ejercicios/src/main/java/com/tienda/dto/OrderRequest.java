package com.tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * EJERCICIO 5 - DTO de entrada para crear una orden.
 * Incluye el ID del cliente y la lista de items del pedido.
 * Usa @Valid en los items para propagar la validacion al record anidado.
 */
public record OrderRequest(

        @NotNull(message = "El ID del cliente es obligatorio")
        Long customerId,

        @NotEmpty(message = "La orden debe tener al menos un item")
        @Valid
        List<OrderItemRequest> items
) {
    /**
     * Sub-record para cada linea del pedido:
     * productId + quantity que el cliente desea comprar.
     */
    public record OrderItemRequest(
            @NotNull(message = "El ID del producto es obligatorio")
            Long productId,

            @Positive(message = "La cantidad debe ser mayor a cero")
            Integer quantity
    ) {}
}

