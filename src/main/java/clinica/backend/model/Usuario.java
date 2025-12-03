package clinica.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Mantenemos Lombok para el resto, pero aseguramos los importantes abajo
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
    
    @Column(name = "contrasena_hash", nullable = false, length = 60)
    private String password;
    
    // --- CAMPOS NUEVOS ---
    @Column(length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;
    // ---------------------

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "rol_id", referencedColumnName = "id", nullable = false)
    private Rol rol;

    // ==========================================
    // GETTERS Y SETTERS MANUALES (Para asegurar)
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // Agregamos estos también por si acaso
    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}