package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.SolicitudProyecto;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.repository.SolicitudProyectoRepository;
import sgpiv.service.SolicitudService;
import sgpiv.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;
    private final SolicitudService solicitudService;
    private final SolicitudProyectoRepository solicitudProyectoRepository;

    @GetMapping("/home")
    public String home(Model model,
                       HttpSession session){

        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null){
            return "redirect:/";
        }


        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "home");

        SolicitudRadicacion solicitudActiva = solicitudService.obtenerSolicitudActiva(usuario.getCuit());
        model.addAttribute("solicitudActiva", solicitudActiva);

        SolicitudProyecto solicitudProyectoActiva = null;
        if (solicitudActiva != null) {
            solicitudProyectoActiva = solicitudProyectoRepository
                    .findBySolicitudRadicacionId(solicitudActiva.getId())
                    .orElse(null);
        }
        model.addAttribute("solicitudProyectoActiva", solicitudProyectoActiva);

        return "home";
    }
}
