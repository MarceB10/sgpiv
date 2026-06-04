package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.ProyectoResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoProyecto;
import sgpiv.model.Proyecto;
import sgpiv.repository.ProyectoRepository;

import java.util.ArrayList;
import java.util.List;

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

    public ProyectoResponseDTO obtenerProyectoDTOPorId(Long id){
        Proyecto pr = proyectoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Proyecto no encontrado"
                        ));
        return new ProyectoResponseDTO(pr);
    }

    public List<ProyectoResponseDTO> obtenerProyectosPendientesDeLote() {
        List<Proyecto> proyectos = proyectoRepository.findByEmpresaEstado(EstadoEmpresa.PENDIENTE_LOTE);
        List<ProyectoResponseDTO> dtos = new ArrayList<>();

        for (Proyecto p : proyectos){
            System.out.println(
                    "ID=" + p.getId() +
                            " ESTADO=" + p.getEstadoProyecto());
            dtos.add(
                    new ProyectoResponseDTO(p)
            );
        }
        return dtos;
    }
}