package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.Entrega;
import gm.proyecto.curso.repository.EntregaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @Autowired
    private EntregaRepository entregaRepository;

    /**
     * Endpoint para que el ADMIN obtenga todas las entregas de una tarea específica.
     * Esta es la URL correcta que el frontend debe llamar con GET.
     * @param id El ID de la Tarea.
     * @return Una lista de objetos Entrega.
     */
    @GetMapping("/{id}/entregas")
    public ResponseEntity<List<Entrega>> obtenerEntregasDeTarea(@PathVariable Long id) {
        return ResponseEntity.ok(entregaRepository.findByTareaId(id));
    }
}