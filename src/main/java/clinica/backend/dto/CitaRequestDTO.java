package clinica.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaRequestDTO {
    private Long pacienteId;   // Debe llamarse así
    private Long odontologoId; // Debe llamarse así
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String motivo;
}