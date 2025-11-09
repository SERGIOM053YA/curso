package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import gm.proyecto.curso.modelo.PreguntaAbierta;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAllByActivoTrue();
    List<Curso> findAllByActivoFalse();
}

