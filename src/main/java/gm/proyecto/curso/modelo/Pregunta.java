package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// ¡YA NO NECESITAMOS JsonIgnoreProperties, JsonProperty NI SerializedName AQUÍ!

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_pregunta") // Columna que diferencia los tipos

// Estas anotaciones SÍ son necesarias para que el backend ENVÍE el JSON correctamente
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoPregunta") // Propiedad en el JSON para identificar el tipo
@JsonSubTypes({
        @JsonSubTypes.Type(value = PreguntaAbierta.class, name = "ABIERTA"),
        @JsonSubTypes.Type(value = PreguntaVerdaderoFalso.class, name = "VERDADERO_FALSO"),
        @JsonSubTypes.Type(value = PreguntaOpcionMultiple.class, name = "OPCION_MULTIPLE"),
        @JsonSubTypes.Type(value = PreguntaSeleccionMultiple.class, name = "SELECCION_MULTIPLE")
})
// --- FIN DE LAS ANOTACIONES DE JSON ---

public abstract class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    // El puntaje que vale esta pregunta (ej. 1.0, 2.5, etc.)
    private Double puntaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id")
    @JsonIgnore // Evita bucles infinitos al convertir a JSON
    private Examen examen;

    // --- ¡EL CAMPO "tipo" SE FUE! ---
    // (No pertenece a la entidad del backend)
}
