package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Notificacion;
import sgpiv.model.Usuario;
import sgpiv.repository.NotificacionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public void crearNotificacion(String mensaje, Usuario usuario) {
        notificacionRepository.save(new Notificacion(mensaje, usuario));
    }

    public List<Notificacion> obtenerNotificaciones(Usuario usuario) {
        return notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public long contarNoLeidas(Usuario usuario) {
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    public void marcarTodasLeidas(Usuario usuario) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
        notificaciones.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(notificaciones);
    }

    /**
     * Marca una notificación individual como leída.
     * Sólo la marca si pertenece al usuario de la sesión (seguridad básica).
     */
    public void marcarLeida(Long id, Usuario usuario) {
        notificacionRepository.findById(id).ifPresent(n -> {
            if (n.getUsuario().getId().equals(usuario.getId())) {
                n.setLeida(true);
                notificacionRepository.save(n);
            }
        });
    }
}
