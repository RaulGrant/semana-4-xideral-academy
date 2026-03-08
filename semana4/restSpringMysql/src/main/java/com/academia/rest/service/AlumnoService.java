package com.academia.rest.service;

import com.academia.rest.entity.Alumno;
import com.academia.rest.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {

    private final AlumnoRepository repository;

    public AlumnoService(AlumnoRepository repository) {
        this.repository = repository;
    }

    public List<Alumno> listarTodos() {
        return repository.findAll();
    }

    public Optional<Alumno> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Alumno crear(Alumno alumno) {
        if (alumno.getFechaRegistro() == null) {
            alumno.setFechaRegistro(LocalDate.now());
        }
        return repository.save(alumno);
    }

    public Optional<Alumno> actualizar(Integer id, Alumno datos) {
        return repository.findById(id).map(alumno -> {
            alumno.setNombre(datos.getNombre());
            alumno.setApellido(datos.getApellido());
            alumno.setEmail(datos.getEmail());
            return repository.save(alumno);
        });
    }

    public boolean eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
