package clinica.backend.service;

import clinica.backend.model.Cita;
import clinica.backend.model.Facturacion;
import clinica.backend.repository.CitaRepository;
import clinica.backend.repository.FacturacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FacturacionService {

    @Autowired
    private FacturacionRepository facturacionRepository;

    @Autowired
    private CitaRepository citaRepository; // Inyectamos aquí para usarlo en la lógica

    public List<Facturacion> listarFacturas() {
        return facturacionRepository.findAll();
    }

    public List<Facturacion> listarFacturasPendientesPorPaciente(Long pacienteId) {
        // Asumiendo que este método existe en tu repositorio según tu código anterior
        return facturacionRepository.findByPacienteIdAndEstadoPago(pacienteId, "Pendiente");
    }

    public Optional<Facturacion> obtenerFactura(Long id) {
        return facturacionRepository.findById(id);
    }

    public boolean existeFactura(Long id) {
        return facturacionRepository.existsById(id);
    }

    public void eliminarFactura(Long id) {
        facturacionRepository.deleteById(id);
    }

    // --- LÓGICA DE NEGOCIO CENTRALIZADA ---
    @Transactional // Asegura la integridad de la transacción
    public Facturacion crearFacturaDesdeDatos(Map<String, Object> datos) {
        // 1. Validar ID de Cita
        if (datos.get("citaId") == null) {
            throw new IllegalArgumentException("El campo 'citaId' es obligatorio y llegó nulo.");
        }

        String citaIdStr = String.valueOf(datos.get("citaId"));
        Long citaId;
        try {
            citaId = Long.valueOf(citaIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El 'citaId' debe ser un número válido. Recibido: " + citaIdStr);
        }

        // 2. Buscar la Cita (Lanza excepción si no existe)
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna Cita con el ID: " + citaId));

        // 3. Validar Monto
        if (datos.get("montoTotal") == null) {
            throw new IllegalArgumentException("El campo 'montoTotal' es obligatorio.");
        }
        BigDecimal monto = new BigDecimal(String.valueOf(datos.get("montoTotal")));

        // 4. Construir Objeto Factura
        Facturacion nuevaFactura = new Facturacion();
        nuevaFactura.setCita(cita);
        nuevaFactura.setPaciente(cita.getPaciente());
        nuevaFactura.setMontoTotal(monto);

        // Manejo seguro de campos opcionales
        String estado = datos.get("estadoPago") != null ? datos.get("estadoPago").toString() : "Pendiente";
        String tipo = datos.get("tipoComprobante") != null ? datos.get("tipoComprobante").toString() : "Boleta";

        nuevaFactura.setEstadoPago(estado);
        nuevaFactura.setTipoComprobante(tipo);

        // 5. Guardar y Retornar
        return facturacionRepository.save(nuevaFactura);
    }
}