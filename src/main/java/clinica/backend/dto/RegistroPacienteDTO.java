package clinica.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistroPacienteDTO {
    // Datos del Usuario
    private String documentoIdentidad;
    private String password;
    
    // Datos del Paciente
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private String alergias;
}