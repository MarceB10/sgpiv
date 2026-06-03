package sgpiv.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.Usuario;
import sgpiv.repository.UsuarioRepository;
import sgpiv.service.NotificacionService;

@Component
@RequiredArgsConstructor
public class NotificacionInterceptor implements HandlerInterceptor {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        if (modelAndView == null) return;

        HttpSession session = request.getSession(false);
        if (session == null) return;

        UsuarioResponseDTO usuarioDTO = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuarioDTO == null) return;

        usuarioRepository.findByCuit(usuarioDTO.getCuit()).ifPresent(usuario -> {
            long noLeidas = notificacionService.contarNoLeidas(usuario);
            modelAndView.addObject("notificacionesNoLeidas", noLeidas);
            modelAndView.addObject("notificaciones", notificacionService.obtenerNotificaciones(usuario));
        });
    }
}