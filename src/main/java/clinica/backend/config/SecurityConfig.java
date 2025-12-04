package clinica.backend.config;

import clinica.backend.service.UsuarioSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioSecurityService usuarioSecurityService;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // Encriptador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Gestor de Autenticación
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(usuarioSecurityService)
            .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Usamos setAllowedOrigins explícito (más seguro y compatible que Patterns en algunos navegadores)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:4200", 
            "https://dental-frontend-plum.vercel.app"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permitir todos los headers
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // Filtros de Seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Usa el bean corsConfigurationSource definido arriba
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                
                // 1. PÚBLICO: Login, Registro y Preflight
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2. Permitir ver la página de errores interna de Spring
                .requestMatchers("/error").permitAll()

                // 3. PERFIL DE USUARIO
                .requestMatchers("/api/usuarios/perfil").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/odontologos/**").authenticated()
                .requestMatchers("/api/pacientes/usuario/**").authenticated()
                // 4. REGLAS DE ROLES
                // Gestión de Usuarios y Odontólogos -> SOLO ADMIN
                .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/odontologos/**").hasAuthority("ROLE_ADMIN")

                .requestMatchers("/api/pacientes/buscar").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA", "ROLE_PACIENTE")
    
                // Si necesitas que el paciente vea su detalle por ID:
                .requestMatchers("/api/pacientes/{id}").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA", "ROLE_PACIENTE")

                // Gestión general de Pacientes (Crear, Borrar, Listar todos) -> ADMIN o RECEPCIONISTA
                .requestMatchers("/api/pacientes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                // ---------------------------

                .requestMatchers("/api/facturas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                
                // Citas -> Acceso general para roles internos
                .requestMatchers("/api/citas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA", "ROLE_ODONTOLOGO", "ROLE_PACIENTE")
                
                // Solo Paciente
                .requestMatchers("/api/citas/paciente/**").hasAuthority("ROLE_PACIENTE")

                // Todo lo demás requiere estar logueado
                .anyRequest().authenticated() 
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            );

        // Añadimos el filtro JWT antes de procesar la autenticación estándar
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}