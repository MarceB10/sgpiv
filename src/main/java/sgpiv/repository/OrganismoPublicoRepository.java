package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.OrganismoPublico;

import java.util.List;
import java.util.Optional;

public interface OrganismoPublicoRepository  extends JpaRepository<OrganismoPublico, Long> {

    Optional<OrganismoPublico> findById(Long id);

    List<OrganismoPublico> findAll();

}
