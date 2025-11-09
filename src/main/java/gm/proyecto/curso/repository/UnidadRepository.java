package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    // Método para buscar unidades activas de un curso específico
    List<Unidad> findByCursoIdAndActivoTrue(Long cursoId);

    // Método para buscar unidades eliminadas (inactivas) de un curso específico
    List<Unidad> findByCursoIdAndActivoFalse(Long cursoId);

}
