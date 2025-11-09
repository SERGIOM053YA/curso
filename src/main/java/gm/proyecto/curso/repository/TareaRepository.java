package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // Spring Data JPA se encargará de las operaciones básicas (findById, save, etc.)
    // y sabrá que debe buscar en la jerarquía de Contenido por instancias de Tarea.
}

