package clinica.backend.service;

import clinica.backend.model.Usuario;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioSecurityService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario por documento de identidad
        Usuario usuario = usuarioRepository.findByDocumentoIdentidad(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 1. Obtener el nombre del rol tal cual viene de la base de datos
        String nombreRol = usuario.getRol().getNombreRol();

        // 2. Limpieza profunda para evitar errores de espacios o nulos
        if (nombreRol != null) {
            nombreRol = nombreRol.trim().toUpperCase();
        } else {
            nombreRol = "PACIENTE"; // Fallback seguro
        }

        // 3. Lógica de normalización: Eliminar prefijos existentes para evitar duplicados
        if (nombreRol.startsWith("ROLE_")) {
            nombreRol = nombreRol.substring(5); // Quita "ROLE_"
        } else if (nombreRol.startsWith("ROL_")) {
            nombreRol = nombreRol.substring(4); // Quita "ROL_"
        }
        
        // 4. Construir la autoridad final estándar: SIEMPRE será ROLE_ + NOMBRE
        String autoridadFinal = "ROLE_" + nombreRol; 

        // Log de confirmación en la consola del backend
        System.out.println(">>> SEGURIDAD: Usuario " + username + " autenticado con rol final: [" + autoridadFinal + "]");

        return User.builder()
                .username(usuario.getDocumentoIdentidad())
                .password(usuario.getPassword())
                .authorities(autoridadFinal) 
                .build();
    }
}