package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.ResultadoExamen;
import org.springframework.security.core.Authentication;
import gm.proyecto.curso.dto.ExamenDTO;
import gm.proyecto.curso.modelo.Examen;
import gm.proyecto.curso.service.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/examenes")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    /**
     * Endpoint para que el ADMIN cree un nuevo examen dentro de una unidad.
     * @param unidadId El ID de la unidad a la que pertenece el examen.
     * @param examenDTO El cuerpo de la solicitud con el título y las preguntas.
     * @return El examen creado.
     */
    @PostMapping("/unidad/{unidadId}")
    public ResponseEntity<Examen> crearExamen(@PathVariable Long unidadId, @RequestBody ExamenDTO examenDTO) {
        return examenService.crearExamen(unidadId, examenDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ... (después de tu método crearExamen)

    /**
     * Endpoint para que un ESTUDIANTE obtenga SU resultado de un examen.
     * ¡ESTE ES EL ENDPOINT PARA EL PUNTO 4!
     * Responde a: GET /api/examenes/{examenId}/mi-resultado
     *
     * @param examenId El ID del examen que se está consultando.
     * @param authentication El objeto de seguridad de Spring (inyectado automáticamente).
     * @return El ResultadoExamen o 404 Not Found si no existe.
     */
    @GetMapping("/{examenId}/mi-resultado")
    public ResponseEntity<ResultadoExamen> obtenerMiResultado(
            @PathVariable Long examenId,
            Authentication authentication) {

        // Obtenemos el email del usuario que está haciendo la petición
        String emailEstudiante = authentication.getName();

        // Llamamos al nuevo método del servicio
        return examenService.obtenerResultado(examenId, emailEstudiante)
                .map(ResponseEntity::ok) // Si se encuentra, devuelve 200 OK con el resultado
                .orElse(ResponseEntity.notFound().build()); // Si no, devuelve 404 Not Found
    }

    // ... (después de tus otros métodos)

    /**
     * Endpoint para que el ADMIN actualice un examen existente.
     * Responde a: PUT /api/examenes/{examenId}
     */
    @PutMapping("/{examenId}")
    public ResponseEntity<Examen> actualizarExamen(
            @PathVariable Long examenId,
            @RequestBody ExamenDTO examenDTO) {

        return examenService.actualizarExamen(examenId, examenDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

