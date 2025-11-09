package gm.proyecto.curso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    // @Async
    public void enviarCorreoRecuperacion(String destinatario, String nombre, String token) {
        System.out.println("[EMAIL SERVICE] Método llamado para: " + destinatario); // <-- Chismoso 1

        // --- VERIFICACIÓN DE INYECCIÓN ---
        if (mailSender == null) {
            System.err.println("[EMAIL SERVICE] ¡ERROR CRÍTICO! mailSender es NULL.");
            return;
        }
        if (fromEmail == null || fromEmail.isEmpty()) {
            System.err.println("[EMAIL SERVICE] ¡ERROR CRÍTICO! fromEmail es NULL o vacío.");
            return;
        }
        System.out.println("[EMAIL SERVICE] mailSender y fromEmail OK."); // <-- Chismoso 2

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("Recuperación de Contraseña - Plataforma de Cursos");

            // --- ¡NUEVO CHISMOSO! ---
            // Verificamos que el token tenga un valor ANTES de usarlo.
            System.out.println("[EMAIL SERVICE DEBUG] Token a incluir en el correo: " + token);

            message.setText("Hola " + nombre + ",\n\n"
                    + "Has solicitado restablecer tu contraseña. Copia el siguiente token y pégalo en la aplicación:\n\n"
                    + token + "\n\n" // <-- Aquí se usa el token
                    + "Si no solicitaste esto, por favor ignora este correo.\n"
                    + "El token expirará en 1 hora.");

            mailSender.send(message);
            System.out.println("[EMAIL SERVICE] Orden de envío para " + destinatario + " completada sin errores.");

        } catch (Exception e) {
            System.err.println("[EMAIL SERVICE] ¡ERROR DETALLADO AL INTENTAR ENVIAR CORREO!");
            e.printStackTrace();
        }
    }
}