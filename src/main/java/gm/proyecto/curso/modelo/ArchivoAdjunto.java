package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "archivos_adjuntos")
@Data
public class ArchivoAdjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- CAMBIAR DE Integer a Long

    @Column(nullable = false)
    private String url; // Aquí guardaremos la URL de YouTube o la ruta al archivo

    @Enumerated(EnumType.STRING)
    private TipoRecurso tipo; // Para saber si es un VIDEO o un ARCHIVO

    // Relación: Muchos archivos pueden pertenecer a UN contenido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenido_id")
    @JsonBackReference
    private Contenido contenido;

    public enum TipoRecurso {
        VIDEO,
        ARCHIVO
    }
}