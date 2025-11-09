package gm.proyecto.curso.modelo;

import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pregunta_abierta")
@DiscriminatorValue("ABIERTA") // <-- ¡AÑADIR ESTA LÍNEA!
@Data
@EqualsAndHashCode(callSuper = true)
public class PreguntaAbierta extends Pregunta {

}