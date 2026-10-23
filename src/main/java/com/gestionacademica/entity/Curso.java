package com.gestionacademica.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El código del curso es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    @Column(nullable = false)
    private Integer creditos;

    @Size(max = 100)
    @Column(length = 100)
    private String horario;

    @Size(max = 100)
    @Column(length = 100)
    private String docente;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("curso")
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Curso() {
    }

    public Curso(String nombre, String codigo, Integer creditos, String horario, String docente) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.creditos = creditos;
        this.horario = horario;
        this.docente = docente;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
}
