package gm.proyecto.curso.modelo; // Asegúrate de que el paquete sea el correcto

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

import java.util.List;

@Entity
@Table(name = "usuarios") // Es buena práctica especificar el nombre de la tabla
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- CAMBIAR DE Integer a Long

    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;
    private String avatarUrl;

    // --- ¡AÑADE ESTOS DOS CAMPOS NUEVOS! ---
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;
    // --- ¡LA CORRECCIÓN MÁGICA ESTÁ AQUÍ! ---
    // Le decimos que la relación está mapeada por el campo 'estudiante' en la clase Inscripcion.
    @JsonIgnore // <-- ¡AÑADE ESTA LÍNEA MÁGICA!
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscripcion> inscripciones;
}