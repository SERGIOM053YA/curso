package gm.proyecto.curso.service;

import gm.proyecto.curso.modelo.Curso;
import gm.proyecto.curso.modelo.Unidad;
import gm.proyecto.curso.repository.CursoRepository;
import gm.proyecto.curso.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadService {

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public Unidad crearUnidad(Long cursoId, Unidad unidad) {
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);
        if (cursoOpt.isPresent()) {
            unidad.setCurso(cursoOpt.get());
            unidad.setActivo(true); // Asegurarse de que sea activa al crearse
            return unidadRepository.save(unidad);
        }
        return null; // O podrías lanzar una excepción aquí
    }

    public Optional<Unidad> actualizarUnidad(Long unidadId, Unidad datosNuevos) {
        return unidadRepository.findById(unidadId).map(unidadExistente -> {
            unidadExistente.setTitulo(datosNuevos.getTitulo());
            return unidadRepository.save(unidadExistente);
        });
    }

    public Optional<Unidad> softDeleteUnidad(Long id) {
        return unidadRepository.findById(id).map(unidad -> {
            unidad.setActivo(false);
            return unidadRepository.save(unidad);
        });
    }
    // --- ¡MÉTODO AÑADIDO QUE FALTABA! ---
    public List<Unidad> obtenerUnidadesActivasPorCurso(Long cursoId) {
        // Llama al método que ya existe en tu UnidadRepository
        return unidadRepository.findByCursoIdAndActivoTrue(cursoId);
    }

    // --- ¡MÉTODO AÑADIDO QUE FALTABA! ---
    public List<Unidad> obtenerUnidadesEliminadasPorCurso(Long cursoId) {
        // Llama al método que ya existe en tu UnidadRepository
        return unidadRepository.findByCursoIdAndActivoFalse(cursoId);
    }


    public Optional<Unidad> restaurarUnidad(Long id) {
        return unidadRepository.findById(id).map(unidad -> {
            unidad.setActivo(true);
            return unidadRepository.save(unidad);
        });
    }
}