package gm.proyecto.curso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // <-- ¡IMPORTAR!
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la URL /uploads/** a la carpeta física 'uploads'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    // --- ¡AÑADIR ESTE MÉTODO COMPLETO! ---
    /**
     * Configuración Global de CORS para permitir todas las peticiones
     * desde el frontend (y Postman).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica a toda tu API
                .allowedOrigins("*") // Permite cualquier origen (para desarrollo)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ¡PERMITE PUT Y DELETE!
                .allowedHeaders("*");
    }
    // --- FIN DEL MÉTODO AÑADIDO ---
}