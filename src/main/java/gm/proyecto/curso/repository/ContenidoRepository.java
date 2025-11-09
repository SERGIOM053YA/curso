package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {
    List<Contenido> findByUnidadIdAndActivoTrue(Long unidadId);
    List<Contenido> findByUnidadIdAndActivoFalse(Long unidadId);
}
