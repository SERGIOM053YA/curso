package gm.proyecto.curso.repository;

import gm.proyecto.curso.modelo.PreguntaAbierta; // Asegúrate de importar esto
import gm.proyecto.curso.modelo.RespuestaEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<RespuestaEstudiante, Long> {

    // ESTE ES EL MÉTODO QUE TE FALTA (para la línea 219)
    // Busca respuestas por ID de Examen, que no estén calificadas y sean de tipo PreguntaAbierta
    @Query("SELECT r FROM RespuestaEstudiante r JOIN r.pregunta p " +
            "WHERE p.examen.id = :examenId " +
            "AND r.calificadaManualmente = false " +
            "AND TYPE(p) = PreguntaAbierta") // TYPE(p) es la forma correcta en JPA de checar la subclase
    List<RespuestaEstudiante> findByPregunta_Examen_IdAndCalificadaManualmenteIsFalseAndPregunta_Dtype(
            @Param("examenId") Long examenId,
            @Param("dtype") String dtype // El @Param("dtype") no se usa, pero lo dejamos para que el nombre del método coincida
    );


    // ESTE ES EL SEGUNDO MÉTODO QUE TE FALTA (para la línea 253)
    // Busca respuestas por ID de Resultado, que no estén calificadas y sean de tipo PreguntaAbierta
    @Query("SELECT r FROM RespuestaEstudiante r JOIN r.pregunta p " +
            "WHERE r.resultadoExamen.id = :resultadoId " +
            "AND r.calificadaManualmente = false " +
            "AND TYPE(p) = PreguntaAbierta")
    List<RespuestaEstudiante> findByResultadoExamen_IdAndCalificadaManualmenteIsFalseAndPregunta_Dtype(
            @Param("resultadoId") Long resultadoId,
            @Param("dtype") String dtype
    );
}

