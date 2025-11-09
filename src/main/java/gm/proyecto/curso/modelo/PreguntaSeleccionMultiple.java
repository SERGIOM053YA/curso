package gm.proyecto.curso.modelo;

import jakarta.persistence.*;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import java.util.List;

@Entity
@DiscriminatorValue("SELECCION_MULTIPLE")
public class PreguntaSeleccionMultiple extends Pregunta {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "psm_opciones", joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "opcion")
    private List<String> opciones;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_respuestas_correctas", joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "indice_respuesta")
    private List<Integer> indicesRespuestasCorrectas;

    // Getters y Setters
    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    public List<Integer> getIndicesRespuestasCorrectas() {
        return indicesRespuestasCorrectas;
    }

    public void setIndicesRespuestasCorrectas(List<Integer> indicesRespuestasCorrectas) {
        this.indicesRespuestasCorrectas = indicesRespuestasCorrectas;
    }
}