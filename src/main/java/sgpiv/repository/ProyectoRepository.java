package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Proyecto;

import java.util.List;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @Query("SELECT p FROM Proyecto p JOIN FETCH p.empresa e WHERE e.estadoEmpresa = :estado")
    List<Proyecto> findByEmpresaEstado(@Param("estado") EstadoEmpresa estado);

}