package clinica.backend.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String rol;

    public AuthResponseDTO(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }
}