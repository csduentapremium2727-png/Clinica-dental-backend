package clinica.backend.repository;

import clinica.backend.model.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
    // Busca por la propiedad 'estadoPago'
    List<Facturacion> findByPacienteIdAndEstadoPago(Long pacienteId, String estadoPago);
}