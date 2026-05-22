package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Empresa;
import sgpiv.model.Lote;
import sgpiv.model.OcupacionLote;

import java.time.LocalDate;
import java.util.Optional;

public interface OcupacionLoteRepository extends JpaRepository<OcupacionLote, Long> {

    Optional<OcupacionLote> findByEmpresa(Empresa empresa);

    Optional<OcupacionLote> findByLote(Lote lote);

    Optional<OcupacionLote> findByEmpresaAndLote(Empresa empresa, Lote lote);

    Optional<OcupacionLote> findByFechaInicioAndLote(LocalDate fechaInicio, Lote lote);

}
