package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Pregunta.
 * Proporciona los métodos CRUD para todos los tipos de preguntas.
 */
@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    // Spring Data JPA se encargará de manejar los diferentes tipos de preguntas
    // que heredan de la clase base Pregunta.
}