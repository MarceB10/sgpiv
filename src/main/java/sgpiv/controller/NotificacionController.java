package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.Usuario;
import sgpiv.repository.UsuarioRepository;
import sgpiv.service.NotificacionService;

@Controller
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/notificaciones")
    public String verNotificaciones(Model model, HttpSession session) {
        UsuarioResponseDTO usuarioDTO = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuarioDTO == null) return "redirect:/login";

        Usuario usuario = usuarioRepository.findByCuit(usuarioDTO.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        notificacionService.marcarTodasLeidas(usuario);

        model.addAttribute("notificaciones", notificacionService.obtenerNotificaciones(usuario));
        model.addAttribute("usuario", usuarioDTO);
        model.addAttribute("pagina", "notificaciones");

        return "notificaciones";
    }

    @PostMapping("/notificaciones/marcar-leidas")
    @ResponseBody
    public void marcarLeidas(HttpSession session) {
        UsuarioResponseDTO usuarioDTO = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuarioDTO == null) return;
        Usuario usuario = usuarioRepository.findByCuit(usuarioDTO.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        notificacionService.marcarTodasLeidas(usuario);
    }
}