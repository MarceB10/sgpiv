package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.RepresentanteEmpresa;

import java.util.Optional;

public interface RepresentanteRepository extends JpaRepository<RepresentanteEmpresa, Long> {

    Optional<RepresentanteEmpresa> findByUsuario_Cuit(String cuit);

}
