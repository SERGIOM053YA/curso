package gm.proyecto.curso.modelo;

import jakarta.persistence.*;
import java.util.Date;

// --- IMPORTS PARA LA CORRECCIÓN ---
// Asegúrate de importar tu Enum y las anotaciones de JPA
import gm.proyecto.curso.modelo.EstadoInscripcion;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Usuario estudiante;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDeInscripcion;

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    // 1. El tipo de dato ahora es el Enum "EstadoInscripcion"
    // 2. La anotación @Enumerated le dice a JPA que lo guarde como String
    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public Date getFechaDeInscripcion() {
        return fechaDeInscripcion;
    }

    public void setFechaDeInscripcion(Date fechaDeInscripcion) {
        this.fechaDeInscripcion = fechaDeInscripcion;
    }

    // --- Getter y Setter para el campo corregido ---
    public EstadoInscripcion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInscripcion estado) {
        this.estado = estado;
    }
}