package clinica.backend.repository;

import clinica.backend.model.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    Optional<Odontologo> findByEmail(String email);
}