package clinica.backend.service;

import clinica.backend.model.Paciente;
import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
import clinica.backend.repository.PacienteRepository;
import clinica.backend.repository.RolRepository;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> obtenerPaciente(Long id) {
        return pacienteRepository.findById(id);
    }

    @Transactional
    public Paciente guardarPaciente(Paciente paciente) {
        
        // Si viene un usuario adjunto (desde el formulario)
        if (paciente.getUsuario() != null) {
            Usuario u = paciente.getUsuario();
            
            // --- SINCRONIZACIÓN DE DATOS ---
            // Copiamos los datos maestros del paciente al usuario
            u.setNombre(paciente.getNombre());
            u.setApellido(paciente.getApellido());
            u.setEmail(paciente.getEmail());
            u.setTelefono(paciente.getTelefono());
            // -------------------------------

            // Si es nuevo (id null) y no tiene password, ponemos default
            if (u.getId() == null && (u.getPassword() == null || u.getPassword().isEmpty())) {
                 u.setPassword(passwordEncoder.encode(u.getDocumentoIdentidad())); // DNI como pass inicial
            }

            // Asegurar Rol
            if (u.getRol() == null) {
                Rol rolPaciente = rolRepository.findByNombreRol("PACIENTE")
                    .orElseGet(() -> rolRepository.save(new Rol(null, "PACIENTE")));
                u.setRol(rolPaciente);
            }
            
            // Guardamos usuario primero si es necesario (aunque Cascade lo hace, esto asegura actualización)
            u = usuarioRepository.save(u);
            paciente.setUsuario(u);
        }
        
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente actualizarPaciente(Paciente paciente) {
        if (!pacienteRepository.existsById(paciente.getId())) {
            throw new RuntimeException("Paciente no encontrado");
        }
        // Reutiliza lógica de sincronización
        return guardarPaciente(paciente);
    }

    @Transactional
    public void eliminarPaciente(Long id) {
        pacienteRepository.findById(id).ifPresent(p -> {
            Usuario u = p.getUsuario();
            pacienteRepository.deleteById(id);
            if (u != null) {
                usuarioRepository.delete(u);
            }
        });
    }
}