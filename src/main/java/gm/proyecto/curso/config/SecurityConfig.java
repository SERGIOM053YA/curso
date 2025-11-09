package gm.proyecto.curso.config;

import gm.proyecto.curso.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- ¡ESTE ES EL MÉTODO COMPLETO Y CORREGIDO! ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // --- REGLA #0: Peticiones de Permiso (CORS) ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- REGLA #1: Públicas ---
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // --- REGLA #2: Estudiante (Reglas Específicas) ---
                        // (Las reglas más específicas DEBEN ir primero)
                        .requestMatchers(HttpMethod.GET, "/api/examenes/{examenId}/mi-resultado").hasAnyAuthority("ADMIN", "ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/entregas/**").hasAuthority("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/respuestas/examen/**").hasAuthority("ESTUDIANTE") // Estudiante ENVÍA
                        .requestMatchers(HttpMethod.POST, "/api/inscripciones/**").hasAuthority("ESTUDIANTE")

                        // --- REGLA #3: Admin (Reglas Específicas) ---
                        // (Estas reglas de admin deben ir ANTES de las generales de admin)
                        .requestMatchers(HttpMethod.POST, "/api/cursos/{cursoId}/unidades").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/respuestas/examen/{examenId}/abiertas").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/respuestas/{respuestaId}/calificar-manual").hasAuthority("ADMIN")

                        // --- REGLA #4: Admin (Generales) ---
                        // Cursos
                        .requestMatchers(HttpMethod.POST, "/api/cursos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cursos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cursos/**").hasAuthority("ADMIN")

                        // Unidades
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/unidades/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{id}/restaurar").hasAuthority("ADMIN")

                        // Contenido
                        .requestMatchers(HttpMethod.POST, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN")

                        // Otros Admin
                        .requestMatchers(HttpMethod.POST, "/api/upload").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/examenes/**").hasAuthority("ADMIN") // <-- ¡LA LÍNEA QUE FALTA!
                        .requestMatchers("/api/examenes/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/notificaciones/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/tareas/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/entregas/**").hasAuthority("ADMIN")

                        // --- REGLA #5: Compartidas (Ver) ---
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/unidades/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").authenticated()

                        // --- REGLA #6: Denegar todo lo demás ---
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // --- REGLA #0: Peticiones de Permiso (CORS) ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- REGLA #1: Públicas ---
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // --- REGLA #2: Estudiante (Acciones Específicas) ---
                        .requestMatchers(HttpMethod.POST, "/api/entregas/**").hasAuthority("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/respuestas/examen/**").hasAuthority("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/inscripciones/**").hasAuthority("ESTUDIANTE")

                        // --- REGLA #3: Admin (Modificaciones) ---
                        // Cursos
                        .requestMatchers(HttpMethod.POST, "/api/cursos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cursos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cursos/**").hasAuthority("ADMIN")
                        // Unidades (Crear, Editar, Borrar Unidades)
                        .requestMatchers(HttpMethod.POST, "/api/cursos/{cursoId}/unidades").hasAuthority("ADMIN") // <-- Ruta nueva// POST /api/unidades (Añadir unidad a un curso)
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{id}").hasAuthority("ADMIN") // PUT /api/unidades/1
                        .requestMatchers(HttpMethod.DELETE, "/api/unidades/{id}").hasAuthority("ADMIN") // DELETE /api/unidades/1
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{id}/restaurar").hasAuthority("ADMIN") // PUT /api/unidades/1/restaurar


                        // Contenido (Crear, Editar, Borrar Contenido)
                        .requestMatchers(HttpMethod.POST, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN") // POST /api/unidades/1/contenidos/leccion
                        .requestMatchers(HttpMethod.PUT, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN") // PUT /api/unidades/1/contenidos/9
                        .requestMatchers(HttpMethod.DELETE, "/api/unidades/{unidadId}/contenidos/**").hasAuthority("ADMIN") // DELETE /api/unidades/1/contenidos/9

                        // V--- ¡PÉGALA AQUÍ ARRIBA Y MODIFÍCALA! ---V
                        .requestMatchers(HttpMethod.GET, "/api/examenes/{examenId}/mi-resultado").hasAnyAuthority("ADMIN", "ESTUDIANTE")

                        // Otros Admin
                        .requestMatchers("/api/examenes/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/notificaciones/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/tareas/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/upload").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/entregas/**").hasAuthority("ADMIN") // Calificar

                        // --- REGLA #4: Compartidas (Ver) ---
                        .requestMatchers(HttpMethod.GET, "/api/cursos/**").authenticated() // Ver catálogo, mis cursos, unidades de curso
                        .requestMatchers(HttpMethod.GET, "/api/unidades/**").authenticated() // Ver unidades, ver contenido
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated() // Ver perfil
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").authenticated() // Actualizar perfil


                        // --- REGLA #5: Denegar todo lo demás ---
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }*/
//}