package com.techshop.servicio;

import com.techshop.modelo.Usuario;
import com.techshop.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica de negocio para la gestion de usuarios de la tienda.
 * Controla el registro de nuevos usuarios validando unicidad de email
 * y asignando automaticamente la fecha de registro.
 * Los usuarios se almacenan en MySQL para garantizar integridad referencial
 * con las futuras entidades de carrito y ordenes.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida que no exista otro usuario con el mismo email (restriccion de negocio)
     * y asigna la fecha de registro al momento actual antes de persistir.
     *
     * @param usuario datos del usuario a registrar
     * @return el usuario registrado con id y fechaRegistro asignados
     * @throws IllegalArgumentException si ya existe un usuario con ese email
     */
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws IllegalArgumentException si no existe un usuario con ese ID
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
    }

    /**
     * Retorna todos los usuarios registrados en el sistema.
     *
     * @return lista de usuarios (puede estar vacia)
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
