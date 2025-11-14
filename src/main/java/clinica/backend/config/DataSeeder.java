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
        // 1. Crear Roles si no existen
        if (rolRepository.findByNombreRol("ROL_PACIENTE").isEmpty()) {
            rolRepository.save(new Rol(null, "ROL_PACIENTE"));
        }
        if (rolRepository.findByNombreRol("ROL_ADMIN").isEmpty()) {
            rolRepository.save(new Rol(null, "ROL_ADMIN"));
        }
        if (rolRepository.findByNombreRol("ROL_ODONTOLOGO").isEmpty()) {
            rolRepository.save(new Rol(null, "ROL_ODONTOLOGO"));
        }
         if (rolRepository.findByNombreRol("ROL_RECEPCIONISTA").isEmpty()) {
            rolRepository.save(new Rol(null, "ROL_RECEPCIONISTA"));
        }

        // 2. Crear usuario Admin si no existe
        if (usuarioRepository.findByDocumentoIdentidad("admin").isEmpty()) {
            Rol adminRol = rolRepository.findByNombreRol("ROL_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: ROL_ADMIN no encontrado."));
            
            Usuario adminUser = new Usuario();
            adminUser.setDocumentoIdentidad("admin");
            adminUser.setPassword(passwordEncoder.encode("admin")); // password: "admin"
            adminUser.setRol(adminRol);
            
            usuarioRepository.save(adminUser);
        }
    }
}