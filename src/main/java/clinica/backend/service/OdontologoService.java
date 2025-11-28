package clinica.backend.service;

import clinica.backend.model.Odontologo;
import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
import clinica.backend.repository.OdontologoRepository;
import clinica.backend.repository.RolRepository;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Odontologo> listarOdontologos() {
        return odontologoRepository.findAll();
    }

    public Optional<Odontologo> obtenerOdontologo(Long id) {
        return odontologoRepository.findById(id);
    }

    @Transactional
    public Odontologo guardarOdontologo(Odontologo odontologo) {
        // Si no tiene usuario asignado (es nuevo registro desde panel), lo creamos
        if (odontologo.getUsuario() == null) {
            Usuario nuevoUsuario = new Usuario();
            
            // Generamos un usuario/login basado en el documento/teléfono o un default
            String documentoLogin = odontologo.getTelefono(); 
            if (documentoLogin == null || documentoLogin.isEmpty()) {
                // Fallback si no hay teléfono
                documentoLogin = "DOC-" + System.currentTimeMillis(); 
            }
            // Seguridad: limitamos largo
            if (documentoLogin.length() > 20) documentoLogin = documentoLogin.substring(0, 20);

            nuevoUsuario.setDocumentoIdentidad(documentoLogin);
            nuevoUsuario.setPassword(passwordEncoder.encode("123456")); // Password por defecto

            // --- CORRECCIÓN IMPORTANTE: COPIAR DATOS AL USUARIO ---
            // Esto es lo que faltaba para que se vea en la tabla de Usuarios
            nuevoUsuario.setNombre(odontologo.getNombre());
            nuevoUsuario.setApellido(odontologo.getApellido());
            nuevoUsuario.setEmail(odontologo.getEmail());
            nuevoUsuario.setTelefono(odontologo.getTelefono());
            // -----------------------------------------------------

            // Asignar Rol
            Rol rolOdontologo = rolRepository.findByNombreRol("ODONTOLOGO")
                    .orElseGet(() -> rolRepository.save(new Rol(null, "ODONTOLOGO")));
            nuevoUsuario.setRol(rolOdontologo);

            // Guardar Usuario
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);
            odontologo.setUsuario(nuevoUsuario);
        } 
        // Caso: Edición donde el usuario ya existe, actualizamos sus datos también
        else if (odontologo.getUsuario() != null) {
            Usuario u = odontologo.getUsuario();
            u.setNombre(odontologo.getNombre());
            u.setApellido(odontologo.getApellido());
            u.setEmail(odontologo.getEmail());
            u.setTelefono(odontologo.getTelefono());
            // No guardamos u explícitamente porque CascadeType.PERSIST/MERGE lo manejaría, 
            // o Hibernate dirty checking, pero para asegurar:
            usuarioRepository.save(u);
        }
        
        return odontologoRepository.save(odontologo);
    }

    public Odontologo actualizarOdontologo(Odontologo odontologo) {
        // Reutilizamos la lógica de guardar que ahora incluye la sincronización de datos
        return guardarOdontologo(odontologo);
    }

    @Transactional
    public void eliminarOdontologo(Long id) {
        odontologoRepository.findById(id).ifPresent(o -> {
            Usuario u = o.getUsuario();
            odontologoRepository.deleteById(id);
            if (u != null) {
                usuarioRepository.delete(u);
            }
        });
    }
}