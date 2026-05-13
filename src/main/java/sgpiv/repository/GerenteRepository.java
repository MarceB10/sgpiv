package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Gerente;

import java.util.List;
import java.util.Optional;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {


    List<Gerente> findAll();

    Optional<Gerente> findById(Long id);

}
