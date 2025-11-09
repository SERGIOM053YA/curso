package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.Inscripcion;
import gm.proyecto.curso.repository.InscripcionRepository; // <-- Importación necesaria
import gm.proyecto.curso.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <-- Importación necesaria

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private InscripcionRepository inscripcionRepository; // <-- Inyectamos el repositorio

    // --- MÉTODO GET AÑADIDO ---
    // Endpoint para obtener TODAS las inscripciones (útil para depuración)
    @GetMapping
    public List<Inscripcion> obtenerTodasLasInscripciones() {
        return inscripcionRepository.findAll();
    }

    // Endpoint para que un usuario solicite inscribirse a un curso
    @PostMapping
    public ResponseEntity<Inscripcion> solicitarInscripcion(@RequestParam Long cursoId, @RequestParam Long usuarioId) {
        Inscripcion nuevaInscripcion = inscripcionService.solicitarInscripcion(cursoId, usuarioId);
        if (nuevaInscripcion != null) {
            return ResponseEntity.ok(nuevaInscripcion);
        }
        return ResponseEntity.badRequest().build();
    }
}
