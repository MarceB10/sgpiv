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

        if (solicitudActiva != null){
            return "redirect:/home";
        }

        model.addAttribute("solicitudDTO", new SolicitudRequestDTO());
        model.addAttribute("usuario", usuario);
        return "solicitudRadicacion";
    }

    @PostMapping("/solicitudRadicacion")
    public String guardarSolicitud(@Valid @ModelAttribute("solicitudDTO") SolicitudRequestDTO dto,
                                   BindingResult result,
                                   HttpSession session,
                                   Model model) {
        if (result.hasErrors()) {
            return "solicitudRadicacion";
        }

        try {
            UsuarioResponseDTO usuarioDTO =
                    (UsuarioResponseDTO) session.getAttribute("usuario");

            solicitudService.enviarSolicitud(dto, usuarioDTO.getCuit());
            return "redirect:/home";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "solicitudRadicacion";
        }
    }
}