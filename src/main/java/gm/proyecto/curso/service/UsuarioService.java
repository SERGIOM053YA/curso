package gm.proyecto.curso.service;

import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    // --- Método para obtener los datos de un usuario ---
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // --- Método para actualizar los datos del perfil (nombre, email, contraseña) ---
    public Optional<Usuario> actualizarUsuario(Long id, Usuario datosNuevos) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            // Actualizamos el nombre si se proporcionó uno nuevo
            if (datosNuevos.getNombreCompleto() != null && !datosNuevos.getNombreCompleto().isEmpty()) {
                usuarioExistente.setNombreCompleto(datosNuevos.getNombreCompleto());
            }

            // Actualizamos el email si se proporcionó uno nuevo
            if (datosNuevos.getEmail() != null && !datosNuevos.getEmail().isEmpty()) {
                usuarioExistente.setEmail(datosNuevos.getEmail());
            }

            // ¡MUY IMPORTANTE! Si se proporciona una nueva contraseña, la encriptamos.
            if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
                usuarioExistente.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
            }

            return usuarioRepository.save(usuarioExistente);
        });
    }

    // --- Método para actualizar solo la imagen de perfil (avatar) ---
    public Optional<Usuario> actualizarAvatar(Long id, MultipartFile avatarFile) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Reutilizamos el servicio que ya funciona para guardar archivos
                String avatarUrl = fileStorageService.storeFile(avatarFile);
                usuarioExistente.setAvatarUrl(avatarUrl);
            }
            return usuarioRepository.save(usuarioExistente);
        });
    }
}
