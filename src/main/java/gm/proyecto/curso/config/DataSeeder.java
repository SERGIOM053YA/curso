package gm.proyecto.curso.config; // (O el paquete donde lo hayas creado)

import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Esta clase se ejecuta automáticamente UNA VEZ al iniciar el backend.
 * Su propósito es crear los administradores definidos si no existen.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        crearAdminsSiNoExisten();
    }

    private void crearAdminsSiNoExisten() {
        // Encriptamos la contraseña "1234" UNA SOLA VEZ
        String contrasenaEncriptada = passwordEncoder.encode("1234");

        // Definimos los 3 administradores
        Map<String, String> adminsACrear = Map.of(
                "humiko.hernandez@upvm.edu.mx", "Humiko Hernandez",
                "beatriz.perez@upvm.edu.mx", "Beatriz Perez",
                "sergio.mendoza.plascencia@upvm.edu.mx", "Sergio Mendoza Plascencia",
                "elizabeth.sanchez@upvm.edu.mx", "Elizabeth Sanchez"
        );

        for (Map.Entry<String, String> entry : adminsACrear.entrySet()) {
            String email = entry.getKey();
            String nombre = entry.getValue();

            // 1. Verificamos si el usuario YA EXISTE en la BD
            if (usuarioRepository.findByEmail(email).isEmpty()) {

                // 2. Si no existe, lo creamos
                Usuario admin = new Usuario();
                admin.setEmail(email);
                admin.setNombreCompleto(nombre);
                admin.setPassword(contrasenaEncriptada);
                admin.setRol("ADMINISTRADOR");

                // 3. Guardamos el nuevo admin en la base de datos
                usuarioRepository.save(admin);
                System.out.println("====== ¡ADMIN CREADO!: " + email + " ======");
            }
        }
    }
}
