package com.techshop.repositorio;

import com.techshop.modelo.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoriaRepository extends MongoRepository<Categoria, String> {

    Optional<Categoria> findByNombre(String nombre);
}
