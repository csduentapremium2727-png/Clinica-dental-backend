package clinica.backend.repository;

import clinica.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
}