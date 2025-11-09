package gm.proyecto.curso.dto;

import java.util.List;

/**
 * DTO para enviar la respuesta de una sola pregunta.
 * El tipo de 'respuesta' dependerá del tipo de pregunta:
 * - OPCION_MULTIPLE: String (la opción seleccionada)
 * - SELECCION_MULTIPLE: List<String> (las opciones seleccionadas)
 * - ABIERTA: String (el texto de la respuesta)
 * - VERDADERO_FALSO: Boolean (true o false)
 */
public record RespuestaDTO(
        Long preguntaId,
        Object respuesta // Usamos Object para flexibilidad, Jackson lo serializará
) {
}