package gm.proyecto.curso.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO flexible para transportar los datos de cualquier tipo de pregunta.
 * - enunciado: El texto de la pregunta.
 * - tipo: "ABIERTA", "VERDADERO_FALSO", "OPCION_MULTIPLE".
 * - detalles: Un mapa para los datos específicos de cada tipo.
 * Ej: para OPCION_MULTIPLE, contendrá "opciones" y "respuestaCorrecta".
 */
public record PreguntaDTO(
        String enunciado,
        String tipo,
        Map<String, Object> detalles
) {
}
