package gm.proyecto.curso.service;

import gm.proyecto.curso.dto.LeccionDTO;
import gm.proyecto.curso.dto.TareaDTO;
import gm.proyecto.curso.modelo.*;
import gm.proyecto.curso.repository.ContenidoRepository;
import gm.proyecto.curso.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    public Optional<Leccion> crearLeccion(Long unidadId, LeccionDTO leccionDTO) {
        return unidadRepository.findById(unidadId).map(unidad -> {
            Leccion nuevaLeccion = new Leccion();
            nuevaLeccion.setTitulo(leccionDTO.titulo());
            nuevaLeccion.setContenidoTexto(leccionDTO.contenidoTexto());
            nuevaLeccion.setUnidad(unidad);
            nuevaLeccion.setActivo(true);

            List<ArchivoAdjunto> archivos = new ArrayList<>();
            if (leccionDTO.urls() != null) {
                for (String url : leccionDTO.urls()) {
                    ArchivoAdjunto archivo = new ArchivoAdjunto();
                    archivo.setUrl(url);
                    archivo.setTipo(ArchivoAdjunto.TipoRecurso.VIDEO);
                    archivo.setContenido(nuevaLeccion);
                    archivos.add(archivo);
                }
            }
            nuevaLeccion.setArchivos(archivos);

            return contenidoRepository.save(nuevaLeccion);
        });
    }

    public Optional<Tarea> crearTarea(Long unidadId, TareaDTO tareaDTO) {
        return unidadRepository.findById(unidadId).map(unidad -> {
            Tarea nuevaTarea = new Tarea();
            nuevaTarea.setTitulo(tareaDTO.titulo());
            nuevaTarea.setInstrucciones(tareaDTO.instrucciones());
            nuevaTarea.setFechaLimite(tareaDTO.fechaLimite()); // Fecha "a tiempo"
            nuevaTarea.setFechaCierre(tareaDTO.fechaCierre());
            nuevaTarea.setUnidad(unidad);
            nuevaTarea.setActivo(true);

            List<ArchivoAdjunto> archivos = new ArrayList<>();
            if (tareaDTO.urls() != null) {
                for (String url : tareaDTO.urls()) {
                    ArchivoAdjunto archivo = new ArchivoAdjunto();
                    archivo.setUrl(url);
                    archivo.setTipo(ArchivoAdjunto.TipoRecurso.ARCHIVO);
                    archivo.setContenido(nuevaTarea);
                    archivos.add(archivo);
                }
            }
            nuevaTarea.setArchivos(archivos);

            return contenidoRepository.save(nuevaTarea);
        });
    }

    public Optional<Contenido> actualizarContenido(Long contenidoId, Object contenidoDTO) {
        return contenidoRepository.findById(contenidoId).map(contenidoExistente -> {
            if (contenidoExistente instanceof Leccion leccion && contenidoDTO instanceof LeccionDTO dto) {
                leccion.setTitulo(dto.titulo());
                leccion.setContenidoTexto(dto.contenidoTexto());
                // Lógica futura para actualizar archivos
                return contenidoRepository.save(leccion);
            } else if (contenidoExistente instanceof Tarea tarea && contenidoDTO instanceof TareaDTO dto) {
                tarea.setTitulo(dto.titulo());
                tarea.setInstrucciones(dto.instrucciones());
                tarea.setFechaLimite(dto.fechaLimite());
                tarea.setFechaCierre(dto.fechaCierre());
                // Lógica futura para actualizar archivos
                return contenidoRepository.save(tarea);
            }
            return null;
        });
    }

    public Optional<Contenido> softDeleteContenido(Long contenidoId) {
        return contenidoRepository.findById(contenidoId).map(contenido -> {
            contenido.setActivo(false);
            return contenidoRepository.save(contenido);
        });
    }
    public Optional<Contenido> restaurarContenido(Long contenidoId) {
        return contenidoRepository.findById(contenidoId).map(contenido -> {
            contenido.setActivo(true);
            return contenidoRepository.save(contenido);
        });
    }

    public boolean deletePermanentemente(Long contenidoId) {
        if (contenidoRepository.existsById(contenidoId)) {
            contenidoRepository.deleteById(contenidoId);
            return true;
        }
        return false;
    }

    // --- ¡MÉTODOS AÑADIDOS QUE FALTABAN! ---

    /**
     * Obtiene una lista de todo el contenido ACTIVO para una unidad específica.
     * @param unidadId El ID de la unidad.
     * @return Lista de Contenido activo.
     */
    public List<Contenido> listarContenidosActivosPorUnidad(Long unidadId) {
        // Tu ContenidoRepository ya tiene este método
        return contenidoRepository.findByUnidadIdAndActivoTrue(unidadId);
    }

    /**
     * Obtiene una lista de todo el contenido ELIMINADO (inactivo) para una unidad específica.
     * @param unidadId El ID de la unidad.
     * @return Lista de Contenido inactivo.
     */
    public List<Contenido> listarContenidosEliminadosPorUnidad(Long unidadId) {
        // Tu ContenidoRepository ya tiene este método
        return contenidoRepository.findByUnidadIdAndActivoFalse(unidadId);
    }
}