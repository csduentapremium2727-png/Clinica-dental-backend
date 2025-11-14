package clinica.backend.config;

import clinica.backend.model.Rol;
import clinica.backend.model.Usuario;
import clinica.backend.repository.RolRepository;
import clinica.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // --- INICIO DE CORRECCIÓN ---
        
        // 1. Crear Roles si no existen (con los nombres correctos)
        if (rolRepository.findByNombreRol("PACIENTE").isEmpty()) {
            rolRepository.save(new Rol(null, "PACIENTE"));
        }
        if (rolRepository.findByNombreRol("ADMIN").isEmpty()) {
            rolRepository.save(new Rol(null, "ADMIN"));
        }
        if (rolRepository.findByNombreRol("ODONTOLOGO").isEmpty()) {
            rolRepository.save(new Rol(null, "ODONTOLOGO"));
        }
         if (rolRepository.findByNombreRol("RECEPCIONISTA").isEmpty()) {
            rolRepository.save(new Rol(null, "RECEPCIONISTA"));
        }

        // 2. Crear usuario Admin si no existe
        if (usuarioRepository.findByDocumentoIdentidad("admin").isEmpty()) {
            // Buscamos por el nombre corregido "ADMIN"
            Rol adminRol = rolRepository.findByNombreRol("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: ROL_ADMIN no encontrado."));
            
            Usuario adminUser = new Usuario();
            adminUser.setDocumentoIdentidad("admin");
            adminUser.setPassword(passwordEncoder.encode("admin")); // password: "admin"
            adminUser.setRol(adminRol);
            
            usuarioRepository.save(adminUser);
        }
        
        // --- FIN DE CORRECCIÓN ---
    }
}