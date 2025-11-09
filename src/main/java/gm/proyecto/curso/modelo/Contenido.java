package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List; // <-- IMPORTACIÓN NECESARIA

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_contenido") // <-- ¡AÑADE ESTA LÍNEA!
@Table(name = "contenido")
@Data
public abstract class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- CAMBIAR DE Integer a Long

    @Column(nullable = false)
    private String titulo;

    // --- RELACIÓN CON UNIDAD ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    @JsonBackReference // <-- Anotación para evitar bucles con Unidad
    private Unidad unidad;

    // --- NUEVA RELACIÓN CON ARCHIVOS ---
    @OneToMany(mappedBy = "contenido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // <-- Anotación para evitar bucles con ArchivoAdjunto
    private List<ArchivoAdjunto> archivos;
    private boolean activo = true;
}