package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.model.Empresa;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("""
        SELECT e FROM Empresa e
        WHERE LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(e.cuit) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(e.rubro) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(e.email) LIKE LOWER(CONCAT('%', :texto, '%'))
        OR LOWER(CAST(e.estadoEmpresa AS string)) LIKE LOWER(CONCAT('%', :texto, '%'))
    """)
    List<Empresa> buscarEmpresas(@Param("texto") String texto);
}