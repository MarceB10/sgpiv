package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.Notificacion;
import sgpiv.model.Usuario;
import sgpiv.repository.UsuarioRepository;
import sgpiv.service.NotificacionService;

import java.util.List;

@Controller
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    /**
     * GET /notificaciones
     * Muestra la bandeja de entrada completa.
     * El interceptor (NotificacionInterceptor) ya inyecta en el model:
     *   - notificaciones          (List<Notificacion>)
     *   - notificacionesNoLeidas  (long)
     * Sólo hace falta devolver la vista.
     */
    @GetMapping
    public String verNotificaciones(HttpSession session, Model model) {
        Usuario usuario = getUsuarioFromSession(session);
        if (usuario == null) return "redirect:/";

        // El interceptor ya lo inyecta, pero si querés reforzarlo:
        List<Notificacion> notificaciones = notificacionService.obtenerNotificaciones(usuario);
        long noLeidas = notificacionService.contarNoLeidas(usuario);

        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("notificacionesNoLeidas", noLeidas);
        //agregado
        model.addAttribute("usuario", session.getAttribute("usuario"));
        model.addAttribute("pagina", "notificaciones");

        return "notificaciones";     // → templates/notificaciones.html
    }

    /**
     * POST /notificaciones/marcar-leidas
     * Marca todas las notificaciones del usuario como leídas.
     */
    @PostMapping("/marcar-leidas")
    public String marcarTodasLeidas(HttpSession session) {
        Usuario usuario = getUsuarioFromSession(session);
        if (usuario == null) return "redirect:/";

        notificacionService.marcarTodasLeidas(usuario);
        return "redirect:/notificaciones";
    }

    /**
     * POST /notificaciones/marcar-leida/{id}
     * Marca una notificación individual como leída.
     */
    @PostMapping("/marcar-leida/{id}")
    public String marcarUnaLeida(@PathVariable Long id, HttpSession session) {
        Usuario usuario = getUsuarioFromSession(session);
        if (usuario == null) return "redirect:/";

        notificacionService.marcarLeida(id, usuario);
        return "redirect:/notificaciones";
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Usuario getUsuarioFromSession(HttpSession session) {
        UsuarioResponseDTO dto = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (dto == null) return null;
        return usuarioRepository.findByCuit(dto.getCuit()).orElse(null);
    }
}