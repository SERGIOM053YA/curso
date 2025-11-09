package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "entregas")
@Data
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tarea_id", nullable = false)
    @JsonBackReference // Anotación clave para romper el bucle con Tarea
    private Tarea tarea;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    private LocalDateTime fechaDeEntrega;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "entrega_archivos", joinColumns = @JoinColumn(name = "entrega_id"))
    @Column(name = "archivo_url")
    private List<String> archivosUrl;

    private Double calificacion;
}



