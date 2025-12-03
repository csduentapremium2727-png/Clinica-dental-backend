package clinica.backend.repository;

import clinica.backend.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteId(Long pacienteId);
    List<Cita> findByOdontologoId(Long odontologoId);
    List<Cita> findByFechaCitaBetween(LocalDate inicio, LocalDate fin);
}

