package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- ¡AÑADE ESTE IMPORT!
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Usuario estudiante;

    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY) // <-- LAZY es bueno, pero Jackson aún intenta seguirlo
    @JoinColumn(name = "resultado_examen_id")
    @JsonIgnore // <-- ¡¡ESTA ES LA LÍNEA MÁGICA!!
    private ResultadoExamen resultadoExamen;

    // Para respuestas de opción múltiple, V/F, selección múltiple (guardado como JSON)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String respuestaJson;

    // Para respuestas abiertas (texto largo)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String respuestaTexto;

    // Puntaje de esta pregunta (1.0, 0.5, 0.0). Puede ser null si está pendiente.
    private Double puntajeObtenido;

    // true si fue calificada por un admin (solo aplica a preguntas abiertas)
    private boolean calificadaManualmente = false;

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

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public ResultadoExamen getResultadoExamen() {
        return resultadoExamen;
    }

    public void setResultadoExamen(ResultadoExamen resultadoExamen) {
        this.resultadoExamen = resultadoExamen;
    }

    public String getRespuestaJson() {
        return respuestaJson;
    }

    public void setRespuestaJson(String respuestaJson) {
        this.respuestaJson = respuestaJson;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
    }

    public Double getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(Double puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public boolean isCalificadaManualmente() {
        return calificadaManualmente;
    }

    public void setCalificadaManualmente(boolean calificadaManualmente) {
        this.calificadaManualmente = calificadaManualmente;
    }
}
