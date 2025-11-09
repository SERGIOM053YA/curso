package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Examen;
import gm.proyecto.curso.modelo.ResultadoExamen;
import gm.proyecto.curso.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultadoExamenRepository extends JpaRepository<ResultadoExamen, Long> {
    Optional<ResultadoExamen> findByExamenAndEstudiante(Examen examen, Usuario estudiante);
}