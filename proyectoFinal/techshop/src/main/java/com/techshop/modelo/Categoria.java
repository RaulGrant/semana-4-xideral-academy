package com.techshop.modelo;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Documento MongoDB que representa una categoria de productos.
 * Cada categoria define una lista de atributos especificos que sus productos
 * pueden tener (ej: Laptops tiene ram, procesador; Smartphones tiene pantalla, camara).
 */
@Document(collection = "categorias")
public class Categoria {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    /** Atributos especificos que los productos de esta categoria pueden tener */
    private List<String> atributos;

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion, List<String> atributos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.atributos = atributos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<String> atributos) {
        this.atributos = atributos;
    }
}
