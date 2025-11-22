package clinica.backend.controller;

import clinica.backend.model.Facturacion;
import clinica.backend.service.FacturacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin // Importante para que Angular pueda acceder sin problemas de CORS local
public class FacturaController {

    @Autowired
    private FacturacionService facturacionService;

    @GetMapping
    public List<Facturacion> listarFacturas() {
        return facturacionService.listarFacturas();
    }

    @GetMapping("/pendientes/paciente/{pacienteId}")
    public ResponseEntity<List<Facturacion>> listarFacturasPendientes(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(facturacionService.listarFacturasPendientesPorPaciente(pacienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facturacion> obtenerFactura(@PathVariable Long id) {
        return facturacionService.obtenerFactura(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody Map<String, Object> datos) {
        try {
            // Delegamos toda la lógica al servicio
            Facturacion facturaGuardada = facturacionService.crearFacturaDesdeDatos(datos);
            return new ResponseEntity<>(facturaGuardada, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Errores de validación (datos faltantes o mal formados) -> 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (RuntimeException e) {
            // Errores de negocio (ej. cita no encontrada) -> 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            // Errores inesperados -> 500 Internal Server Error
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        if (!facturacionService.existeFactura(id)) {
            return ResponseEntity.notFound().build();
        }
        facturacionService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}