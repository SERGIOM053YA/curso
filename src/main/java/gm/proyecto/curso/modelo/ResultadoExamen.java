package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ResultadoExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Usuario estudiante;

    @ManyToOne
    @JoinColumn(name = "examen_id")
    private Examen examen;

    @OneToMany(mappedBy = "resultadoExamen", cascade = CascadeType.ALL)
    @JsonIgnore // Evita bucles infinitos al serializar
    private List<RespuestaEstudiante> respuestas;

    private LocalDateTime fechaRealizacion;
    private Double puntajeObtenido; // Calificación automática o final
    private Double puntajeTotal;    // Total de preguntas

    // "PENDIENTE" (si hay manuales) o "CALIFICADO" (si todo es automático o ya se calificó)
    private String estado;

    // true si todas las preguntas fueron automáticas o si no había preguntas
    // false si al menos una pregunta (abierta) requiere calificación manual
    private boolean calificacionAutomaticaCompleta;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }

    public List<RespuestaEstudiante> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaEstudiante> respuestas) {
        this.respuestas = respuestas;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public Double getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(Double puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public Double getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(Double puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isCalificacionAutomaticaCompleta() {
        return calificacionAutomaticaCompleta;
    }

    public void setCalificacionAutomaticaCompleta(boolean calificacionAutomaticaCompleta) {
        this.calificacionAutomaticaCompleta = calificacionAutomaticaCompleta;
    }
}
