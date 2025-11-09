package gm.proyecto.curso.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.DiscriminatorValue; // <-- ¡AÑADIR IMPORT!
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tareas")
@DiscriminatorValue("TAREA") // <-- ¡AÑADE ESTA LÍNEA!
@EqualsAndHashCode(callSuper = true)
public class Tarea extends Contenido {

    private String instrucciones;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaEntrega;
    private boolean entregada = false;
    private String estadoEntrega;
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Entrega> entregas;


    // --- Getters y Setters MANUALES ---

    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public boolean isEntregada() { return entregada; }
    public void setEntregada(boolean entregada) { this.entregada = entregada; }
    public String getEstadoEntrega() { return estadoEntrega; }
    public void setEstadoEntrega(String estadoEntrega) { this.estadoEntrega = estadoEntrega; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public List<Entrega> getEntregas() { return entregas; }
    public void setEntregas(List<Entrega> entregas) { this.entregas = entregas; }
}
