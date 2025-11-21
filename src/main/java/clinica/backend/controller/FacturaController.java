package clinica.backend.controller;

import clinica.backend.model.Cita;
import clinica.backend.model.Facturacion;
import clinica.backend.repository.CitaRepository;
import clinica.backend.repository.FacturacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin
public class FacturaController {

    @Autowired
    private FacturacionRepository facturaRepository;
    
    @Autowired
    private CitaRepository citaRepository;

    @GetMapping
    public List<Facturacion> listarFacturas() {
        return facturaRepository.findAll();
    }

    @GetMapping("/pendientes/paciente/{pacienteId}")
    public ResponseEntity<List<Facturacion>> listarFacturasPendientes(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(facturaRepository.findByPacienteIdAndEstadoPago(pacienteId, "Pendiente")); 
    }

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody Map<String, Object> datos) {
        System.out.println(">>> Recibiendo datos para factura: " + datos); // LOG PARA DEBUG

        try {
            // 1. Validar ID de Cita
            if (datos.get("citaId") == null) {
                return ResponseEntity.badRequest().body("El campo 'citaId' es obligatorio y llegó nulo.");
            }
            
            // Convertir de forma segura (maneja String o Number del JSON)
            String citaIdStr = String.valueOf(datos.get("citaId"));
            Long citaId;
            try {
                 citaId = Long.valueOf(citaIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("El 'citaId' debe ser un número válido. Recibido: " + citaIdStr);
            }

            // 2. Buscar la Cita
            Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna Cita con el ID: " + citaId));

            // 3. Validar Monto
            if (datos.get("montoTotal") == null) {
                return ResponseEntity.badRequest().body("El campo 'montoTotal' es obligatorio.");
            }
            BigDecimal monto = new BigDecimal(String.valueOf(datos.get("montoTotal")));

            // 4. Crear Factura
            Facturacion nuevaFactura = new Facturacion();
            nuevaFactura.setCita(cita);
            nuevaFactura.setPaciente(cita.getPaciente());
            nuevaFactura.setMontoTotal(monto);
            
            // Manejo seguro de strings
            String estado = datos.get("estadoPago") != null ? datos.get("estadoPago").toString() : "Pendiente";
            String tipo = datos.get("tipoComprobante") != null ? datos.get("tipoComprobante").toString() : "Boleta";
            
            nuevaFactura.setEstadoPago(estado);
            nuevaFactura.setTipoComprobante(tipo);

            Facturacion facturaGuardada = facturaRepository.save(nuevaFactura);
            
            System.out.println(">>> Factura creada con ID: " + facturaGuardada.getId());
            return new ResponseEntity<>(facturaGuardada, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            // Error de lógica (ej: cita no encontrada) -> 400 o 404
            System.err.println("Error lógico creando factura: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Error inesperado -> 500
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }
}