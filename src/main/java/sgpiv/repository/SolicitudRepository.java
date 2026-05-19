package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Solicitud;

public interface SolicitudRepository
        extends JpaRepository<Solicitud, Long> {
}