package gm.proyecto.curso.dto;

public class LoginResponse {
    // 1. AÑADE EL CAMPO PARA EL ID
    private Long id;
    private String token;
    private String nombreCompleto;
    private String rol;

    // 2. ACTUALIZA EL CONSTRUCTOR PARA QUE ACEPTE 4 ARGUMENTOS
    public LoginResponse(Long id, String token, String nombreCompleto, String rol) {
        this.id = id;
        this.token = token;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    // 3. AÑADE EL GETTER Y SETTER PARA EL ID
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Getters y Setters existentes...
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}