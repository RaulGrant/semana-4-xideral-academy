package com.techshop.servicio;

import com.techshop.modelo.Categoria;
import com.techshop.modelo.Producto;
import com.techshop.repositorio.CategoriaRepository;
import com.techshop.repositorio.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Laptops", "Computadoras portatiles", List.of("ram", "procesador"));
        categoria.setId("cat1");

        producto = new Producto("MacBook Pro", "Laptop Apple", new BigDecimal("29999.99"),
                10, "https://img.com/mac.jpg", "Laptops", Map.of("ram", "16GB", "procesador", "M3"));
        producto.setId("prod1");
    }

    @Test
    void listarTodos_devuelveLista() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("MacBook Pro", resultado.get(0).getNombre());
    }

    @Test
    void listarTodos_listaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());

        assertTrue(productoService.listarTodos().isEmpty());
    }

    @Test
    void buscarPorId_existente() {
        when(productoRepository.findById("prod1")).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.buscarPorId("prod1");

        assertTrue(resultado.isPresent());
        assertEquals("MacBook Pro", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_noExistente() {
        when(productoRepository.findById("xyz")).thenReturn(Optional.empty());

        assertTrue(productoService.buscarPorId("xyz").isEmpty());
    }

    @Test
    void buscarPorCategoria_conResultados() {
        Producto otro = new Producto("ThinkPad", "Laptop Lenovo", new BigDecimal("19999"),
                5, null, "Laptops", Map.of("ram", "8GB"));
        when(productoRepository.findByCategoria("Laptops")).thenReturn(Arrays.asList(producto, otro));

        List<Producto> resultado = productoService.buscarPorCategoria("Laptops");

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorCategoria_sinResultados() {
        when(productoRepository.findByCategoria("Relojes")).thenReturn(List.of());

        assertTrue(productoService.buscarPorCategoria("Relojes").isEmpty());
    }

    @Test
    void crear_productoValido() {
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.crear(producto);

        assertNotNull(resultado);
        assertEquals("MacBook Pro", resultado.getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void crear_categoriaInexistente_lanzaExcepcion() {
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productoService.crear(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizar_productoExistente() {
        Producto datosNuevos = new Producto("MacBook Pro M3", "Actualizado", new BigDecimal("32999"),
                8, "https://img.com/mac2.jpg", "Laptops", Map.of("ram", "32GB"));
        when(productoRepository.findById("prod1")).thenReturn(Optional.of(producto));
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto resultado = productoService.actualizar("prod1", datosNuevos);

        assertEquals("MacBook Pro M3", resultado.getNombre());
        assertEquals(new BigDecimal("32999"), resultado.getPrecio());
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(productoRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productoService.actualizar("xyz", producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizar_categoriaInexistente_lanzaExcepcion() {
        when(productoRepository.findById("prod1")).thenReturn(Optional.of(producto));
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productoService.actualizar("prod1", producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_productoExistente() {
        when(productoRepository.existsById("prod1")).thenReturn(true);

        productoService.eliminar("prod1");

        verify(productoRepository).deleteById("prod1");
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(productoRepository.existsById("xyz")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> productoService.eliminar("xyz"));
        verify(productoRepository, never()).deleteById(any());
    }
}
