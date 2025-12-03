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

    // Listar todos
    public List<Odontologo> listarOdontologos() {
        return odontologoRepository.findAll();
    }

    // Obtener uno por ID (¡Este era el que faltaba!)
    public Optional<Odontologo> obtenerOdontologo(Long id) {
        return odontologoRepository.findById(id);
    }

    // Guardar con creación automática de Usuario
    @Transactional
    public Odontologo guardarOdontologo(Odontologo odontologo) {
        if (odontologo.getUsuario() == null) {
            Usuario nuevoUsuario = new Usuario();
            
            // Usamos el teléfono como login (fallback a ID temporal si no hay)
            String documentoLogin = odontologo.getTelefono(); 
            if (documentoLogin == null || documentoLogin.isEmpty()) {
                documentoLogin = "DOC" + System.currentTimeMillis(); 
            }
            // Cortar a 20 chars para evitar error de BD
            if (documentoLogin.length() > 20) {
                documentoLogin = documentoLogin.substring(0, 20);
            }

            nuevoUsuario.setDocumentoIdentidad(documentoLogin);
            nuevoUsuario.setPassword(passwordEncoder.encode("123456")); // Pass default

            // Asignar Rol
            Rol rolOdontologo = rolRepository.findByNombreRol("ODONTOLOGO")
                    .orElseGet(() -> rolRepository.save(new Rol(null, "ODONTOLOGO")));
            nuevoUsuario.setRol(rolOdontologo);

            // Guardar Usuario y asignarlo
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);
            odontologo.setUsuario(nuevoUsuario);
        }
        return odontologoRepository.save(odontologo);
    }

    // Actualizar
    public Odontologo actualizarOdontologo(Odontologo odontologo) {
        // Verifica si existe, si no, podrías lanzar excepción, 
        // pero save() también funciona como "upsert" si el ID viene cargado.
        return odontologoRepository.save(odontologo);
    }

    // Eliminar (con limpieza opcional de usuario)
    @Transactional
    public void eliminarOdontologo(Long id) {
        odontologoRepository.findById(id).ifPresent(o -> {
            Usuario u = o.getUsuario();
            odontologoRepository.deleteById(id);
            // Opcional: Borrar el usuario asociado para no dejar basura
            if (u != null) {
                usuarioRepository.delete(u);
            }
        });
    }
}