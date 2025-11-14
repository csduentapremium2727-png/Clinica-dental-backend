package clinica.backend.service;

import clinica.backend.dto.CitaRequestDTO; // Importar el DTO
import clinica.backend.model.Cita;
import clinica.backend.model.Odontologo;
import clinica.backend.model.Paciente;
import clinica.backend.repository.CitaRepository;
import clinica.backend.repository.OdontologoRepository;
import clinica.backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> obtenerCita(Long id) {
        return citaRepository.findById(id);
    }

    // --- ¡MÉTODO MODIFICADO! ---
    // Ahora recibe el DTO y la lógica está 100% en el servicio.
    public Cita agendarCita(CitaRequestDTO request) {
        
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Odontologo odontologo = odontologoRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Creamos la nueva entidad Cita
        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setOdontologo(odontologo);
        nuevaCita.setFechaCita(request.getFechaCita());
        nuevaCita.setHoraCita(request.getHoraCita());
        nuevaCita.setMotivo(request.getMotivo());
        nuevaCita.setEstado("Programada"); // Estado por defecto

        return citaRepository.save(nuevaCita);
    }

    // Métodos de búsqueda que definimos en el repositorio
    public List<Cita> citasPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public List<Cita> citasPorOdontologo(Long odontologoId) {
        return citaRepository.findByOdontologoId(odontologoId);
    }
    
    public void eliminarCita(Long id) {
        citaRepository.deleteById(id);
    }
}