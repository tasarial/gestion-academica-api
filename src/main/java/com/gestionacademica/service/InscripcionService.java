package com.gestionacademica.service;

import com.gestionacademica.entity.Curso;
import com.gestionacademica.entity.Inscripcion;
import com.gestionacademica.exception.ResourceNotFoundException;
import com.gestionacademica.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoService cursoService;

    public InscripcionService(InscripcionRepository inscripcionRepository, CursoService cursoService) {
        this.inscripcionRepository = inscripcionRepository;
        this.cursoService = cursoService;
    }

    public List<Inscripcion> listarTodas() {
        return inscripcionRepository.findAll();
    }

    public Inscripcion obtenerPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con id: " + id));
    }

    public List<Inscripcion> listarPorCurso(Long cursoId) {
        // Verifica que el curso exista
        cursoService.obtenerPorId(cursoId);
        return inscripcionRepository.findByCursoId(cursoId);
    }

    public Inscripcion crear(Inscripcion inscripcion) {
        // Valida y asigna el curso real desde la BD
        Long cursoId = inscripcion.getCurso() != null ? inscripcion.getCurso().getId() : null;
        if (cursoId == null) {
            throw new IllegalArgumentException("Debe indicar el id del curso");
        }
        Curso curso = cursoService.obtenerPorId(cursoId);
        inscripcion.setCurso(curso);
        return inscripcionRepository.save(inscripcion);
    }

    public Inscripcion actualizar(Long id, Inscripcion datos) {
        Inscripcion existente = obtenerPorId(id);

        existente.setEstudianteNombre(datos.getEstudianteNombre());
        existente.setEstudianteEmail(datos.getEstudianteEmail());
        existente.setEstado(datos.getEstado());

        if (datos.getFechaInscripcion() != null) {
            existente.setFechaInscripcion(datos.getFechaInscripcion());
        }

        if (datos.getCurso() != null && datos.getCurso().getId() != null) {
            Curso curso = cursoService.obtenerPorId(datos.getCurso().getId());
            existente.setCurso(curso);
        }

        return inscripcionRepository.save(existente);
    }

    public void eliminar(Long id) {
        Inscripcion existente = obtenerPorId(id);
        inscripcionRepository.delete(existente);
    }
}
