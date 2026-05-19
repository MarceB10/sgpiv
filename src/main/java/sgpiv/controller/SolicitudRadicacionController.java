package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.Solicitud;
import sgpiv.model.Usuario;
import sgpiv.repository.SolicitudRepository;
import sgpiv.repository.UsuarioRepository;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class SolicitudRadicacionController {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/solicitudRadicacion")
    public String mostrarFormulario(Model model){

        model.addAttribute("solicitud", new Solicitud());

        return "solicitudRadicacion";
    }

    @PostMapping("/solicitudRadicacion")
    public String guardarSolicitud(HttpSession session) {

        UsuarioResponseDTO usuarioDTO =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        Usuario usuario = usuarioRepository
                .findByCuit(usuarioDTO.getCuit())
                .orElseThrow();

        Solicitud solicitud = new Solicitud();

        solicitud.setTipo("Solicitud de radicación");
        solicitud.setEstado("EN_REVISION");
        solicitud.setFechaEnvio(LocalDate.now());

        solicitud.setUsuario(usuario);

        solicitudRepository.save(solicitud);

//        return "redirect:/misSolicitudes";
        return "redirect:/home";
    }

}
