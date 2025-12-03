package clinica.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import clinica.backend.config.JwtRequestFilter;

@SpringBootApplication
public class ClinicaDentalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicaDentalBackendApplication.class, args);
	}
	@Bean
    public FilterRegistrationBean<JwtRequestFilter> registration(JwtRequestFilter filter) {
        FilterRegistrationBean<JwtRequestFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); // Desactiva el registro automático global
        return registration;
    }

}
