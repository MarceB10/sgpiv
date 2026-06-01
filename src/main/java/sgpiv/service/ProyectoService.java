package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Proyecto;
import sgpiv.repository.ProyectoRepository;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public Proyecto obtenerPorId(Long id){
        return proyectoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Proyecto no encontrado"
                        ));
    }
}