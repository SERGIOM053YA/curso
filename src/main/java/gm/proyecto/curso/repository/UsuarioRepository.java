package gm.proyecto.curso.repository;


import gm.proyecto.curso.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // <-- Añade esta importación

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Data JPA creará la consulta automáticamente por el nombre del método
    Optional<Usuario> findByEmail(String email);
    // <-- ¡AÑADE ESTE NUEVO MÉTODO! -->
    Optional<Usuario> findByPasswordResetToken(String token);
}
