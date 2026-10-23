package com.gestionacademica.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Size(max = 120)
    @Column(name = "estudiante_nombre", nullable = false, length = 120)
    private String estudianteNombre;

    @NotBlank(message = "El email del estudiante es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(max = 150)
    @Column(name = "estudiante_email", nullable = false, length = 150)
    private String estudianteEmail;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Size(max = 30)
    @Column(length = 30)
    private String estado; // ACTIVA, CANCELADA, FINALIZADA

    @NotNull(message = "El curso es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties("inscripciones")
    private Curso curso;

    public Inscripcion() {
    }

    public Inscripcion(String estudianteNombre, String estudianteEmail, LocalDate fechaInscripcion,
                       String estado, Curso curso) {
        this.estudianteNombre = estudianteNombre;
        this.estudianteEmail = estudianteEmail;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
        this.curso = curso;
    }

    @PrePersist
    public void prePersist() {
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDate.now();
        }
        if (estado == null || estado.isBlank()) {
            estado = "ACTIVA";
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public String getEstudianteEmail() {
        return estudianteEmail;
    }

    public void setEstudianteEmail(String estudianteEmail) {
        this.estudianteEmail = estudianteEmail;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
