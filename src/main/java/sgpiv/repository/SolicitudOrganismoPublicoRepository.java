package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.enums.EstadoSolicitudOrganismo;
import sgpiv.model.SolicitudOrganismoPublico;

import java.util.List;
import java.util.Optional;

public interface SolicitudOrganismoPublicoRepository extends JpaRepository<SolicitudOrganismoPublico, Long> {

    List<SolicitudOrganismoPublico> findByEstado(EstadoSolicitudOrganismo estado);

    Optional<SolicitudOrganismoPublico> findByUsuarioId(Long usuarioId);
}
