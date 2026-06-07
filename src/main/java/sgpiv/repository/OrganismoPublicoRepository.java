package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.model.OrganismoPublico;

import java.util.List;
import java.util.Optional;

public interface OrganismoPublicoRepository  extends JpaRepository<OrganismoPublico, Long> {

    Optional<OrganismoPublico> findById(Long id);

    List<OrganismoPublico> findAll();

    @Query("SELECT o FROM OrganismoPublico o JOIN FETCH o.usuario u WHERE u.cuit = :cuit")
    Optional<OrganismoPublico> findByUsuarioCuit(@Param("cuit") String cuit);

}
