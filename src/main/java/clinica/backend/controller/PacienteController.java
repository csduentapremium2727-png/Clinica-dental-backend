package clinica.backend.controller;

import clinica.backend.model.Paciente;
import clinica.backend.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes") // Ruta base para todos los endpoints de pacientes
@CrossOrigin // Permite peticiones desde Angular (localhost:4200)
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // GET /api/pacientes (Listar todos)
    @GetMapping
    public List<Paciente> listarPacientes() {
        // Sin lógica, solo llama al servicio
        return pacienteService.listarPacientes();
    }

    // GET /api/pacientes/{id} (Obtener uno)
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPaciente(@PathVariable Long id) {
        return pacienteService.obtenerPaciente(id)
                .map(paciente -> ResponseEntity.ok(paciente)) // 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // POST /api/pacientes (Crear uno)
    @PostMapping
    public ResponseEntity<Paciente> guardarPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);
        return new ResponseEntity<>(pacienteGuardado, HttpStatus.CREATED); // 201 Created
    }

    // PUT /api/pacientes/{id} (Actualizar uno)
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        // Aseguramos que el ID del path coincida con el del body
        paciente.setId(id); 
        Paciente pacienteActualizado = pacienteService.actualizarPaciente(paciente);
        return ResponseEntity.ok(pacienteActualizado); // 200 OK
    }

    // DELETE /api/pacientes/{id} (Eliminar uno)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}