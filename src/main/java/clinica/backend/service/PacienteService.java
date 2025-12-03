package clinica.backend.service;

import clinica.backend.model.Paciente;
import clinica.backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Obtener todos los pacientes
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    // Obtener un paciente por ID
    public Optional<Paciente> obtenerPaciente(Long id) {
        return pacienteRepository.findById(id);
    }

    // Crear un nuevo paciente
    // Nota: Más adelante, aquí recibiremos un DTO y también crearemos el Usuario.
    public Paciente guardarPaciente(Paciente paciente) {
        // Aquí iría la lógica de validación (ej. verificar si el email ya existe)
        return pacienteRepository.save(paciente);
    }

    // Actualizar un paciente
    public Paciente actualizarPaciente(Paciente paciente) {
        // Validar que el paciente exista antes de actualizar
        if (!pacienteRepository.existsById(paciente.getId())) {
            // Aquí deberíamos lanzar una excepción personalizada
            throw new RuntimeException("Paciente no encontrado");
        }
        return pacienteRepository.save(paciente);
    }

    // Eliminar un paciente
    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

    public Optional<Paciente> buscarPorUsuarioId(Long usuarioId) {
        return pacienteRepository.findByUsuarioId(usuarioId);
    }    
}