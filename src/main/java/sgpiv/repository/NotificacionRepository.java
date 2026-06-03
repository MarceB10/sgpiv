package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Notificacion;
import sgpiv.model.Usuario;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);

    long countByUsuarioAndLeidaFalse(Usuario usuario);
}