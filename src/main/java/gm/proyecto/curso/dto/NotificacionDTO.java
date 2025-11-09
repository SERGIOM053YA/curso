package gm.proyecto.curso.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

// DTO simple para enviar al frontend
@Data
@NoArgsConstructor
public class NotificacionDTO {

    private Long idNotificacion;
    private String mensaje;
    private boolean leida;

    // Datos aplanados de las relaciones
    private Long idInscripcion;
    private String nombreEstudiante;
    private String nombreCurso;

    // Constructor para facilitar la conversión
    public NotificacionDTO(Long idNotificacion, String mensaje, boolean leida,
                           Long idInscripcion, String nombreEstudiante, String nombreCurso) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.leida = leida;
        this.idInscripcion = idInscripcion;
        this.nombreEstudiante = nombreEstudiante;
        this.nombreCurso = nombreCurso;
    }
}
