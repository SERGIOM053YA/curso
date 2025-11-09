package gm.proyecto.curso.security;

import gm.proyecto.curso.modelo.Usuario;
import gm.proyecto.curso.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // En CustomUserDetailsService.java

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // --- ¡AQUÍ ESTÁ EL MICRÓFONO OCULTO! ---
        // Vamos a ver qué rol está leyendo exactamente desde la base de datos.
        String rol = usuario.getRol();
        System.out.println("DEBUG-SECURITY: Rol del usuario desde la BD -> '" + rol + "'");
        // ------------------------------------------

        return new User(usuario.getEmail(), usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(rol)));
    }
}