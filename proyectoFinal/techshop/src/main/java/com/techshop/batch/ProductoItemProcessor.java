package com.techshop.batch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Procesador de Spring Batch que valida y transforma cada producto leido del CSV.
 * Aplica las siguientes reglas de negocio antes de permitir la escritura en MongoDB:
 * <ul>
 *   <li>El nombre no puede estar vacio</li>
 *   <li>El precio debe ser mayor a cero</li>
 *   <li>El stock no puede ser negativo</li>
 *   <li>La categoria no puede estar vacia</li>
 *   <li>Los atributos se parsean de JSON string a Map</li>
 * </ul>
 * Si alguna validacion falla, retorna null para que Spring Batch filtre el registro.
 */
@Component
public class ProductoItemProcessor implements ItemProcessor<Producto, Producto> {

    private static final Logger log = LoggerFactory.getLogger(ProductoItemProcessor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Valida los campos del producto y parsea los atributos JSON.
     * Retorna null si el producto no cumple las validaciones,
     * lo que hace que Spring Batch lo filtre automaticamente.
     */
    @Override
    public Producto process(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            log.warn("Producto filtrado: nombre vacio o nulo");
            return null;
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Producto filtrado: precio invalido para '{}'", producto.getNombre());
            return null;
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            log.warn("Producto filtrado: stock negativo para '{}'", producto.getNombre());
            return null;
        }

        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
            log.warn("Producto filtrado: categoria vacia para '{}'", producto.getNombre());
            return null;
        }

        // Parsear atributos JSON si aun no fueron convertidos a Map
        parsearAtributos(producto);

        return producto;
    }

    /**
     * Parsea el campo atributos cuando viene como JSON string embebido en el CSV.
     * El FieldSetMapper del BatchConfig almacena el JSON crudo usando un Map temporal
     * con la clave especial "_json". Este metodo lo detecta y lo convierte al Map real.
     */
    void parsearAtributos(Producto producto) {
        Map<String, Object> atributos = producto.getAtributos();
        if (atributos != null && atributos.containsKey("_json")) {
            String json = (String) atributos.get("_json");
            Map<String, Object> parsed = parsearJsonAtributos(json);
            producto.setAtributos(parsed);
        }
    }

    /**
     * Parsea un string JSON a un Map de atributos.
     *
     * @param json el string JSON (ej: {"ram":"16GB","procesador":"M3"})
     * @return el Map parseado, o null si el JSON es invalido o vacio
     */
    Map<String, Object> parsearJsonAtributos(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Error al parsear atributos JSON: {}", e.getMessage());
            return null;
        }
    }
}
