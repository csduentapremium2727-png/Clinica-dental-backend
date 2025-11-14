package clinica.backend.service;

import clinica.backend.config.JwtUtil;
import clinica.backend.dto.AuthResponseDTO;
import clinica.backend.dto.LoginRequestDTO;
import clinica.backend.dto.RegistroPacienteDTO;
import clinica.backend.model.Paciente;
import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
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

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

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

    // Lógica para registrar un nuevo PACIENTE
    // Esto es "Transaccional": si algo falla, no guarda nada.
    @Transactional
    public Paciente registrarPaciente(RegistroPacienteDTO dto) {
        
        // 1. Validar si el usuario ya existe
        if (usuarioRepository.findByDocumentoIdentidad(dto.getDocumentoIdentidad()).isPresent()) {
            throw new RuntimeException("El documento de identidad ya está registrado.");
        }
        if (pacienteRepository.findByEmail(dto.getEmail()).isPresent()) {
             throw new RuntimeException("El email ya está registrado.");
        }

        // 2. Buscar el ROL "PACIENTE"
        Rol rolPaciente = rolRepository.findByNombreRol("PACIENTE")
                .orElseThrow(() -> new RuntimeException("Error: Rol PACIENTE no encontrado."));

        // 3. Crear y guardar el nuevo Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword())); // ¡Encriptar!
        nuevoUsuario.setRol(rolPaciente);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Crear y guardar el nuevo Paciente
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setUsuario(usuarioGuardado); // Vincular con el usuario
        nuevoPaciente.setNombre(dto.getNombre());
        nuevoPaciente.setApellido(dto.getApellido());
        nuevoPaciente.setEmail(dto.getEmail());
        nuevoPaciente.setTelefono(dto.getTelefono());
        nuevoPaciente.setFechaNacimiento(dto.getFechaNacimiento());
        nuevoPaciente.setGenero(dto.getGenero());
        nuevoPaciente.setDireccion(dto.getDireccion());
        nuevoPaciente.setAlergias(dto.getAlergias());

        return pacienteRepository.save(nuevoPaciente);
    }

    // Lógica para el Login
    public AuthResponseDTO login(LoginRequestDTO dto) {
        // 1. Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getDocumentoIdentidad(),
                        dto.getPassword()
                )
        );

        // 2. Establecer el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Cargar detalles del usuario para generar el token
        final UserDetails userDetails = usuarioSecurityService
                .loadUserByUsername(dto.getDocumentoIdentidad());
        
        // 4. Generar el token
        final String jwt = jwtUtil.generateToken(userDetails);
        
        // 5. Extraer el rol del token y devolver la respuesta
        String rol = jwtUtil.extractRol(jwt);
        return new AuthResponseDTO(jwt, rol);
    }
}

// Nota: Necesitas añadir findByEmail en PacienteRepository
// Abre PacienteRepository.java y añade esta línea:
// Optional<Paciente> findByEmail(String email);