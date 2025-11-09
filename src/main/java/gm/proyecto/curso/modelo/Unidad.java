package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- IMPORTACIÓN CLAVE
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "unidades")
@Data
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- CAMBIAR DE Integer a Long

    private String titulo;

    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonBackReference
    private Curso curso;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // <-- ESTA ES LA ANOTACIÓN QUE FALTABA
    private List<Contenido> contenidos;
}