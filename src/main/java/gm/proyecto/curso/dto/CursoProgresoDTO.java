package gm.proyecto.curso.dto;

import gm.proyecto.curso.modelo.Curso;

public class CursoProgresoDTO {

    private Curso curso;
    private double progreso; // Progreso como porcentaje (0.0 a 100.0)

    // Constructor vacío (necesario para algunas librerías)
    public CursoProgresoDTO() {
    }

    // Constructor principal
    public CursoProgresoDTO(Curso curso, double progreso) {
        this.curso = curso;
        this.progreso = progreso;
    }

    // Getters
    public Curso getCurso() {
        return curso;
    }

    public double getProgreso() {
        return progreso;
    }

    // Setters
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setProgreso(double progreso) {
        this.progreso = progreso;
    }
}
