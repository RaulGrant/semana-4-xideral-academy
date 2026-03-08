package com.tienda.repository;

import com.tienda.model.Category;
import com.tienda.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EJERCICIO 2 - Repositorio de productos.
 *
 * JpaRepository<Product, Long> ya provee:
 *   save(), findById(), findAll(), deleteById(), count(), existsById(), etc.
 *
 * Spring Data JPA genera el SQL automaticamente a partir del nombre del metodo
 * siguiendo convenciones (query derivation). Por ejemplo:
 *   findByCategory  ->  SELECT * FROM products WHERE category = ?
 *   findByPriceBetween -> SELECT * FROM products WHERE price BETWEEN ? AND ?
 *
 * @Query permite JPQL personalizado cuando la convencion de nombres no alcanza.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Filtra por categoria. Spring Data genera la query automaticamente. */
    List<Product> findByCategory(Category category);

    /** Filtra productos dentro de un rango de precio (inclusivo). */
    List<Product> findByPriceBetween(Double min, Double max);

    /** Busqueda case-insensitive por fragmento del nombre. */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /** Verifica si ya existe un producto con ese nombre exacto (case-insensitive). */
    boolean existsByNameIgnoreCase(String name);

    /**
     * @Query con JPQL (Java Persistence Query Language):
     * opera sobre entidades y campos Java, no sobre tablas SQL directas.
     * Retorna solo productos con stock > 0, ordenados por precio ascendente.
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.price ASC")
    List<Product> findAvailableProducts();

    /**
     * Consulta para top 5 productos mas vendidos (Ejercicio 5).
     * SUM(oi.quantity) agrega las unidades vendidas por producto,
     * ORDER BY DESC + LIMIT 5 via @Query con JPQL.
     */
    @Query("SELECT p FROM Product p JOIN OrderItem oi ON oi.product = p " +
           "GROUP BY p ORDER BY SUM(oi.quantity) DESC LIMIT 5")
    List<Product> findTop5BestSellers();
}

