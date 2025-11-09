package gm.proyecto.curso.service;

import gm.proyecto.curso.dto.CursoProgresoDTO;
import gm.proyecto.curso.modelo.*;
import gm.proyecto.curso.repository.CursoRepository;
import gm.proyecto.curso.repository.InscripcionRepository;
import gm.proyecto.curso.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private InscripcionRepository inscripcionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Curso> listarCursosActivos() {
        return cursoRepository.findAllByActivoTrue();
    }

    public List<Curso> listarCursosEliminados() {
        return cursoRepository.findAllByActivoFalse();
    }

    public Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Curso actualizarCurso(Long id, Curso cursoActualizado) {
        return cursoRepository.findById(id).map(cursoExistente -> {
            cursoExistente.setTitulo(cursoActualizado.getTitulo());
            cursoExistente.setDescripcion(cursoActualizado.getDescripcion());
            cursoExistente.setTipoInscripcion(cursoActualizado.getTipoInscripcion());
            if (cursoActualizado.getPortadaUrl() != null) {
                cursoExistente.setPortadaUrl(cursoActualizado.getPortadaUrl());
            }
            return cursoRepository.save(cursoExistente);
        }).orElse(null);
    }

    public boolean deletePermanentemente(Long id) {
        if (cursoRepository.existsById(id)) {
            cursoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Curso> softDeleteCurso(Long id) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setActivo(false);
            return cursoRepository.save(curso);
        });
    }

    public Optional<Curso> restaurarCurso(Long id) {
        return cursoRepository.findById(id).map(curso -> {
            curso.setActivo(true);
            return cursoRepository.save(curso);
        });
    }

    // --- LÓGICA DE PROGRESO CORREGIDA ---
    public List<CursoProgresoDTO> obtenerCursosConProgresoPorUsuario(Long usuarioId) {
        Usuario estudiante = usuarioRepository.findById(usuarioId).orElse(null);
        if (estudiante == null || estudiante.getInscripciones() == null) {
            return new ArrayList<>();
        }

        List<CursoProgresoDTO> cursosConProgreso = new ArrayList<>();

        List<Inscripcion> inscripcionesAprobadas = estudiante.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.getEstado() == EstadoInscripcion.APROBADA) // <-- ¡ESTA ES LA CORRECCIÓN!
                .collect(Collectors.toList());

        for (Inscripcion inscripcion : inscripcionesAprobadas) {
            Curso curso = inscripcion.getCurso();
            if (curso == null || !curso.isActivo() || curso.getUnidades() == null) {
                continue;
            }

            // Obtenemos todas las TAREAS del curso que están activas
            List<Tarea> tareasDelCurso = curso.getUnidades().stream()
                    .filter(Unidad::isActivo)
                    .flatMap(unidad -> unidad.getContenidos().stream())
                    .filter(Contenido::isActivo)
                    .filter(Tarea.class::isInstance)
                    .map(Tarea.class::cast)
                    .collect(Collectors.toList());

            int totalTareas = tareasDelCurso.size();
            int tareasCalificadas = 0;

            if(totalTareas > 0) {
                // Contamos cuántas de esas tareas tienen una entrega CALIFICADA por el estudiante
                for(Tarea tarea : tareasDelCurso){
                    boolean calificada = tarea.getEntregas().stream()
                            .anyMatch(entrega ->
                                    entrega.getEstudiante().getId().equals(usuarioId) && entrega.getCalificacion() != null
                            );
                    if(calificada){
                        tareasCalificadas++;
                    }
                }
            }

            double progreso = (totalTareas == 0) ? 100.0 : ((double) tareasCalificadas / totalTareas) * 100.0;
            cursosConProgreso.add(new CursoProgresoDTO(curso, progreso));
        }

        return cursosConProgreso;
    }
}
