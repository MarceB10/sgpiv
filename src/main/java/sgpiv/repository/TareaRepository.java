package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sgpiv.model.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
}