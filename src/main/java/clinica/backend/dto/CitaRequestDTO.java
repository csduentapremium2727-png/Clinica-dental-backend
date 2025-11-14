package clinica.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class CitaRequestDTO {
    private Long pacienteId;
    private Long odontologoId;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String motivo;
}