package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.SolicitudProyecto;
import sgpiv.model.TareaSolicitud;

import java.util.List;

public interface TareaSolicitudRepository extends JpaRepository<TareaSolicitud, Long> {
    List<TareaSolicitud> findBySolicitudProyecto(SolicitudProyecto solicitudProyecto);
}
