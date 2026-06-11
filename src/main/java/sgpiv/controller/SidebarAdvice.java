package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.SolicitudOrganismoPublico;
import sgpiv.model.SolicitudProyecto;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.repository.SolicitudOrganismoPublicoRepository;
import sgpiv.repository.SolicitudProyectoRepository;
import sgpiv.service.SolicitudService;

@ControllerAdvice
@RequiredArgsConstructor
public class SidebarAdvice {

    private final SolicitudService solicitudService;
    private final SolicitudProyectoRepository solicitudProyectoRepository;
    private final SolicitudOrganismoPublicoRepository solicitudOrganismoPublicoRepository;

    /*
    Esta clase se encarga de cargar al model la solicitud activa y asi cambia de estado las opciones
    por eso mismo para no repetir codigo, SIEMPRE QUE UN USUARIO CON ROL NULO ACTUE esta clase carga la solicitud de proyecto
    para poder ver sus funcionalidades dependiendo en que paso de la radicacion este
     */

    @ModelAttribute
    public void agregarDatosSidebar(HttpSession session, Model model) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return;

        String rol = usuario.getRoles().get(0).getNombre().name();

        if (rol.equals("ROL_NULO") || rol.equals("ROL_REPRESENTANTE_EMPRESA")) {
            SolicitudRadicacion solicitudActiva =
                    solicitudService.obtenerSolicitudActiva(usuario.getCuit());
            model.addAttribute("solicitudActiva", solicitudActiva);

            if (solicitudActiva != null) {
                SolicitudProyecto solicitudProyectoActiva = solicitudProyectoRepository
                        .findBySolicitudRadicacionId(solicitudActiva.getId())
                        .orElse(null);
                model.addAttribute("solicitudProyectoActiva", solicitudProyectoActiva);
            }
        }

        if (rol.equals("ROL_NULO")) {
            SolicitudOrganismoPublico solicitudOrganismo = solicitudOrganismoPublicoRepository
                    .findByUsuarioId(usuario.getId())
                    .orElse(null);
            model.addAttribute("solicitudOrganismoActiva", solicitudOrganismo);
        }
    }
}
