package gm.proyecto.curso.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gm.proyecto.curso.dto.LeccionDTO;
import gm.proyecto.curso.dto.TareaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import gm.proyecto.curso.modelo.*;
import gm.proyecto.curso.repository.ContenidoRepository;
import gm.proyecto.curso.repository.InscripcionRepository;
import gm.proyecto.curso.repository.UnidadRepository;
import gm.proyecto.curso.repository.UsuarioRepository;
import gm.proyecto.curso.service.ContenidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/unidades/{unidadId}/contenidos") // <-- ESTA ES TU RUTA BASE
public class ContenidoController {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoController.class);

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    // --- GET (Para Admin y Estudiante) ---
    @GetMapping
    public ResponseEntity<List<Contenido>> obtenerContenidosDeUnidad(
            @PathVariable Long unidadId, Authentication authentication) {

        String email = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioOpt.get();

        if ("ADMINISTRADOR".equals(usuario.getRol())) {
            return ResponseEntity.ok(contenidoService.listarContenidosActivosPorUnidad(unidadId)); // Usar el Service
        }

        Optional<Unidad> unidadOpt = unidadRepository.findById(unidadId);
        if (unidadOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long cursoId = unidadOpt.get().getCurso().getId();

        boolean estaInscritoAceptado = inscripcionRepository.existsByEstudianteIdAndCursoIdAndEstado(
                usuario.getId(),
                cursoId,
                EstadoInscripcion.APROBADA
        );

        if (estaInscritoAceptado) {
            return ResponseEntity.ok(contenidoService.listarContenidosActivosPorUnidad(unidadId)); // Usar el Service
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // --- GET Eliminados (Solo Admin) ---
    @GetMapping("/eliminados")
    public ResponseEntity<List<Contenido>> obtenerContenidosEliminados(@PathVariable Long unidadId) {
        return ResponseEntity.ok(contenidoService.listarContenidosEliminadosPorUnidad(unidadId)); // Usar el Service
    }

    // --- POST Lección (Solo Admin) ---
    @PostMapping("/leccion")
    public ResponseEntity<Leccion> crearLeccion(@PathVariable Long unidadId, @RequestBody LeccionDTO leccionDTO) {
        return contenidoService.crearLeccion(unidadId, leccionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- POST Tarea (Solo Admin) ---
    @PostMapping("/tarea")
    public ResponseEntity<Tarea> crearTarea(@PathVariable Long unidadId, @RequestBody TareaDTO tareaDTO) {
        return contenidoService.crearTarea(unidadId, tareaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ¡PUT EDITAR (Solo Admin)! ---
    // Este es el único método PUT que debe existir aquí
    @PutMapping("/{contenidoId}")
    public ResponseEntity<?> actualizarContenido(@PathVariable Long unidadId, @PathVariable Long contenidoId, @RequestBody Map<String, Object> payload) {
        Optional<Contenido> contenidoActualizado;

        if (payload.containsKey("contenidoTexto")) { // Es Lección
            LeccionDTO dto = new LeccionDTO(
                    (String) payload.get("titulo"),
                    (String) payload.get("contenidoTexto"),
                    (List<String>) payload.get("urls")
            );
            contenidoActualizado = contenidoService.actualizarContenido(contenidoId, dto);

        } else if (payload.containsKey("instrucciones")) { // Es Tarea
            String fechaLimiteStr = (String) payload.get("fechaLimite");
            String fechaCierreStr = (String) payload.get("fechaCierre");

            LocalDateTime fechaLimite = (fechaLimiteStr != null && !fechaLimiteStr.isEmpty())
                    ? LocalDateTime.parse(fechaLimiteStr) : null;
            LocalDateTime fechaCierre = (fechaCierreStr != null && !fechaCierreStr.isEmpty())
                    ? LocalDateTime.parse(fechaCierreStr) : null;

            TareaDTO dto = new TareaDTO(
                    (String) payload.get("titulo"),
                    (String) payload.get("instrucciones"),
                    fechaLimite,
                    fechaCierre,
                    (List<String>) payload.get("urls")
            );
            contenidoActualizado = contenidoService.actualizarContenido(contenidoId, dto);
        } else {
            return ResponseEntity.badRequest().body("Tipo de contenido desconocido");
        }

        return contenidoActualizado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    // --- DELETE (Solo Admin) ---
    @DeleteMapping("/{contenidoId}")
    public ResponseEntity<Void> eliminarContenido(@PathVariable Long contenidoId) {
        return contenidoService.softDeleteContenido(contenidoId)
                .map(c -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    // --- PUT Restaurar (Solo Admin) ---
    @PutMapping("/{contenidoId}/restaurar")
    public ResponseEntity<Contenido> restaurarContenido(@PathVariable Long contenidoId) {
        return contenidoService.restaurarContenido(contenidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- DELETE Permanente (Solo Admin) ---
    @DeleteMapping("/{contenidoId}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(@PathVariable Long contenidoId) {
        if (contenidoService.deletePermanentemente(contenidoId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
