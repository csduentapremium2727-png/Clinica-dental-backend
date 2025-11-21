package clinica.backend.service;

import clinica.backend.dto.CitaRequestDTO;
import clinica.backend.model.Cita;
import clinica.backend.model.Odontologo;
import clinica.backend.model.Paciente;
import clinica.backend.repository.CitaRepository;
import clinica.backend.repository.OdontologoRepository;
import clinica.backend.repository.PacienteRepository;

// Imports añadidos para el correo y el log
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private static final Logger logger = LoggerFactory.getLogger(CitaService.class);

    @Autowired
    private EmailService emailService; // Inyecta el servicio de correo
    
    private final String ADMIN_EMAIL = "jjnmcontacto@gmail.com"; // Email del Admin

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> obtenerCita(Long id) {
        return citaRepository.findById(id);
    }

    public Cita agendarCita(CitaRequestDTO request) {
        
        // 1. Buscamos las entidades
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Odontologo odontologo = odontologoRepository.findById(request.getOdontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // 2. Creamos la nueva entidad Cita
        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setOdontologo(odontologo);
        nuevaCita.setFechaCita(request.getFechaCita());
        nuevaCita.setHoraCita(request.getHoraCita());
        nuevaCita.setMotivo(request.getMotivo());
        nuevaCita.setEstado("Programada"); // Estado por defecto

        // 3. ¡GUARDAMOS LA CITA!
        Cita citaGuardada = citaRepository.save(nuevaCita);

        // --- INICIO DE LÓGICA DE CORREOS (CON DISEÑO) ---

        // 4. Enviar correo de confirmación al Paciente
        // try {
        //     String subjectPaciente = "Confirmación de tu Cita - Clínica Sonrisa Plena";
            
        //     // Usamos un Bloque de Texto de Java 17 (más limpio que concatenar strings)
        //     String contentPaciente = """
        //         <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
        //             <table width="100%%" cellspacing="0" cellpadding="0" border="0">
        //                 <tr>
        //                     <td style="background-color: #007bff; color: white; padding: 25px 30px; text-align: center;">
        //                         <h2 style="margin: 0; font-size: 24px;">Clínica Sonrisa Plena</h2>
        //                     </td>
        //                 </tr>
                        
        //                 <tr>
        //                     <td style="padding: 30px 40px; font-size: 16px; line-height: 1.6; color: #333;">
        //                         <h1 style="color: #007bff; margin-top: 0; font-size: 28px;">¡Tu cita ha sido confirmada!</h1>
        //                         <p>Hola, <b>%s</b>,</p>
        //                         <p>Tu cita ha sido agendada con éxito. A continuación, te dejamos los detalles:</p>
                                
        //                         <table style="width: 100%%; border-collapse: collapse; margin-top: 25px; margin-bottom: 25px;">
        //                             <tr style="border-bottom: 1px solid #eee;">
        //                                 <td style="padding: 12px 0; color: #555; width: 120px;"><b>Fecha:</b></td>
        //                                 <td style="padding: 12px 0; color: #000;"><b>%s</b></td>
        //                             </tr>
        //                             <tr style="border-bottom: 1px solid #eee;">
        //                                 <td style="padding: 12px 0; color: #555;"><b>Hora:</b></td>
        //                                 <td style="padding: 12px 0; color: #000;"><b>%s</b></td>
        //                             </tr>
        //                             <tr style="border-bottom: 1px solid #eee;">
        //                                 <td style="padding: 12px 0; color: #555;"><b>Odontólogo:</b></td>
        //                                 <td style="padding: 12px 0; color: #000;">Dr(a). %s (%s)</td>
        //                             </tr>
        //                             <tr style="border-bottom: 1px solid #eee;">
        //                                 <td style="padding: 12px 0; color: #555;"><b>Motivo:</b></td>
        //                                 <td style="padding: 12px 0; color: #000;">%s</td>
        //                             </tr>
        //                         </table>
                                
        //                         <p style="margin-top: 20px;">Si necesitas reprogramar o cancelar, por favor contáctanos con anticipación.</p>
        //                         <p>Gracias por confiar en nosotros.</p>
        //                     </td>
        //                 </tr>
                        
        //                 <tr>
        //                     <td style="background-color: #f4f4f4; color: #888; padding: 20px 30px; text-align: center; font-size: 12px;">
        //                         <p style="margin: 0;">© 2025 Clínica Sonrisa Plena. Todos los derechos reservados.</p>
        //                     </td>
        //                 </tr>
        //             </table>
        //         </div>
        //     """.formatted(
        //         paciente.getNombre() + " " + paciente.getApellido(), // %s (Hola, [Nombre])
        //         citaGuardada.getFechaCita(),                       // %s (Fecha)
        //         citaGuardada.getHoraCita(),                       // %s (Hora)
        //         odontologo.getNombre() + " " + odontologo.getApellido(), // %s (Dr. Nombre)
        //         odontologo.getEspecialidad(),                     // %s (Especialidad)
        //         citaGuardada.getMotivo()                          // %s (Motivo)
        //     );
            
        //     emailService.sendHtmlEmail(paciente.getEmail(), subjectPaciente, contentPaciente);
            
        // } catch (Exception e) {
        //     logger.warn("Cita " + citaGuardada.getId() + " registrada, pero falló el envío de correo al paciente: " + e.getMessage());
        // }

        // 5. Enviar correo de notificación al Admin
        // try {
        //     String subjectAdmin = "Notificación de Nueva Cita: #" + citaGuardada.getId();
            
        //     String contentAdmin = """
        //         <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;">
        //             <table width="100%%" cellspacing="0" cellpadding="0" border="0">
        //                 <tr>
        //                     <td style="background-color: #333; color: white; padding: 20px 30px; text-align: center;">
        //                         <h2 style="margin: 0;">Notificación del Sistema</h2>
        //                     </td>
        //                 </tr>
        //                 <tr>
        //                     <td style="padding: 30px 40px; font-size: 16px; line-height: 1.6;">
        //                         <h1 style="color: #333; margin-top: 0;">Nueva Cita Registrada (#%d)</h1>
        //                         <p>Se ha registrado una nueva cita en el sistema:</p>
        //                         <ul style="list-style: none; padding-left: 0; line-height: 1.8;">
        //                             <li style="padding: 8px 0; border-bottom: 1px solid #eee;"><b>Paciente:</b> %s (ID: %d)</li>
        //                             <li style="padding: 8px 0; border-bottom: 1px solid #eee;"><b>Odontólogo:</b> Dr(a). %s (ID: %d)</li>
        //                             <li style="padding: 8px 0; border-bottom: 1px solid #eee;"><b>Fecha y Hora:</b> %s a las %s</li>
        //                             <li style="padding: 8px 0;"><b>Motivo:</b> %s</li>
        //                         </ul>
        //                     </td>
        //                 </tr>
        //             </table>
        //         </div>
        //     """.formatted(
        //         citaGuardada.getId(), // %d (Cita ID)
        //         paciente.getNombre() + " " + paciente.getApellido(), // %s (Paciente)
        //         paciente.getId(), // %d (Paciente ID)
        //         odontologo.getNombre() + " " + odontologo.getApellido(), // %s (Odontólogo)
        //         odontologo.getId(), // %d (Odontólogo ID)
        //         citaGuardada.getFechaCita(), // %s (Fecha)
        //         citaGuardada.getHoraCita(), // %s (Hora)
        //         citaGuardada.getMotivo() // %s (Motivo)
        //     );
            
        //     emailService.sendHtmlEmail(ADMIN_EMAIL, subjectAdmin, contentAdmin);
            
        // } catch (Exception e) {
        //     logger.warn("Cita " + citaGuardada.getId() + " registrada, pero falló el envío de correo al admin: " + e.getMessage());
        // }
        // --- FIN DE LÓGICA DE CORREOS ---

        // 6. Devolvemos la cita creada
        return citaGuardada;
    }

    // Métodos de búsqueda que definimos en el repositorio
    public List<Cita> citasPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public List<Cita> citasPorOdontologo(Long odontologoId) {
        return citaRepository.findByOdontologoId(odontologoId);
    }
    
    public void eliminarCita(Long id) {
        citaRepository.deleteById(id);
    }
}