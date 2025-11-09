package gm.proyecto.curso.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.proyecto.curso.modelo.Curso;
import gm.proyecto.curso.modelo.Unidad; // <-- ¡AÑADIDO!
import gm.proyecto.curso.service.CursoService;
import gm.proyecto.curso.service.FileStorageService;
import gm.proyecto.curso.service.UnidadService; // <-- ¡AÑADIDO!
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List; // <-- ¡AÑADIDO!

@RestController
@RequestMapping("/api/cursos") // Ruta base para todo en este controlador
public class CursoController {

    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);

    @Autowired
    private CursoService cursoService;

    @Autowired
    private FileStorageService fileStorageService;

    // --- ¡AÑADIDO! ---
    // Necesitamos el servicio de unidades para listar las unidades de un curso
    @Autowired
    private UnidadService unidadService;
    // --- ---

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public List<Curso> obtenerTodosLosCursos() {
        return cursoService.listarCursosActivos();
    }

    @GetMapping("/eliminados")
    public List<Curso> obtenerCursosEliminados() {
        return cursoService.listarCursosEliminados();
    }

    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Void> eliminarPermanentemente(@PathVariable Long id) {
        if (cursoService.deletePermanentemente(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Curso> crearCurso(
            @RequestParam("curso") String cursoJson,
            @RequestParam(value = "portada", required = false) MultipartFile portadaFile) {
        try {
            Curso curso = objectMapper.readValue(cursoJson, Curso.class);
            if (portadaFile != null && !portadaFile.isEmpty()) {
                String portadaUrl = fileStorageService.storeFile(portadaFile);
                curso.setPortadaUrl(portadaUrl);
            }
            Curso cursoGuardado = cursoService.guardarCurso(curso);
            return ResponseEntity.ok(cursoGuardado);
        } catch (Exception e) {
            logger.error("Error al crear el curso: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Curso> actualizarCurso(
            @PathVariable Long id,
            @RequestParam("curso") String cursoJson,
            @RequestParam(value = "portada", required = false) MultipartFile portadaFile) {
        try {
            Curso cursoDatosNuevos = objectMapper.readValue(cursoJson, Curso.class);
            if (portadaFile != null && !portadaFile.isEmpty()) {
                String portadaUrl = fileStorageService.storeFile(portadaFile);
                cursoDatosNuevos.setPortadaUrl(portadaUrl);
            }
            Curso cursoActualizado = cursoService.actualizarCurso(id, cursoDatosNuevos);
            return (cursoActualizado != null) ? ResponseEntity.ok(cursoActualizado) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error al actualizar curso con id {}: ", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        return cursoService.softDeleteCurso(id)
                .map(c -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Curso> restaurarCurso(@PathVariable Long id) {
        return cursoService.restaurarCurso(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ¡MÉTODOS MOVIDOS AQUÍ! ---
    // Responde a: GET /api/cursos/{cursoId}/unidades
    // (Esta es la ruta que tu frontend está buscando)
    @GetMapping("/{cursoId}/unidades")
    public ResponseEntity<List<Unidad>> obtenerUnidadesPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(unidadService.obtenerUnidadesActivasPorCurso(cursoId));
    }

    // Responde a: GET /api/cursos/{cursoId}/unidades/eliminadas
    @GetMapping("/{cursoId}/unidades/eliminadas")
    public ResponseEntity<List<Unidad>> obtenerUnidadesEliminadasPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(unidadService.obtenerUnidadesEliminadasPorCurso(cursoId));
    }

    @PostMapping("/{cursoId}/unidades")
    public ResponseEntity<Unidad> crearUnidadParaCurso(
            @PathVariable Long cursoId,
            @RequestBody Unidad unidad) {

        // Usamos el unidadService que ya tenías inyectado
        Unidad nuevaUnidad = unidadService.crearUnidad(cursoId, unidad);

        if (nuevaUnidad != null) {
            return ResponseEntity.ok(nuevaUnidad); // Devuelve 200 OK con la unidad creada
        } else {
            // Esto sucede si el cursoId no se encontró
            return ResponseEntity.notFound().build();
        }
    }
}