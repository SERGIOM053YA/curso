package gm.proyecto.curso.controlador;

import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.service.UsuarioService;
import gm.proyecto.curso.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import gm.proyecto.curso.dto.CursoProgresoDTO;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CursoService cursoService;

    // --- Endpoint para OBTENER datos del perfil ---
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok) // Si lo encuentra, devuelve 200 OK con el usuario
                .orElse(ResponseEntity.notFound().build()); // Si no, devuelve 404 Not Found
    }

    // --- Endpoint para ACTUALIZAR datos del perfil (nombre, email, contraseña) ---
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datosUsuario) {
        return usuarioService.actualizarUsuario(id, datosUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoint para ACTUALIZAR la imagen de perfil (avatar) ---
    @PutMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public ResponseEntity<Usuario> actualizarAvatar(
            @PathVariable Long id,
            @RequestParam("avatar") MultipartFile avatarFile) {
        return usuarioService.actualizarAvatar(id, avatarFile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}/cursos-inscritos")
    public ResponseEntity<List<CursoProgresoDTO>> obtenerCursosInscritosPorUsuario(@PathVariable Long id) {
        // Llama al método que creamos en CursoService
        List<CursoProgresoDTO> cursosConProgreso = cursoService.obtenerCursosConProgresoPorUsuario(id);

        // Devuelve la lista (podría estar vacía si no hay cursos o el usuario no existe)
        return ResponseEntity.ok(cursosConProgreso);
    }
}