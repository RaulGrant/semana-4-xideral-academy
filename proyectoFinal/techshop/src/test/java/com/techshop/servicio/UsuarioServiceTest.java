package com.techshop.servicio;

import com.techshop.modelo.Usuario;
import com.techshop.repositorio.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan Perez", "juan@ejemplo.com", "password123");
        usuario.setId(1L);
    }

    @Test
    void registrar_usuarioValido_asignaFechaYGuarda() {
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrar(usuario);

        assertNotNull(resultado);
        assertNotNull(resultado.getFechaRegistro());
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@ejemplo.com", resultado.getEmail());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void registrar_emailDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(usuario));

        assertTrue(excepcion.getMessage().contains("Ya existe un usuario"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_retornaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.buscarPorId(99L));

        assertTrue(excepcion.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void listarTodos_devuelveLista() {
        Usuario otro = new Usuario("Maria Lopez", "maria@ejemplo.com", "pass456");
        otro.setId(2L);
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, otro));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void listarTodos_listaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<Usuario> resultado = usuarioService.listarTodos();

        assertTrue(resultado.isEmpty());
    }
}
