package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.ProveedorParque;

import java.util.List;

public interface ProveedorParqueRepository extends JpaRepository<ProveedorParque, Long> {
    List<ProveedorParque> findByTipoServicio(String tipoServiio);
}
