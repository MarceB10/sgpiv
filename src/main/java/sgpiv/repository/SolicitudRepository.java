package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudRadicacion, Long> {

    List<SolicitudRadicacion> findByUsuario(Usuario usuario);
    List<SolicitudRadicacion> findByEstado(sgpiv.enums.EstadoSolicitud estado);
//para saber si ya estiste una solicitud
    SolicitudRadicacion findFirstByUsuarioIdAndEstadoIn(
            Long usuarioId,
            List<EstadoSolicitud> estados
    );

}