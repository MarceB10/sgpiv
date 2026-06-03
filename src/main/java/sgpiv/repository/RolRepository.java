package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.enums.NombreRol;
import sgpiv.model.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(NombreRol nombre);

}
