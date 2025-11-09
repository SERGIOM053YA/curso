package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.Unidad;
import gm.proyecto.curso.service.UnidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// (Ya no se necesitan otros imports como ContenidoService, ObjectMapper, etc.)

@RestController
@RequestMapping("/api/unidades") // La ruta base es /api/unidades
public class UnidadController {

    @Autowired
    private UnidadService unidadService;

    // --- MÉTODOS PARA UNIDADES ---
    // (Toda la lógica de /contenidos se fue a ContenidoController)
    // (Toda la lógica de /curso/{id} se fue a CursoController)

    // Responde a: PUT /api/unidades/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Unidad> actualizarUnidad(@PathVariable Long id, @RequestBody Unidad unidad) {
        return unidadService.actualizarUnidad(id, unidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Responde a: DELETE /api/unidades/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUnidad(@PathVariable Long id) {
        return unidadService.softDeleteUnidad(id)
                .map(u -> ResponseEntity.ok().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    // Responde a: PUT /api/unidades/{id}/restaurar
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Unidad> restaurarUnidad(@PathVariable Long id) {
        return unidadService.restaurarUnidad(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
