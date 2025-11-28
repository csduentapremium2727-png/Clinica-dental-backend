package clinica.backend.controller;

import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
import clinica.backend.repository.RolRepository;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerPerfil() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByDocumentoIdentidad(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- NUEVOS MÉTODOS PARA GESTIÓN (Crear y Editar) ---

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> datos) {
        try {
            Usuario usuario = new Usuario();
            usuario.setDocumentoIdentidad((String) datos.get("documentoIdentidad"));
            usuario.setNombre((String) datos.get("nombres")); // Mapeo desde el frontend 'nombres'
            usuario.setApellido((String) datos.get("apellidos"));
            usuario.setEmail((String) datos.get("email"));
            
            String rawPassword = (String) datos.get("password");
            usuario.setPassword(passwordEncoder.encode(rawPassword));

            // Buscar el rol enviado (ej. "RECEPCIONISTA")
            String nombreRol = (String) datos.get("rol");
            Rol rol = rolRepository.findByNombreRol(nombreRol)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));
            usuario.setRol(rol);

            return new ResponseEntity<>(usuarioRepository.save(usuario), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre((String) datos.get("nombres"));
            usuario.setApellido((String) datos.get("apellidos"));
            usuario.setEmail((String) datos.get("email"));
            
            // Solo actualizamos password si viene uno nuevo
            if (datos.containsKey("password") && datos.get("password") != null) {
                String newPass = (String) datos.get("password");
                if (!newPass.isEmpty()) {
                    usuario.setPassword(passwordEncoder.encode(newPass));
                }
            }
            
            // Actualizar Rol si cambió
            if (datos.containsKey("rol")) {
                 String nombreRol = (String) datos.get("rol");
                 rolRepository.findByNombreRol(nombreRol).ifPresent(usuario::setRol);
            }

            return ResponseEntity.ok(usuarioRepository.save(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}