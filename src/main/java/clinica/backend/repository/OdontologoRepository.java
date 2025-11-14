package clinica.backend.repository;

import clinica.backend.model.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    // Aquí puedes añadir métodos de búsqueda personalizados si los necesitas
}