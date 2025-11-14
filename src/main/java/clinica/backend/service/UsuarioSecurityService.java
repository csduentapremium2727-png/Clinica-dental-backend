package clinica.backend.service;

import clinica.backend.model.Usuario;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsuarioSecurityService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // En nuestro caso, "username" es el documento de identidad
        Usuario usuario = usuarioRepository.findByDocumentoIdentidad(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Spring Security necesita un objeto UserDetails.
        // Lo construimos con el DNI, la contraseña encriptada y los roles.
        return User.builder()
                .username(usuario.getDocumentoIdentidad())
                .password(usuario.getPassword())
                .authorities("ROL_" + usuario.getRol().getNombreRol()) // Ej: "ROL_ADMIN", "ROL_PACIENTE"
                .build();
    }
}