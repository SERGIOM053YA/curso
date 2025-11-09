package gm.proyecto.curso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gm.proyecto.curso.dto.ExamenDTO;
import gm.proyecto.curso.dto.PreguntaDTO;
import gm.proyecto.curso.dto.RespuestaDTO;
import gm.proyecto.curso.modelo.*;
import gm.proyecto.curso.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamenService {

    @Autowired
    private ExamenRepository examenRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RespuestaRepository respuestaRepository;
    @Autowired
    private ResultadoExamenRepository resultadoExamenRepository;

    // Para convertir respuestas (List<String>) a JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public Optional<Examen> crearExamen(Long unidadId, ExamenDTO examenDTO) {
        return unidadRepository.findById(unidadId).map(unidad -> {
            Examen nuevoExamen = new Examen();
            nuevoExamen.setTitulo(examenDTO.titulo());
            nuevoExamen.setUnidad(unidad);
            nuevoExamen.setActivo(true);

            List<Pregunta> listaPreguntas = new ArrayList<>();

            for (PreguntaDTO preguntaDTO : examenDTO.preguntas()) {
                Pregunta pregunta = null;
                Map<String, Object> detalles = preguntaDTO.detalles();

                switch (preguntaDTO.tipo()) {
                    case "ABIERTA":
                        pregunta = new PreguntaAbierta();
                        break;

                    case "VERDADERO_FALSO":
                        PreguntaVerdaderoFalso pvf = new PreguntaVerdaderoFalso();
                        pvf.setRespuestaCorrecta((Boolean) detalles.get("respuestaCorrecta"));
                        pregunta = pvf;
                        break;

                    case "OPCION_MULTIPLE":
                        PreguntaOpcionMultiple pom = new PreguntaOpcionMultiple();
                        pom.setOpciones((List<String>) detalles.get("opciones"));
                        pom.setIndiceRespuestaCorrecta(((Number) detalles.get("indiceRespuestaCorrecta")).intValue());
                        pregunta = pom;
                        break;

                    // Esta te faltaba:
                    case "SELECCION_MULTIPLE":
                        PreguntaSeleccionMultiple psm = new PreguntaSeleccionMultiple();
                        psm.setOpciones((List<String>) detalles.get("opciones"));
                        // Convertir lista de Numbers (Integer, Long, etc.) a List<Integer>
                        List<Integer> indicesInt = ((List<Number>) detalles.get("indicesRespuestasCorrectas"))
                                .stream()
                                .map(Number::intValue)
                                .collect(Collectors.toList());
                        psm.setIndicesRespuestasCorrectas(indicesInt);
                        pregunta = psm;
                        break;
                }

                if (pregunta != null) {
                    pregunta.setEnunciado(preguntaDTO.enunciado());
                    pregunta.setExamen(nuevoExamen);
                    listaPreguntas.add(pregunta);
                }
            }

            nuevoExamen.setPreguntas(listaPreguntas);
            return examenRepository.save(nuevoExamen);
        });
    }

    // --- MÉTODOS FALTANTES ---

    /**
     * Procesa las respuestas de un estudiante, califica automáticamente y guarda el resultado.
     */
    @Transactional
    public Optional<ResultadoExamen> procesarYCalcularExamen(Long examenId, String emailEstudiante, List<RespuestaDTO> respuestas) {
        Optional<Examen> optExamen = examenRepository.findById(examenId);
        Optional<Usuario> optEstudiante = usuarioRepository.findByEmail(emailEstudiante);

        if (optExamen.isEmpty() || optEstudiante.isEmpty()) {
            return Optional.empty(); // No se encontró el examen o el estudiante
        }

        Examen examen = optExamen.get();
        Usuario estudiante = optEstudiante.get();

        // 1. Crear el ResultadoExamen
        ResultadoExamen resultado = new ResultadoExamen();
        resultado.setExamen(examen);
        resultado.setEstudiante(estudiante);
        resultado.setFechaRealizacion(LocalDateTime.now());
        resultado.setPuntajeTotal((double) examen.getPreguntas().size());

        double puntajeObtenido = 0.0;
        boolean calificacionAutomaticaCompleta = true;
        List<RespuestaEstudiante> respuestasAGuardar = new ArrayList<>();

        // Map de preguntas para búsqueda eficiente
        Map<Long, Pregunta> preguntasMap = examen.getPreguntas().stream()
                .collect(Collectors.toMap(Pregunta::getId, p -> p));

        // 2. Procesar cada respuesta
        for (RespuestaDTO resDTO : respuestas) {
            Pregunta pregunta = preguntasMap.get(resDTO.preguntaId());
            if (pregunta == null) continue; // Ignorar respuesta si la pregunta no existe

            RespuestaEstudiante respuestaEst = new RespuestaEstudiante();
            respuestaEst.setEstudiante(estudiante);
            respuestaEst.setPregunta(pregunta);
            respuestaEst.setResultadoExamen(resultado);
            respuestaEst.setCalificadaManualmente(false); // Por defecto

            double puntajePregunta = calificarPregunta(pregunta, resDTO.respuesta());

            if (puntajePregunta == -1.0) { // Requiere calificación manual
                calificacionAutomaticaCompleta = false;
                respuestaEst.setRespuestaTexto((String) resDTO.respuesta());
                respuestaEst.setPuntajeObtenido(null); // Pendiente
            } else { // Calificación automática
                puntajeObtenido += puntajePregunta;
                respuestaEst.setPuntajeObtenido(puntajePregunta);
                // Guardar la respuesta (simple o lista) como JSON
                try {
                    respuestaEst.setRespuestaJson(objectMapper.writeValueAsString(resDTO.respuesta()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            respuestasAGuardar.add(respuestaEst);
        }

        // 3. Guardar estado final del Resultado
        resultado.setPuntajeObtenido(puntajeObtenido);
        resultado.setCalificacionAutomaticaCompleta(calificacionAutomaticaCompleta);
        resultado.setEstado(calificacionAutomaticaCompleta ? "CALIFICADO" : "PENDIENTE");

        // Guardar primero el resultado (para que tenga ID)
        ResultadoExamen resultadoGuardado = resultadoExamenRepository.save(resultado);

        // Asignar el resultado guardado a las respuestas y guardarlas
        respuestasAGuardar.forEach(r -> r.setResultadoExamen(resultadoGuardado));
        respuestaRepository.saveAll(respuestasAGuardar);

        return Optional.of(resultadoGuardado);
    }

    /**
     * Helper para calificar una pregunta automáticamente.
     * Devuelve -1.0 si requiere calificación manual.
     */
    private double calificarPregunta(Pregunta pregunta, Object respuesta) {
        if (pregunta instanceof PreguntaAbierta) {
            return -1.0; // Requiere manual
        }

        try {
            if (pregunta instanceof PreguntaVerdaderoFalso pvf) {
                Boolean respEstudiante = (Boolean) respuesta;
                return pvf.getRespuestaCorrecta().equals(respEstudiante) ? 1.0 : 0.0;
            }

            if (pregunta instanceof PreguntaOpcionMultiple pom) {
                String respEstudiante = (String) respuesta;
                String correcta = pom.getOpciones().get(pom.getIndiceRespuestaCorrecta());
                return correcta.equals(respEstudiante) ? 1.0 : 0.0;
            }

            if (pregunta instanceof PreguntaSeleccionMultiple psm) {
                List<String> respEstudiante = (List<String>) respuesta;
                List<String> correctas = psm.getIndicesRespuestasCorrectas().stream()
                        .map(idx -> psm.getOpciones().get(idx))
                        .collect(Collectors.toList());

                // Compara si ambas listas tienen el mismo tamaño y los mismos elementos
                boolean match = respEstudiante.size() == correctas.size() &&
                        new HashSet<>(respEstudiante).equals(new HashSet<>(correctas));
                return match ? 1.0 : 0.0;
            }

        } catch (Exception e) {
            // Error en casteo o formato de respuesta
            e.printStackTrace();
            return 0.0;
        }

        return 0.0; // Tipo de pregunta desconocido
    }

    /**
     * Obtiene todas las respuestas abiertas de un examen que no han sido calificadas.
     */
    public List<RespuestaEstudiante> getRespuestasAbiertasPendientes(Long examenId) {
        // Busca respuestas de tipo "PreguntaAbierta" (Dtype) para un examenId
        // donde 'calificadaManualmente' sea false.
        return respuestaRepository.findByPregunta_Examen_IdAndCalificadaManualmenteIsFalseAndPregunta_Dtype(
                examenId, "PreguntaAbierta");
    }

    // ... (después de tus otros métodos, como calificarPreguntaAbierta)

    /**
     * Obtiene el resultado de un examen para un estudiante específico.
     * ¡ESTE ES EL MÉTODO PARA EL PUNTO 4!
     *
     * @param examenId El ID del examen.
     * @param emailEstudiante El email (username) del estudiante autenticado.
     * @return Un Optional con el ResultadoExamen si existe.
     */
    public Optional<ResultadoExamen> obtenerResultado(Long examenId, String emailEstudiante) {

        // Buscamos las entidades principales
        Optional<Examen> optExamen = examenRepository.findById(examenId);
        Optional<Usuario> optEstudiante = usuarioRepository.findByEmail(emailEstudiante);

        if (optExamen.isEmpty() || optEstudiante.isEmpty()) {
            // Si el examen o el estudiante no existen, no hay resultado que buscar.
            return Optional.empty();
        }

        // Buscamos el resultado usando el método que ya existe en el repositorio
        return resultadoExamenRepository.findByExamenAndEstudiante(
                optExamen.get(),
                optEstudiante.get()
        );
    }

    // ... (después de tus otros métodos)

    /**
     * Actualiza un examen existente, reemplazando sus preguntas.
     * ¡ESTA ES LA LÓGICA PARA EDITAR EXÁMENES!
     */
    @Transactional
    public Optional<Examen> actualizarExamen(Long examenId, ExamenDTO examenDTO) {

        // 1. Busca el examen existente
        return examenRepository.findById(examenId).map(examen -> {

            // 2. Actualiza el título
            examen.setTitulo(examenDTO.titulo());

            // 3. ¡Borra las preguntas antiguas!
            //    (Gracias a "orphanRemoval=true" en tu entidad Examen,
            //     esto las eliminará de la base de datos)
            //      examen.getPreguntas().clear();

            // 4. Crear y añadir las nuevas preguntas (lógica copiada de crearExamen)
            List<Pregunta> nuevasPreguntas = new ArrayList<>();
            for (PreguntaDTO preguntaDTO : examenDTO.preguntas()) {
                Pregunta pregunta = null;
                Map<String, Object> detalles = preguntaDTO.detalles();

                switch (preguntaDTO.tipo()) {
                    case "ABIERTA":
                        pregunta = new PreguntaAbierta();
                        break;

                    case "VERDADERO_FALSO":
                        PreguntaVerdaderoFalso pvf = new PreguntaVerdaderoFalso();
                        pvf.setRespuestaCorrecta((Boolean) detalles.get("respuestaCorrecta"));
                        pregunta = pvf;
                        break;

                    case "OPCION_MULTIPLE":
                        PreguntaOpcionMultiple pom = new PreguntaOpcionMultiple();
                        pom.setOpciones((List<String>) detalles.get("opciones"));
                        pom.setIndiceRespuestaCorrecta(((Number) detalles.get("indiceRespuestaCorrecta")).intValue());
                        pregunta = pom;
                        break;

                    case "SELECCION_MULTIPLE":
                        PreguntaSeleccionMultiple psm = new PreguntaSeleccionMultiple();
                        psm.setOpciones((List<String>) detalles.get("opciones"));
                        // Convertir la lista de números (que puede venir como Long o Integer de JSON)
                        List<Integer> indicesInt = ((List<Number>) detalles.get("indicesRespuestasCorrectas"))
                                .stream()
                                .map(Number::intValue)
                                .collect(Collectors.toList());
                        psm.setIndicesRespuestasCorrectas(indicesInt);
                        pregunta = psm;
                        break;
                }

                if (pregunta != null) {
                    pregunta.setEnunciado(preguntaDTO.enunciado());
                    pregunta.setExamen(examen); // ¡Importante! Asignar al examen padre
                    nuevasPreguntas.add(pregunta);
                }
            }

            // 5. Añadir la nueva lista al examen
            examen.getPreguntas().addAll(nuevasPreguntas);

            // 6. Guardar los cambios
            return examenRepository.save(examen);
        });
    }
    /**
     * Asigna un puntaje a una pregunta abierta y actualiza el resultado total.
     */
    @Transactional
    public Optional<RespuestaEstudiante> calificarPreguntaAbierta(Long respuestaId, Double puntaje) {
        Optional<RespuestaEstudiante> optRespuesta = respuestaRepository.findById(respuestaId);
        if (optRespuesta.isEmpty()) {
            return Optional.empty();
        }

        RespuestaEstudiante respuesta = optRespuesta.get();
        // Evitar doble calificación si ya se hizo (o permitir recalificar)
        if (respuesta.isCalificadaManualmente()) {
            // (Opcional: manejar lógica de recalificación si se desea)
            System.out.println("Advertencia: Recalificando respuesta " + respuestaId);
        }

        // 1. Actualizar la respuesta individual
        Double puntajeAnterior = respuesta.getPuntajeObtenido() != null ? respuesta.getPuntajeObtenido() : 0.0;
        respuesta.setPuntajeObtenido(puntaje);
        respuesta.setCalificadaManualmente(true);
        RespuestaEstudiante respuestaGuardada = respuestaRepository.save(respuesta);

        // 2. Actualizar el ResultadoExamen general
        ResultadoExamen resultado = respuesta.getResultadoExamen();
        double nuevoPuntajeTotal = resultado.getPuntajeObtenido() - puntajeAnterior + puntaje;
        resultado.setPuntajeObtenido(nuevoPuntajeTotal);

        // 3. Verificar si ya se terminaron de calificar todas las preguntas
        List<RespuestaEstudiante> pendientes = respuestaRepository
                .findByResultadoExamen_IdAndCalificadaManualmenteIsFalseAndPregunta_Dtype(
                        resultado.getId(), "PreguntaAbierta");

        if (pendientes.isEmpty()) {
            resultado.setEstado("CALIFICADO");
        }

        resultadoExamenRepository.save(resultado);

        return Optional.of(respuestaGuardada);
    }
}


