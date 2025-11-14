package clinica.backend.controller;

import clinica.backend.dto.CitaRequestDTO;
import clinica.backend.model.Cita;
import clinica.backend.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin
public class CitaController {

    @Autowired
    private CitaService citaService;

    // GET /api/citas (Listar todas)
    @GetMapping
    public List<Cita> listarCitas() {
        return citaService.listarCitas();
    }

    // POST /api/citas (Agendar nueva cita)
    @PostMapping
    public ResponseEntity<Cita> agendarCita(@RequestBody CitaRequestDTO request) {
        // ¡Limpio! Solo pasamos el DTO al servicio.
        Cita nuevaCita = citaService.agendarCita(request);
        return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
    }

    // GET /api/citas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerCita(@PathVariable Long id) {
        return citaService.obtenerCita(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/citas/paciente/{id} (Buscar por paciente)
    @GetMapping("/paciente/{pacienteId}")
    public List<Cita> obtenerCitasPorPaciente(@PathVariable Long pacienteId) {
        return citaService.citasPorPaciente(pacienteId);
    }

    // GET /api/citas/odontologo/{id} (Buscar por odontólogo)
    @GetMapping("/odontologo/{odontologoId}")
    public List<Cita> obtenerCitasPorOdontologo(@PathVariable Long odontologoId) {
        return citaService.citasPorOdontologo(odontologoId);
    }

    // DELETE /api/citas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
        return ResponseEntity.noContent().build();
    }
}