package sgpiv.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Tarea;
import sgpiv.repository.TareaRepository;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;

    @Transactional
    public void toggleCompleta(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tarea no encontrada"));
        tarea.setCompleta(!tarea.isCompleta());
        tareaRepository.save(tarea);
    }
}
