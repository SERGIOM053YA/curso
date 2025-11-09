package gm.proyecto.curso.controlador;

import gm.proyecto.curso.service.EmailService;
import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.dto.LoginResponse;
import gm.proyecto.curso.repository.UsuarioRepository;
import gm.proyecto.curso.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Importante
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime; // <-- AÑADE ESTE IMPORT
import org.springframework.mail.SimpleMailMessage;     // <-- AÑADE ESTE IMPORT
import org.springframework.mail.javamail.JavaMailSender; // <-- AÑADE ESTE IMPORT
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.util.UUID;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder; // <-- Inyectamos el encriptador

    @Autowired
    private EmailService emailService;

    // ¡INYECTAMOS NUESTRO PROVEEDOR DE TOKENS!
    @Autowired
    private JwtTokenProvider tokenProvider;

    // Dentro de AuthController.java

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        // 1. Encriptamos la contraseña
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));

        // 2. Asignamos el rol por defecto de forma segura
        nuevoUsuario.setRol("ESTUDIANTE"); // <-- ¡ESTA ES LA LÍNEA MÁGICA!

        // 3. Guardamos el nuevo usuario
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String rawPassword = credentials.get("password");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent() && passwordEncoder.matches(rawPassword, usuarioOptional.get().getPassword())) {
            Usuario usuario = usuarioOptional.get();

            String token = tokenProvider.generateToken(usuario.getEmail());

            // --- ¡LA CORRECCIÓN SUCEDE AQUÍ! ---
            // Ahora pasamos el ID del usuario a la respuesta.
            LoginResponse response = new LoginResponse(usuario.getId(), token, usuario.getNombreCompleto(), usuario.getRol());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
    // --- Endpoint para SOLICITAR la recuperación de contraseña ---
    // REEMPLAZA TU MÉTODO forgotPassword con esta versión simplificada
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        // --- CHISMOSO PRINCIPAL ---
        System.out.println("[AUTH CONTROLLER] Buscando usuario con email: " + email);
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        // Verificamos si se encontró y lo reportamos
        if (usuarioOptional.isPresent()) {
            System.out.println("[AUTH CONTROLLER] ¡Usuario ENCONTRADO! Procediendo...");

            usuarioOptional.ifPresent(usuario -> {
                String token = UUID.randomUUID().toString();
                usuario.setPasswordResetToken(token);
                usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
                usuarioRepository.save(usuario);

                System.out.println("[AUTH CONTROLLER] Token guardado. A punto de llamar a EmailService...");

                emailService.enviarCorreoRecuperacion(usuario.getEmail(), usuario.getNombreCompleto(), token);

                System.out.println("[AUTH CONTROLLER] Llamada a EmailService completada.");
            });
        } else {
            // Si no se encontró, lo sabremos
            System.err.println("[AUTH CONTROLLER] ¡ERROR! Usuario NO encontrado con email: " + email);
        }

        return ResponseEntity.ok("Si tu correo está registrado, recibirás las instrucciones...");
    }
    // --- Endpoint para CONFIRMAR la nueva contraseña ---
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String nuevaPassword = payload.get("password");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByPasswordResetToken(token);

        // Verificamos si el token existe Y si no ha expirado
        if (usuarioOptional.isPresent() && usuarioOptional.get().getPasswordResetTokenExpiry().isAfter(LocalDateTime.now())) {
            Usuario usuario = usuarioOptional.get();

            // Actualizamos la contraseña (¡siempre encriptada!)
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));

            // Limpiamos los campos del token para que no se pueda reutilizar
            usuario.setPasswordResetToken(null);
            usuario.setPasswordResetTokenExpiry(null);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("¡Contraseña actualizada con éxito!");
        } else {
            // Si el token no es válido o ha expirado
            return ResponseEntity.badRequest().body("El token no es válido o ha expirado.");
        }
    }
}