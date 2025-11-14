package clinica.backend.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String documentoIdentidad;
    private String password;
}