package gm.proyecto.curso.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("VERDADERO_FALSO") // <-- ¡AÑADIR ESTA LÍNEA!
@EqualsAndHashCode(callSuper = true)
public class PreguntaVerdaderoFalso extends Pregunta {

    private boolean respuestaCorrecta;


    // --- MÉTODOS AÑADIDOS MANUALMENTE ---

    // Y este es el getter que te falta
    public Boolean getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(boolean respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
}

