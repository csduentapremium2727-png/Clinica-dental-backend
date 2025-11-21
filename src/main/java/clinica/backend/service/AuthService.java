package clinica.backend.service;

import clinica.backend.config.JwtUtil;
import clinica.backend.dto.AuthResponseDTO;
import clinica.backend.dto.LoginRequestDTO;
import clinica.backend.dto.RegistroOdontologoDTO;
import clinica.backend.dto.RegistroPacienteDTO;
import clinica.backend.model.Odontologo;
import clinica.backend.model.Paciente;
import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
import clinica.backend.repository.OdontologoRepository;
import clinica.backend.repository.PacienteRepository;
import clinica.backend.repository.RolRepository;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Imports para el Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    // --- INICIO DE MODIFICACIÓN ---
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private EmailService emailService; // 1. INYECTA EL SERVICIO DE CORREO
    // --- FIN DE MODIFICACIÓN ---

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioSecurityService usuarioSecurityService;

    @Transactional
    public Paciente registrarPaciente(RegistroPacienteDTO dto) {
        
        if (usuarioRepository.findByDocumentoIdentidad(dto.getDocumentoIdentidad()).isPresent()) {
            throw new RuntimeException("El documento de identidad ya está registrado.");
        }
        if (pacienteRepository.findByEmail(dto.getEmail()).isPresent()) {
             throw new RuntimeException("El email ya está registrado.");
        }

        Rol rolPaciente = rolRepository.findByNombreRol("PACIENTE")
                .orElseThrow(() -> new RuntimeException("Error: Rol PACIENTE no encontrado."));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setRol(rolPaciente);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setUsuario(usuarioGuardado);
        nuevoPaciente.setNombre(dto.getNombre());
        nuevoPaciente.setApellido(dto.getApellido());
        nuevoPaciente.setEmail(dto.getEmail());
        nuevoPaciente.setTelefono(dto.getTelefono());
        nuevoPaciente.setFechaNacimiento(dto.getFechaNacimiento());
        nuevoPaciente.setGenero(dto.getGenero());
        nuevoPaciente.setDireccion(dto.getDireccion());
        nuevoPaciente.setAlergias(dto.getAlergias());
        
        Paciente pacienteGuardado = pacienteRepository.save(nuevoPaciente);

        // --- INICIO DE MODIFICACIÓN ---
        // 2. LLAMA AL SERVICIO DE CORREO
        try {
            String subject = "¡Bienvenido a la Clínica Sonrisa Plena!";
            String content = "<h1>Hola, " + dto.getNombre() + "!</h1>"
                           + "<p>Tu registro ha sido exitoso. Tu usuario es: <b>" + dto.getDocumentoIdentidad() + "</b></p>"
                           + "<p>Gracias por confiar en nosotros.</p>";
            emailService.sendHtmlEmail(dto.getEmail(), subject, content);
        } catch (Exception e) {
            // Si el correo falla, solo lo registramos en el log pero no detenemos la operación
            logger.warn("El usuario " + dto.getDocumentoIdentidad() + " se registró, pero el email de bienvenida falló: " + e.getMessage());
        }
        // --- FIN DE MODIFICACIÓN ---

        return pacienteGuardado;
    }
    
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getDocumentoIdentidad(),
                        dto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = usuarioSecurityService
                .loadUserByUsername(dto.getDocumentoIdentidad());
        
        final String jwt = jwtUtil.generateToken(userDetails);
        
        String rol = jwtUtil.extractRol(jwt);
        return new AuthResponseDTO(jwt, rol);
    }

    @Transactional
    public Odontologo registrarOdontologo(RegistroOdontologoDTO dto) {
        if (usuarioRepository.findByDocumentoIdentidad(dto.getDocumentoIdentidad()).isPresent()) {
            throw new RuntimeException("El documento de identidad ya está registrado.");
        }
        if (odontologoRepository.findByEmail(dto.getEmail()).isPresent()) {
             throw new RuntimeException("El email ya está registrado.");
        }

        Rol odontologoRol = rolRepository.findByNombreRol("ODONTOLOGO")
                .orElseThrow(() -> new RuntimeException("Error: ROL_ODONTOLOGO no encontrado."));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setRol(odontologoRol);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Odontologo nuevoOdontologo = new Odontologo();
        nuevoOdontologo.setUsuario(usuarioGuardado);
        nuevoOdontologo.setNombre(dto.getNombre());
        nuevoOdontologo.setApellido(dto.getApellido());
        nuevoOdontologo.setEmail(dto.getEmail());
        nuevoOdontologo.setTelefono(dto.getTelefono());
        nuevoOdontologo.setEspecialidad(dto.getEspecialidad());

        return odontologoRepository.save(nuevoOdontologo);
    }
}