package sgpiv.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findById(Long id);

    List<Proveedor> findAll();
}
