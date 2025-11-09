package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Entrega;
import gm.proyecto.curso.modelo.Tarea; // <-- AÑADIR IMPORT
import gm.proyecto.curso.modelo.Usuario; // <-- AÑADIR IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    List<Entrega> findByTareaId(Long tareaId);

    // --- ¡AÑADIR ESTE MÉTODO! ---
    /**
     * Comprueba si ya existe una entrega para una tarea y estudiante específicos.
     */
    boolean existsByTareaAndEstudiante(Tarea tarea, Usuario estudiante);
}