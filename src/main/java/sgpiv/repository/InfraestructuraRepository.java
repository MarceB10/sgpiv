package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.enums.EstadoInfraestructura;
import sgpiv.model.Infraestructura;

public interface InfraestructuraRepository extends JpaRepository<Infraestructura, Long> {
    long countByEstado(EstadoInfraestructura estado);
}