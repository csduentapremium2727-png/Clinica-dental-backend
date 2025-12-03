package clinica.backend.controller;

import clinica.backend.model.Odontologo;
import clinica.backend.service.OdontologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/odontologos")
@CrossOrigin // Permite peticiones desde Angular
public class OdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    // GET /api/odontologos
    @GetMapping
    public List<Odontologo> listarOdontologos() {
        return odontologoService.listarOdontologos();
    }

    // GET /api/odontologos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Odontologo> obtenerOdontologo(@PathVariable Long id) {
        return odontologoService.obtenerOdontologo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/odontologos
    @PostMapping
    public ResponseEntity<Odontologo> guardarOdontologo(@RequestBody Odontologo odontologo) {
        // Forzamos ID nulo para crear nuevo
        odontologo.setId(null); // Asegúrate que tu modelo tenga setId (Lombok o manual)
        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(odontologo);
        return new ResponseEntity<>(odontologoGuardado, HttpStatus.CREATED);
    }

    // PUT /api/odontologos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Odontologo> actualizarOdontologo(@PathVariable Long id, @RequestBody Odontologo odontologo) {
        // Verificamos si existe antes de actualizar
        if (odontologoService.obtenerOdontologo(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        odontologo.setId(id); // Forzamos el ID de la URL
        Odontologo odontologoActualizado = odontologoService.actualizarOdontologo(odontologo);
        return ResponseEntity.ok(odontologoActualizado);
    }

    // DELETE /api/odontologos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {
        if (odontologoService.obtenerOdontologo(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        odontologoService.eliminarOdontologo(id);
        return ResponseEntity.noContent().build();
    }
}