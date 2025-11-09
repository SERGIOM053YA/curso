package gm.proyecto.curso.service;

import gm.proyecto.curso.modelo.*;
import gm.proyecto.curso.repository.CursoRepository;
import gm.proyecto.curso.repository.InscripcionRepository;
import gm.proyecto.curso.repository.UsuarioRepository;
import gm.proyecto.curso.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NotificacionRepository notificacionRepository;

    public Inscripcion solicitarInscripcion(Long cursoId, Long usuarioId) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (cursoOpt.isPresent() && usuarioOpt.isPresent()) {
            Curso curso = cursoOpt.get();
            Usuario usuario = usuarioOpt.get();

            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Verificamos si ya existe una inscripción ANTES de crear una nueva.
            if (inscripcionRepository.existsByCursoAndEstudiante(curso, usuario)) {
                // Si ya existe, devolvemos null. El controlador
                // devolverá un error 400 (Bad Request) o 409 (Conflict).
                return null;
            }
            // --- FIN DE LA CORRECCIÓN ---

            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setCurso(curso);
            inscripcion.setEstudiante(usuario);
            inscripcion.setFechaDeInscripcion(new Date());

            if (curso.getTipoInscripcion() == TipoInscripcion.ABIERTA) {
                inscripcion.setEstado(EstadoInscripcion.APROBADA);
            } else {
                inscripcion.setEstado(EstadoInscripcion.PENDIENTE);
            }

            Inscripcion inscripcionGuardada = inscripcionRepository.save(inscripcion);

            if (inscripcionGuardada.getEstado() == EstadoInscripcion.PENDIENTE) {
                Notificacion notificacion = new Notificacion();
                notificacion.setInscripcion(inscripcionGuardada);
                notificacion.setLeida(false);
                notificacion.setMensaje("El usuario " + usuario.getNombreCompleto() + " ha solicitado inscribirse al curso '" + curso.getTitulo() + "'.");
                notificacionRepository.save(notificacion);
            }

            return inscripcionGuardada;
        }
        return null;
    }
}