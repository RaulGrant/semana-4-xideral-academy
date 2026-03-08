package com.techshop.servicio;

import com.techshop.modelo.Categoria;
import com.techshop.repositorio.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Laptops", "Computadoras portatiles",
                Arrays.asList("ram", "procesador", "almacenamiento"));
        categoria.setId("abc123");
    }

    @Test
    void listarTodas_devuelveLista() {
        Categoria otra = new Categoria("Smartphones", "Telefonos", Arrays.asList("pantalla", "camara"));
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria, otra));

        List<Categoria> resultado = categoriaService.listarTodas();

        assertEquals(2, resultado.size());
        verify(categoriaRepository).findAll();
    }

    @Test
    void listarTodas_listaVacia() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<Categoria> resultado = categoriaService.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_existente() {
        when(categoriaRepository.findById("abc123")).thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado = categoriaService.buscarPorId("abc123");

        assertTrue(resultado.isPresent());
        assertEquals("Laptops", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_noExistente() {
        when(categoriaRepository.findById("xyz")).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.buscarPorId("xyz");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_categoriaValida() {
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.crear(categoria);

        assertNotNull(resultado);
        assertEquals("Laptops", resultado.getNombre());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void crear_nombreDuplicado_lanzaExcepcion() {
        when(categoriaRepository.findByNombre("Laptops")).thenReturn(Optional.of(categoria));

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizar_categoriaExistente() {
        Categoria datosNuevos = new Categoria("Laptops Gaming", "Para gamers",
                Arrays.asList("ram", "gpu", "pantalla"));
        when(categoriaRepository.findById("abc123")).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        Categoria resultado = categoriaService.actualizar("abc123", datosNuevos);

        assertEquals("Laptops Gaming", resultado.getNombre());
        assertEquals("Para gamers", resultado.getDescripcion());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(categoriaRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.actualizar("xyz", categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminar_categoriaExistente() {
        when(categoriaRepository.existsById("abc123")).thenReturn(true);

        categoriaService.eliminar("abc123");

        verify(categoriaRepository).deleteById("abc123");
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(categoriaRepository.existsById("xyz")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.eliminar("xyz"));
        verify(categoriaRepository, never()).deleteById(any());
    }
}
