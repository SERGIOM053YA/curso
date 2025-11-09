package gm.proyecto.curso.modelo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@DiscriminatorValue("OPCION_MULTIPLE") // <-- ¡AÑADIR ESTA LÍNEA!
@EqualsAndHashCode(callSuper = true)
public class PreguntaOpcionMultiple extends Pregunta {

    @ElementCollection(fetch = FetchType.EAGER) // EAGER para que siempre cargue las opciones con la pregunta
    @CollectionTable(name = "pom_opciones", joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "opcion")
    private List<String> opciones;

    private int indiceRespuestaCorrecta;

    // --- MÉTODOS AÑADIDOS MANUALMENTE ---

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    public int getIndiceRespuestaCorrecta() {
        return indiceRespuestaCorrecta;
    }

    public void setIndiceRespuestaCorrecta(int indiceRespuestaCorrecta) {
        this.indiceRespuestaCorrecta = indiceRespuestaCorrecta;
    }
}