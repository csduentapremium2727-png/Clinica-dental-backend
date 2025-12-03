package clinica.backend.model;

// 1. IMPORTA ESTO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "paciente_id", referencedColumnName = "id", nullable = false)
    // 2. AÑADE ESTA LÍNEA
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", referencedColumnName = "id", nullable = false)
    // 3. AÑADE ESTA LÍNEA
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Odontologo odontologo;

    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @Column(name = "hora_cita", nullable = false)
    private LocalTime horaCita;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 50)
    private String estado; 
}