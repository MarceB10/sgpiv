package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.service.SolicitudService;

@Controller
@RequiredArgsConstructor
public class SolicitudRadicacionController {

    private final SolicitudService solicitudService;

    @GetMapping("/solicitudRadicacion")
    public String mostrarFormulario(Model model, HttpSession session) {
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        SolicitudRadicacion solicitudActiva =
                solicitudService.obtenerSolicitudActiva(usuario.getCuit());

        model.addAttribute("solicitudActiva", solicitudActiva);
        //para la modificacion
        if(solicitudActiva != null &&
                solicitudActiva.getEstado() == EstadoSolicitud.REQUIERE_MODIFICACION){
            model.addAttribute("solicitudDTO",
                    solicitudService.toRequestDTO(solicitudActiva));
            model.addAttribute("motivo", solicitudActiva.getMotivoRechazo());
            model.addAttribute("editando", true);
            model.addAttribute("usuario", usuario);
            return "solicitudRadicacion";
        }
        if (solicitudActiva != null){
            return "redirect:/home";
        }

        model.addAttribute("solicitudDTO", new SolicitudRequestDTO());
        model.addAttribute("usuario", usuario);

        model.addAttribute("pagina", "solicitud-radicacion");

        return "solicitudRadicacion";
    }

    @PostMapping("/solicitudRadicacion")
    public String guardarSolicitud(@Valid @ModelAttribute("solicitudDTO") SolicitudRequestDTO dto,
                                   BindingResult result,
                                   HttpSession session,
                                   Model model) {
        UsuarioResponseDTO usuarioDTO =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if (result.hasErrors()) {
            SolicitudRadicacion solicitudActiva =
                    solicitudService.obtenerSolicitudActiva(usuarioDTO.getCuit());

            model.addAttribute("usuario", usuarioDTO);
            model.addAttribute("solicitudActiva", solicitudActiva);
            model.addAttribute("pagina", "solicitud-radicacion");

            if (solicitudActiva != null &&
                    solicitudActiva.getEstado() == EstadoSolicitud.REQUIERE_MODIFICACION) {
                model.addAttribute("editando", true);
                model.addAttribute("motivo", solicitudActiva.getMotivoRechazo());
            }

            return "solicitudRadicacion";
        }

        try {
            solicitudService.enviarSolicitud(dto, usuarioDTO.getCuit());
            return "redirect:/home";

        } catch (RuntimeException e) {
            model.addAttribute("usuario", usuarioDTO);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pagina", "solicitud-radicacion");
            return "solicitudRadicacion";
        }
    }

    @GetMapping("/miSolicitud")
    public String miSolicitud(Model model, HttpSession session) {
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        SolicitudRadicacion solicitud =
                solicitudService.obtenerSolicitudActiva(usuario.getCuit());

        if (solicitud == null) return "redirect:/solicitudRadicacion";

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("solicitudActiva",solicitud);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "mi-solicitud");

        return "representante_empresa/miSolicitud";
    }
}