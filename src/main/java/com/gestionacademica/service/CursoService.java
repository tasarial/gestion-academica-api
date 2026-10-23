package com.gestionacademica.service;

import com.gestionacademica.entity.Curso;
import com.gestionacademica.exception.ResourceNotFoundException;
import com.gestionacademica.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Curso obtenerPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con id: " + id));
    }

    public Curso crear(Curso curso) {
        if (cursoRepository.existsByCodigo(curso.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un curso con el código: " + curso.getCodigo());
        }
        return cursoRepository.save(curso);
    }

    public Curso actualizar(Long id, Curso datos) {
        Curso existente = obtenerPorId(id);

        if (!existente.getCodigo().equals(datos.getCodigo())
                && cursoRepository.existsByCodigo(datos.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un curso con el código: " + datos.getCodigo());
        }

        existente.setNombre(datos.getNombre());
        existente.setCodigo(datos.getCodigo());
        existente.setCreditos(datos.getCreditos());
        existente.setHorario(datos.getHorario());
        existente.setDocente(datos.getDocente());

        return cursoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Curso existente = obtenerPorId(id);
        cursoRepository.delete(existente);
    }
}
