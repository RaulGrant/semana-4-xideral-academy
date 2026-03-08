package com.tienda.repository;

import com.tienda.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EJERCICIO 5 - Repositorio de ordenes.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Obtiene todas las ordenes de un cliente especifico por su ID. */
    List<Order> findByCustomerId(Long customerId);
}

