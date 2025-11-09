package gm.proyecto.curso.controlador;

import gm.proyecto.curso.dto.RespuestaDTO;
import gm.proyecto.curso.modelo.RespuestaEstudiante;
import gm.proyecto.curso.service.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/respuestas")
public class RespuestaController {

    @Autowired
    private ExamenService examenService;

    @PostMapping("/examen/{examenId}")
    public ResponseEntity<?> finalizarExamen(
            @PathVariable Long examenId,
            @RequestBody List<RespuestaDTO> respuestas,
            Authentication authentication) {
        String emailEstudiante = authentication.getName();
        return examenService.procesarYCalcularExamen(examenId, emailEstudiante, respuestas)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/examen/{examenId}/abiertas")
    public ResponseEntity<List<RespuestaEstudiante>> getRespuestasAbiertasSinCalificar(@PathVariable Long examenId) {
        return ResponseEntity.ok(examenService.getRespuestasAbiertasPendientes(examenId));
    }

    @PostMapping("/{respuestaId}/calificar-manual")
    public ResponseEntity<?> calificarPreguntaAbierta(@PathVariable Long respuestaId, @RequestBody Map<String, Double> payload) {
        Double puntaje = payload.get("puntaje");
        if (puntaje == null || (puntaje != 0.0 && puntaje != 0.5 && puntaje != 1.0)) {
            return ResponseEntity.badRequest().body("Puntaje inválido. Valores permitidos: 0.0, 0.5, 1.0");
        }
        return examenService.calificarPreguntaAbierta(respuestaId, puntaje)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

