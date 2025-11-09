package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Examen.
 * Proporciona los métodos CRUD (Crear, Leer, Actualizar, Borrar) para los exámenes.
 */
@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {
    // Spring Data JPA generará automáticamente los métodos básicos.
    // Por ahora no necesitamos métodos personalizados.
}
