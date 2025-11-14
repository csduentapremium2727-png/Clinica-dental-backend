package clinica.backend.service;

import clinica.backend.model.Odontologo;
import clinica.backend.repository.OdontologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<Odontologo> listarOdontologos() {
        return odontologoRepository.findAll();
    }

    public Optional<Odontologo> obtenerOdontologo(Long id) {
        return odontologoRepository.findById(id);
    }

    public Odontologo guardarOdontologo(Odontologo odontologo) {
        return odontologoRepository.save(odontologo);
    }

    public Odontologo actualizarOdontologo(Odontologo odontologo) {
        if (!odontologoRepository.existsById(odontologo.getId())) {
            throw new RuntimeException("Odontólogo no encontrado");
        }
        return odontologoRepository.save(odontologo);
    }

    public void eliminarOdontologo(Long id) {
        odontologoRepository.deleteById(id);
    }
}