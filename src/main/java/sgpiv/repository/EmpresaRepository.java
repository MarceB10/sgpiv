package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCuit(String cuit);
    boolean existsByCuit(String cuit);
    boolean existsByEmail(String email);
    List<Empresa> findByEstadoEmpresa(EstadoEmpresa estado);
    List<Empresa> findByRazonSocialStartingWithIgnoreCase(String razonSocial);
    List<Empresa> findByRazonSocialStartingWithIgnoreCaseAndEstadoEmpresa(
            String razonSocial, EstadoEmpresa estadoEmpresa
    );

}
