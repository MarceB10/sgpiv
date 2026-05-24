package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.OcupacionLoteResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.model.Empresa;
import sgpiv.model.Lote;
import sgpiv.model.OcupacionLote;
import sgpiv.model.RepresentanteEmpresa;
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



    public List<OcupacionLoteResponseDTO> obtenerTodas() {
        List<OcupacionLote> ocupaciones = ocupacionLoteRepository.findAll();
        List<OcupacionLoteResponseDTO> resultado = new ArrayList<>();

        for (OcupacionLote ocupacion : ocupaciones) {
            RepresentanteEmpresa rep = representanteRepository
                    .findByEmpresa(ocupacion.getEmpresa())
                    .orElse(null);

            String nombre   = rep != null ? rep.getUsuario().getNombre()   : "-";
            String apellido = rep != null ? rep.getUsuario().getApellido() : "-";
            String cuit     = rep != null ? rep.getUsuario().getCuit()     : "-";

            resultado.add(new OcupacionLoteResponseDTO(ocupacion, nombre, apellido, cuit));
        }

        return resultado;
    }


    public void ocuparLote(Lote lote, Empresa empresa){
        lote.setEstadoLote(EstadoLote.EN_USO);
        loteRepository.save(lote);
        empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(empresa);
        OcupacionLote ocupacionLote = new OcupacionLote(empresa, lote, LocalDate.now());
        ocupacionLoteRepository.save(ocupacionLote);
    }


}
