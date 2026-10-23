package com.gestionacademica.repository;

import com.gestionacademica.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByCursoId(Long cursoId);

    List<Inscripcion> findByEstudianteEmail(String email);
}
