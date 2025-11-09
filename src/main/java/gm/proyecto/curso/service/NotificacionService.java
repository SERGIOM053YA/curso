package gm.proyecto.curso.service;

import gm.proyecto.curso.dto.NotificacionDTO; // <-- ¡IMPORTAR DTO!
import gm.proyecto.curso.modelo.EstadoInscripcion;
import gm.proyecto.curso.modelo.Inscripcion;
import gm.proyecto.curso.modelo.Notificacion;
import gm.proyecto.curso.repository.InscripcionRepository;
import gm.proyecto.curso.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- ¡IMPORTAR STREAMS!

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // --- ¡CORRECCIÓN IMPORTANTE! ---
    // El método ahora devuelve una LISTA DE DTOs
    public List<NotificacionDTO> obtenerNotificacionesNoLeidas() {

        // 1. Obtenemos las entidades (tu consulta JOIN FETCH es perfecta para esto)
        List<Notificacion> notificaciones = notificacionRepository.findNotificacionesPendientesWithDetails();

        // 2. Convertimos la lista de Entidades a una lista de DTOs
        return notificaciones.stream()
                .map(this::convertirADTO) // Usamos un método ayudante
                .collect(Collectors.toList());
    }

    // Método privado para convertir una Entidad Notificacion a un NotificacionDTO
    private NotificacionDTO convertirADTO(Notificacion n) {
        // Como hicimos JOIN FETCH, podemos acceder a todo sin miedo a errores
        Inscripcion i = n.getInscripcion();

        return new NotificacionDTO(
                n.getId(),
                n.getMensaje(),
                n.isLeida(),
                i.getId(),
                i.getEstudiante().getNombreCompleto(), // Asumiendo que Estudiante tiene getNombre()
                i.getCurso().getTitulo()        // Asumiendo que Curso tiene getTitulo()
        );
    }

    public Optional<Inscripcion> gestionarSolicitud(Long notificacionId, boolean aprobar) {
        Optional<Notificacion> notificacionOpt = notificacionRepository.findById(notificacionId);

        if (notificacionOpt.isPresent()) {
            Notificacion notificacion = notificacionOpt.get();
            Inscripcion inscripcion = notificacion.getInscripcion();

            if (aprobar) {
                inscripcion.setEstado(EstadoInscripcion.APROBADA);
            } else {
                inscripcion.setEstado(EstadoInscripcion.RECHAZADA);
            }

            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
            inscripcionRepository.save(inscripcion);

            return Optional.of(inscripcion);
        }
        return Optional.empty();
    }
}