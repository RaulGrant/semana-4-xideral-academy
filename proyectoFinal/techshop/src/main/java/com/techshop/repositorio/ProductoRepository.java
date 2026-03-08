package com.techshop.repositorio;

import com.techshop.modelo.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByCategoria(String categoria);
}
