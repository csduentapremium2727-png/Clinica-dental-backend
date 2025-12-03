package clinica.backend.controller;

import clinica.backend.dto.AuthResponseDTO;
import clinica.backend.dto.LoginRequestDTO;
import clinica.backend.dto.RegistroOdontologoDTO;
import clinica.backend.dto.RegistroPacienteDTO;
import clinica.backend.model.Odontologo;
import clinica.backend.model.Paciente;
import clinica.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    
    // POST /api/auth/registrar
    @PostMapping("/registrar")
    public ResponseEntity<Paciente> registrarPaciente(@RequestBody RegistroPacienteDTO registroDTO) {
        Paciente pacienteRegistrado = authService.registrarPaciente(registroDTO);
        return new ResponseEntity<>(pacienteRegistrado, HttpStatus.CREATED);
    }

    @PostMapping("/registrar-odontologo")
    public ResponseEntity<Odontologo> registrarOdontologo(@RequestBody RegistroOdontologoDTO registroDTO) {
        Odontologo odontologoRegistrado = authService.registrarOdontologo(registroDTO);
        return new ResponseEntity<>(odontologoRegistrado, HttpStatus.CREATED);
 
    }
    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }
}