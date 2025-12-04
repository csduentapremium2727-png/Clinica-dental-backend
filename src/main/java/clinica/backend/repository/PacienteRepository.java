package clinica.backend.repository;

import clinica.backend.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Aquí puedes añadir métodos de búsqueda personalizados si los necesitas
    Optional<Paciente> findByEmail(String email);
    Optional<Paciente> findByUsuarioId(Long usuarioId);

    // ************ CORRECCIÓN AÑADIDA ************
    // Método para buscar Paciente por el campo documentoIdentidad dentro de su entidad Usuario
    Optional<Paciente> findByUsuarioDocumentoIdentidad(String documentoIdentidad);
    // **********************************************
}