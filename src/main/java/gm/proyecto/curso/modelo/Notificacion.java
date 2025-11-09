package gm.proyecto.curso.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- CAMBIAR DE Integer a Long

    private String mensaje;

    private boolean leida = false;

    // Relacionamos la notificación con la inscripción que la generó
    @OneToOne
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;
}