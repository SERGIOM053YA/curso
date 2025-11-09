package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Curso; // <-- AÑADIR IMPORT
import gm.proyecto.curso.modelo.EstadoInscripcion;
import gm.proyecto.curso.modelo.Inscripcion;
import gm.proyecto.curso.modelo.Usuario; // <-- AÑADIR IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEstudianteIdAndCursoIdAndEstado(Long estudianteId, Long cursoId, EstadoInscripcion estado);

    // --- ¡AÑADIR ESTE MÉTODO! ---
    /**
     * Comprueba si ya existe CUALQUIER inscripción (pendiente, aprobada, etc.)
     * para un curso y estudiante específicos.
     */
    boolean existsByCursoAndEstudiante(Curso curso, Usuario estudiante);
}