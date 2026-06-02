package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.SolicitudProyecto;

import java.util.Optional;

public interface SolicitudProyectoRepository extends JpaRepository<SolicitudProyecto, Long> {

    Optional<SolicitudProyecto> findByIdConTareas(Long idSolicitudProyecto);
}
