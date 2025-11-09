package gm.proyecto.curso.modelo; // Asegúrate de que el paquete sea el correcto

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- ¡AÑADE ESTE IMPORT!
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String portadaUrl;

    @Enumerated(EnumType.STRING)
    private TipoInscripcion tipoInscripcion;

    private boolean activo = true;

    // --- ¡LA CORRECCIÓN MÁGICA ESTÁ AQUÍ! ---
    // Le decimos al convertidor de JSON que ignore esta lista para evitar bucles.
    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unidad> unidades;
}
