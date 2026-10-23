package com.gestionacademica.controller;

import com.gestionacademica.entity.Curso;
import com.gestionacademica.entity.Inscripcion;
import com.gestionacademica.service.CursoService;
import com.gestionacademica.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoService cursoService;
    private final InscripcionService inscripcionService;

    public CursoController(CursoService cursoService, InscripcionService inscripcionService) {
        this.cursoService = cursoService;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Curso> crear(@Valid @RequestBody Curso curso) {
        Curso creado = cursoService.crear(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id, @Valid @RequestBody Curso curso) {
        return ResponseEntity.ok(cursoService.actualizar(id, curso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Inscripciones de un curso (relación 1:N) */
    @GetMapping("/{id}/inscripciones")
    public ResponseEntity<List<Inscripcion>> inscripcionesDelCurso(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.listarPorCurso(id));
    }
}
