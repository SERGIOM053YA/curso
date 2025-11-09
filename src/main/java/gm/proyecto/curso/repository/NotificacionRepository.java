package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.Inscripcion;
import gm.proyecto.curso.modelo.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // --- ¡AÑADE ESTE MÉTODO COMPLETO! ---
    // Esta consulta le dice a JPA: "Trae las notificaciones y, de una vez,
    // únelas con sus inscripciones, cursos y estudiantes correspondientes".
    @Query(value = "SELECT n FROM Notificacion n JOIN FETCH n.inscripcion i JOIN FETCH i.curso JOIN FETCH i.estudiante WHERE n.leida = false ")
    List<Notificacion> findNotificacionesPendientesWithDetails();



}