package gm.proyecto.curso.dto;

import java.util.List;

/**
 * DTO para transportar los datos de creación de un nuevo Examen.
 * Contiene el título del examen y una lista de las preguntas a crear.
 */
public record ExamenDTO(
        String titulo,
        List<PreguntaDTO> preguntas
) {
}
