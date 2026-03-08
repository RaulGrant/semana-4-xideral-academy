package com.techshop.batch;

import com.techshop.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductoItemProcessorTest {

    @InjectMocks
    private ProductoItemProcessor processor;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setNombre("MacBook Pro");
        producto.setDescripcion("Laptop Apple");
        producto.setPrecio(new BigDecimal("29999.99"));
        producto.setStock(10);
        producto.setImagenUrl("https://img.com/mac.jpg");
        producto.setCategoria("Laptops");
        producto.setAtributos(Map.of("_json", "{\"ram\":\"16GB\",\"procesador\":\"M3\"}"));
    }

    @Test
    void process_datosValidos_retornaProducto() throws Exception {
        Producto resultado = processor.process(producto);

        assertNotNull(resultado);
        assertEquals("MacBook Pro", resultado.getNombre());
        assertEquals(new BigDecimal("29999.99"), resultado.getPrecio());
    }

    @Test
    void process_nombreBlank_retornaNull() throws Exception {
        producto.setNombre("   ");

        assertNull(processor.process(producto));
    }

    @Test
    void process_nombreNull_retornaNull() throws Exception {
        producto.setNombre(null);

        assertNull(processor.process(producto));
    }

    @Test
    void process_precioNegativo_retornaNull() throws Exception {
        producto.setPrecio(new BigDecimal("-100"));

        assertNull(processor.process(producto));
    }

    @Test
    void process_precioCero_retornaNull() throws Exception {
        producto.setPrecio(BigDecimal.ZERO);

        assertNull(processor.process(producto));
    }

    @Test
    void process_precioNull_retornaNull() throws Exception {
        producto.setPrecio(null);

        assertNull(processor.process(producto));
    }

    @Test
    void process_stockNegativo_retornaNull() throws Exception {
        producto.setStock(-1);

        assertNull(processor.process(producto));
    }

    @Test
    void process_stockNull_retornaNull() throws Exception {
        producto.setStock(null);

        assertNull(processor.process(producto));
    }

    @Test
    void process_stockCero_retornaProducto() throws Exception {
        producto.setStock(0);

        Producto resultado = processor.process(producto);

        assertNotNull(resultado);
        assertEquals(0, resultado.getStock());
    }

    @Test
    void process_categoriaBlank_retornaNull() throws Exception {
        producto.setCategoria("  ");

        assertNull(processor.process(producto));
    }

    @Test
    void process_categoriaNull_retornaNull() throws Exception {
        producto.setCategoria(null);

        assertNull(processor.process(producto));
    }

    @Test
    void process_parseaAtributosJsonCorrectamente() throws Exception {
        Producto resultado = processor.process(producto);

        assertNotNull(resultado);
        assertNotNull(resultado.getAtributos());
        assertEquals("16GB", resultado.getAtributos().get("ram"));
        assertEquals("M3", resultado.getAtributos().get("procesador"));
        assertFalse(resultado.getAtributos().containsKey("_json"));
    }

    @Test
    void process_atributosNull_retornaProductoSinAtributos() throws Exception {
        producto.setAtributos(null);

        Producto resultado = processor.process(producto);

        assertNotNull(resultado);
        assertNull(resultado.getAtributos());
    }

    @Test
    void process_atributosJsonInvalido_retornaProductoConAtributosNull() throws Exception {
        producto.setAtributos(Map.of("_json", "esto-no-es-json"));

        Producto resultado = processor.process(producto);

        assertNotNull(resultado);
        assertNull(resultado.getAtributos());
    }
}
