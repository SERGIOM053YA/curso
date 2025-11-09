package gm.proyecto.curso.modelo;

/**
 * Representa los posibles estados de una inscripción a un curso.
 */
public enum EstadoInscripcion {
    PENDIENTE, // El estudiante solicitó inscribirse, pero el admin no ha respondido.
    APROBADA,  // El admin aprobó la inscripción.
    RECHAZADA  // El admin rechazó la inscripción.
}