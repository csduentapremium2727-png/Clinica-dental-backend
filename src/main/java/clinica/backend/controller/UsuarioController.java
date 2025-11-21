package clinica.backend.controller;

import clinica.backend.model.Usuario;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Endpoint para el perfil
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerPerfil() {
        // Obtenemos el usuario del token actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByDocumentoIdentidad(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}