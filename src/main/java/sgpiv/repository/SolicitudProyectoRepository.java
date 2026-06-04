package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.enums.EstadoSolicitudProyecto;
import sgpiv.model.SolicitudProyecto;

import java.util.List;
import java.util.Optional;

public interface SolicitudProyectoRepository extends JpaRepository<SolicitudProyecto, Long> {

    Optional<SolicitudProyecto> findBySolicitudRadicacionId(Long solicitudRadicacionId);

    @Query("SELECT sp FROM SolicitudProyecto sp LEFT JOIN FETCH sp.tareas WHERE sp.id = :id")
    Optional<SolicitudProyecto> findByIdConTareas(@Param("id") Long id);

    @Query("SELECT sp FROM SolicitudProyecto sp LEFT JOIN FETCH sp.tareas WHERE sp.estado = :estado")
    List<SolicitudProyecto> findByEstadoConTareas(@Param("estado") EstadoSolicitudProyecto estado);

    // O sin tareas
    List<SolicitudProyecto> findByEstado(EstadoSolicitudProyecto estado);
}
