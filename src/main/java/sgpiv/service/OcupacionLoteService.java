package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.OcupacionLoteResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.model.*;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.LoteRepository;
import sgpiv.repository.OcupacionLoteRepository;
import sgpiv.repository.RepresentanteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcupacionLoteService {

    private final OcupacionLoteRepository ocupacionLoteRepository;
    private final RepresentanteRepository representanteRepository;
    private final LoteRepository loteRepository;
    private final EmpresaRepository empresaRepository;

    private final ProyectoService proyectoService;


    public List<OcupacionLoteResponseDTO> obtenerTodas() {
        List<OcupacionLote> ocupaciones = ocupacionLoteRepository.findAll();
        List<OcupacionLoteResponseDTO> resultado = new ArrayList<>();

        for (OcupacionLote ocupacion : ocupaciones) {
//            RepresentanteEmpresa rep = representanteRepository
//                    .findByEmpresa(ocupacion.getProyecto().getEmpresa())
//                    .orElse(null);
//
//            String nombre   = rep != null ? rep.getUsuario().getNombre()   : "-";
//            String apellido = rep != null ? rep.getUsuario().getApellido() : "-";
//            String cuit     = rep != null ? rep.getUsuario().getCuit()     : "-";

            resultado.add(new OcupacionLoteResponseDTO(ocupacion));
        }

        return resultado;
    }


    public void ocuparLote(Long idLote, Long idProyecto){
        Lote lote = loteRepository
                .findById(idLote)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        Proyecto proyecto = proyectoService
                .obtenerPorId(idProyecto);


        lote.setEstadoLote(EstadoLote.EN_USO);
        loteRepository.save(lote);
        proyecto.getEmpresa().setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(proyecto.getEmpresa());
        OcupacionLote ocupacionLote = new OcupacionLote(proyecto, lote, LocalDate.now());
        ocupacionLoteRepository.save(ocupacionLote);
    }


}
