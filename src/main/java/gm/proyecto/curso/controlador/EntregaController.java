package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.Entrega;
import gm.proyecto.curso.modelo.Tarea;
import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.repository.EntregaRepository;
import gm.proyecto.curso.repository.TareaRepository;
import gm.proyecto.curso.repository.UsuarioRepository;
import gm.proyecto.curso.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // <-- AÑADIR IMPORT
import java.util.Optional; // <-- AÑADIR IMPORT

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping(value = "/tarea/{tareaId}", consumes = "multipart/form-data")
    public ResponseEntity<?> crearEntrega(
            @PathVariable Long tareaId,
            @RequestParam("archivos") MultipartFile[] archivos,
            Authentication authentication) {

        // 1. Identificar al estudiante
        String userEmail = authentication.getName();
        Usuario estudiante = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Encontrar la tarea
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // 3. Verificar si ya existe una entrega para esta tarea y este estudiante
        if (entregaRepository.existsByTareaAndEstudiante(tarea, estudiante)) {
            // Devolvemos 409 Conflict (Conflicto) para indicar que ya se realizó la entrega.
            return new ResponseEntity<>("Ya has realizado una entrega para esta tarea.", HttpStatus.CONFLICT);
        }

        // 4. Guardar cada archivo y obtener sus URLs
        List<String> urlsArchivos = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            String url = fileStorageService.storeFile(archivo);
            urlsArchivos.add(url);
        }

        // 5. Crear el registro de la entrega
        Entrega nuevaEntrega = new Entrega();
        nuevaEntrega.setTarea(tarea);
        nuevaEntrega.setEstudiante(estudiante);
        nuevaEntrega.setArchivosUrl(urlsArchivos);
        nuevaEntrega.setFechaDeEntrega(LocalDateTime.now());

        Entrega entregaGuardada = entregaRepository.save(nuevaEntrega);

        return ResponseEntity.ok(entregaGuardada); // Devolvemos 200 OK con la entrega
    }

    // --- ¡AQUÍ ESTÁ EL NUEVO MÉTODO AÑADIDO! ---

    /**
     * Endpoint para que el ADMIN asigne una calificación a una entrega específica.
     * PRUEBA EN POSTMAN: PUT http://localhost:8080/api/entregas/123/calificar
     * Con un body JSON como: { "calificacion": 9.5 }
     *
     * @param entregaId El ID de la Entrega (NO el ID de la Tarea)
     * @param payload Un JSON que contiene la calificación
     * @return La entrega actualizada con su calificación
     */
    @PutMapping("/{entregaId}/calificar")
    public ResponseEntity<?> calificarEntrega(
            @PathVariable Long entregaId,
            @RequestBody Map<String, Double> payload) {

        // 1. Obtenemos la calificación del JSON
        Double calificacion = payload.get("calificacion");
        if (calificacion == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El JSON debe incluir la 'calificacion'"));
        }

        // 2. Buscamos la entrega específica por su ID
        Optional<Entrega> entregaOpt = entregaRepository.findById(entregaId);
        if (entregaOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // No se encontró la entrega
        }

        // 3. Asignamos la calificación y la guardamos en la BD
        Entrega entrega = entregaOpt.get();
        entrega.setCalificacion(calificacion);
        Entrega entregaActualizada = entregaRepository.save(entrega);

        // 4. Devolvemos la entrega actualizada (200 OK)
        return ResponseEntity.ok(entregaActualizada);
    }
}