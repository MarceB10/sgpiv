package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.model.Empresa;
import sgpiv.model.Lote;
import sgpiv.model.OcupacionLote;
import sgpiv.model.Proyecto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OcupacionLoteRepository extends JpaRepository<OcupacionLote, Long> {

    Optional<OcupacionLote> findByProyecto(Proyecto proyecto);

    Optional<OcupacionLote> findByLote(Lote lote);

    Optional<OcupacionLote> findByProyectoAndLote(Proyecto proyecto, Lote lote);

    Optional<OcupacionLote> findByFechaInicioAndLote(LocalDate fechaInicio, Lote lote);

    @Query("SELECT o FROM OcupacionLote o WHERE o.proyecto.empresa.id = :empresaId AND o.fechaFin IS NULL")
    Optional<OcupacionLote> findOcupacionActiva(@Param("empresaId") Long empresaId);

    List<OcupacionLote> findAll();

}
