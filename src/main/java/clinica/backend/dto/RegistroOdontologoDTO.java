package clinica.backend.dto;

import lombok.Data;

@Data
public class RegistroOdontologoDTO {
    // Datos del Usuario
    private String documentoIdentidad;
    private String password;
    
    // Datos del Odontologo
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String especialidad;
}