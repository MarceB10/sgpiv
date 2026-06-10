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
        // el DTO ya incluye el campo solicitudRadicacionId
        SolicitudProyectoRequestDTO dto = new SolicitudProyectoRequestDTO();
        dto.setSolicitudRadicacionId(solicitudRadicacion.getId());
        model.addAttribute("solicitudProyectoDTO", dto);
        model.addAttribute("pagina", "solicitudProyecto");
        return "solicitudProyecto";
    }

//    @PostMapping
//    public String guardar(@Valid @ModelAttribute SolicitudProyectoRequestDTO dto,
//                          BindingResult result,
//                          HttpSession session,
//                          Model model) {
//
//        if (result.hasErrors()) {
//            UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("solicitudProyectoDTO", dto);
//            return "solicitudProyecto";
//        }
//
//        solicitudService.guardarSolicitudProyecto(dto);
//        return "redirect:/home";
//    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("solicitudProyectoDTO") SolicitudProyectoRequestDTO dto,
            BindingResult result, HttpSession session, Model model) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null) return "redirect:/";

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "solicitudProyecto");
            return "solicitudProyecto";
        }

        solicitudService.guardarSolicitudProyecto(dto);
        return "redirect:/home";
    }

    @GetMapping("/miSolicitudProyecto")
    public String verMiSolicitudProyecto(HttpSession session, Model model){
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if(usuario == null) return "redirect:/";

        var proyecto = solicitudService.obtenerSolicitudProyecto(usuario.getCuit());
        model.addAttribute("usuario", usuario);

        if(proyecto.getEstado().name().equals("REQUIERE_MODIFICACION")){
            SolicitudProyectoRequestDTO dto = solicitudService.convertirARequestDTO(proyecto);
            model.addAttribute("solicitudProyectoDTO", dto);
            model.addAttribute("editando", true);
            model.addAttribute("motivo", proyecto.getMotivoRechazo());
            return "solicitudProyecto"; // misma vista que al crear
        }

        // en otros estados mostramos detalle
        model.addAttribute("solicitudProyecto", proyecto);
        model.addAttribute("pagina","mi-solicitud-proyecto");
        return "representante_empresa/miSolicitudProyecto";
    }
}
