package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Proyecto;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Query("SELECT p FROM Proyecto p JOIN FETCH p.empresa e WHERE e.estadoEmpresa = :estado")
    List<Proyecto> findByEmpresaEstado(@Param("estado") EstadoEmpresa estado);



    @Query("SELECT p FROM Proyecto p JOIN FETCH p.empresa e JOIN FETCH p.representanteEmpresa r WHERE e.id = :empresaId AND r.id = :representanteId")
    Optional<Proyecto> findByEmpresaIdAndRepresentanteId(@Param("empresaId") Long empresaId,
                                                             @Param("representanteId") Long representanteId);

        // Solo por representante
    Optional<Proyecto> findByRepresentanteEmpresa_Id(Long representanteId);

        // Solo por empresa
    Optional<Proyecto> findByEmpresa_Id(Long empresaId);
}