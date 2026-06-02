package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sgpiv.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

}
