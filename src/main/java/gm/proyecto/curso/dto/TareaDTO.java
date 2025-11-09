package gm.proyecto.curso.dto;

import java.time.LocalDateTime;
import java.util.List;

// --- ¡AÑADIMOS fechaCierre AQUÍ! ---
public record TareaDTO(
        String titulo,
        String instrucciones,
        LocalDateTime fechaLimite,
        LocalDateTime fechaCierre, // <--- CAMPO AÑADIDO
        List<String> urls
) {
}