package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sgpiv.model.Empresa;
import sgpiv.model.ProveedorEmpresa;
import java.util.List;

@Repository
public interface ProveedorEmpresaRepository extends JpaRepository<ProveedorEmpresa, Long> {
    List<ProveedorEmpresa> findByEmpresa(Empresa empresa);
}