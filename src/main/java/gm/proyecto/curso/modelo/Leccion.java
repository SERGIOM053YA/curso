package gm.proyecto.curso.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "lecciones")
@DiscriminatorValue("LECCION") // <-- ¡AÑADE ESTA LÍNEA!
@Data
@EqualsAndHashCode(callSuper = true)
public class Leccion extends Contenido {

    private String contenidoTexto;

    // Getters y Setters
    public String getContenidoTexto() { return contenidoTexto; }
    public void setContenidoTexto(String contenidoTexto) { this.contenidoTexto = contenidoTexto; }

}