package clinica.backend.config;

import clinica.backend.service.UsuarioSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioSecurityService usuarioSecurityService;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // 1. Define el Encriptador de Contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Define el AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(usuarioSecurityService)
            .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    // 3. Define la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (login y registro)
                .requestMatchers("/api/auth/**").permitAll() 
                // Endpoints de Admin (Ejemplo, ajusta según tus roles)
                .requestMatchers("/api/pacientes/**").hasAnyAuthority("ROL_ADMIN", "ROL_RECEPCIONISTA")
                .requestMatchers("/api/odontologos/**").hasAnyAuthority("ROL_ADMIN")
                // Endpoints de Paciente (Ejemplo)
                .requestMatchers("/api/citas/paciente/**").hasAuthority("ROL_PACIENTE")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated() 
            )
            // Configura la gestión de sesión como STATELESS (sin estado)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            );

        // Añade nuestro filtro de JWT ANTES del filtro de autenticación de Spring
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}