package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sgpiv.dtos.request.SolicitudProyectoRequestDTO;
import sgpiv.dtos.response.SolicitudResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.service.SolicitudService;

@Controller
@RequestMapping("/solicitudProyecto")
@RequiredArgsConstructor
public class SolicitudProyectoController {


    private final SolicitudService solicitudService;

    @GetMapping
    public String formulario(HttpSession session, Model model) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        // Verificar que tenga una solicitud de radicacion aprobada
        SolicitudResponseDTO solicitudRadicacion = solicitudService
                .obtenerSolicitudRadicacionAprobadaPrimerParte(usuario.getCuit());

        model.addAttribute("usuario", usuario);
        model.addAttribute("solicitudProyectoDTO", new SolicitudProyectoRequestDTO());
        model.addAttribute("solicitudRadicacionId", solicitudRadicacion.getId());
        model.addAttribute("pagina", "solicitudProyecto");
        return "solicitudProyecto";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute SolicitudProyectoRequestDTO dto,
                          BindingResult result,
                          @RequestParam Long solicitudRadicacionId,
                          HttpSession session,
                          Model model) {

        if (result.hasErrors()) {
            UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
            model.addAttribute("usuario", usuario);
            model.addAttribute("solicitudRadicacionId", solicitudRadicacionId);
            return "solicitudProyecto";
        }

        dto.setSolicitudRadicacionId(solicitudRadicacionId);
        solicitudService.guardarSolicitudProyecto(dto);
        return "redirect:/home";
    }


}
