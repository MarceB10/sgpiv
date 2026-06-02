package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.model.SolicitudProyecto;

import java.util.Optional;

public interface SolicitudProyectoRepository extends JpaRepository<SolicitudProyecto, Long> {

    Optional<SolicitudProyecto> findBySolicitudRadicacionId(Long solicitudRadicacionId);

    @Query("SELECT sp FROM SolicitudProyecto sp LEFT JOIN FETCH sp.tareas WHERE sp.id = :id")
    Optional<SolicitudProyecto> findByIdConTareas(@Param("id") Long id);
}
