package gm.proyecto.curso.controlador;

// --- ¡IMPORTA LOS DTOs Y LAS CLASES NECESARIAS! ---
import gm.proyecto.curso.dto.NotificacionDTO;
import gm.proyecto.curso.modelo.Inscripcion; // <- La quitaremos de las respuestas
import gm.proyecto.curso.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // <-- Para respuestas simples
import java.util.Optional; // <-- Para validar

@RestController
@RequestMapping("/api/notificaciones") // Te sugiero añadir una ruta base
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Endpoint para OBTENER la lista de notificaciones pendientes (NO LEÍDAS).
     * PRUEBA EN POSTMAN: GET http://localhost:8080/api/notificaciones
     */
    @GetMapping // (Tu ruta estaba vacía, asumo que es la raíz del controlador)
    // --- ¡CORRECCIÓN #1! ---
    // El método ahora devuelve la lista de DTOs, no de entidades.
    public List<NotificacionDTO> obtenerNotificaciones() {
        // El error rojo desaparecerá porque los tipos ya coinciden
        return notificacionService.obtenerNotificacionesNoLeidas();
    }

    /**
     * Endpoint para APROBAR una solicitud.
     * PRUEBA EN POSTMAN: POST http://localhost:8080/api/notificaciones/{id}/aprobar
     */
    @PostMapping("/{id}/aprobar")
    // --- ¡CORRECCIÓN #2! ---
    // Cambiamos ResponseEntity<Inscripcion> por ResponseEntity<?>
    // para no devolver la entidad y evitar el error de recursividad.
    public ResponseEntity<?> aprobarInscripcion(@PathVariable Long id) {

        Optional<Inscripcion> inscripcionOpt = notificacionService.gestionarSolicitud(id, true); // true para aprobar

        // Validamos si se encontró y gestionó
        if (inscripcionOpt.isPresent()) {
            // Devolvemos un mensaje simple, ¡NUNCA la entidad!
            return ResponseEntity.ok(Map.of("mensaje", "Inscripción aprobada correctamente."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para RECHAZAR una solicitud.
     * PRUEBA EN POSTMAN: POST http://localhost:8080/api/notificaciones/{id}/rechazar
     */
    @PostMapping("/{id}/rechazar")
    // --- ¡CORRECCIÓN #3! ---
    // Hacemos lo mismo que con aprobar.
    public ResponseEntity<?> rechazarInscripcion(@PathVariable Long id) {

        Optional<Inscripcion> inscripcionOpt = notificacionService.gestionarSolicitud(id, false); // false para rechazar

        if (inscripcionOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("mensaje", "Inscripción rechazada correctamente."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}