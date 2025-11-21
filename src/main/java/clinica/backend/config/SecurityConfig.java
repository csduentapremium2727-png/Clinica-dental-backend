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

    // Configuración CORS Global
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Filtros de Seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                
                // 1. PÚBLICO: Login, Registro y Preflight
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2. ¡CRUCIAL! Permitir ver la página de errores interna de Spring
                // Esto desbloqueará el 403 falso y mostrará el error real (404 o 500)
                .requestMatchers("/error").permitAll()

                // 3. PERFIL DE USUARIO: Accesible para cualquier usuario logueado
                .requestMatchers("/api/usuarios/perfil").authenticated()

                // 4. REGLAS DE ROLES (Usamos hasAuthority para coincidencia EXACTA con ROLE_ADMIN)
                
                // Gestión de Usuarios y Odontólogos -> SOLO ADMIN
                .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/odontologos/**").hasAuthority("ROLE_ADMIN")

                // Gestión de Pacientes y Facturas -> ADMIN o RECEPCIONISTA
                .requestMatchers("/api/pacientes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                .requestMatchers("/api/facturas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                
                // Citas -> Acceso general para roles internos
                .requestMatchers("/api/citas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA", "ROLE_ODONTOLOGO", "ROLE_PACIENTE")
                
                // Solo Paciente (para ver sus propias cosas específicas)
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