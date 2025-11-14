package clinica.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_identidad", unique = true, nullable = false, length = 20)
    private String documentoIdentidad;
    
    // --- CAMPO AÑADIDO ---
    @Column(name = "password", nullable = false, length = 60) // 60 chars para el hash de BCrypt
    private String password;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "rol_id", referencedColumnName = "id", nullable = false)
    private Rol rol;
}