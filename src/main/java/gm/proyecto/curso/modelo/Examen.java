package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Representa un Examen, que es un tipo de Contenido.
 * Un examen contiene una lista de preguntas.
 */
@Entity
@DiscriminatorValue("EXAMEN")
@Data
@EqualsAndHashCode(callSuper = true)
public class Examen extends Contenido {

    // Un examen puede tener muchas preguntas.
    // Usamos EAGER para que al pedir un examen, siempre vengan sus preguntas.
    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // <-- ¡SIN orphanRemoval!
    @JsonManagedReference
    private List<Pregunta> preguntas;

    // Podríamos añadir campos como tiempoLímite en minutos, etc.
    private Integer tiempoLimiteMinutos;
}

