package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.request.LoteRequestDTO;
import sgpiv.model.Lote;
import sgpiv.repository.LoteRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;


    public void definirLote(LoteRequestDTO loteDTO){

        Lote loteNuevo = new Lote(loteDTO.getSuperficie(), loteDTO.getUbicacion(), loteDTO.getPrecio(), loteDTO.getRestricciones());

        loteRepository.save(loteNuevo);
    }


    public List<LoteResponseDTO> obtenerLotesParaSolicitud(Float superficie){
        List<Lote> lotes = loteRepository
                .findLotesDisponiblesConSuperficieMinima(superficie)
                .orElse(Collections.emptyList()); //devuelvo lista vacia para mostrar un mensaje en front
        List<LoteResponseDTO> lotesDTOS = new ArrayList<>();

        for (Lote lote: lotes){
            lotesDTOS.add(new LoteResponseDTO(lote));
        }

        return lotesDTOS;
    }


    public List<LoteResponseDTO> obtenerTodosLosLotes(){
        List<LoteResponseDTO> lotesResponseDTO = new ArrayList<>();
        List<Lote> lotes = loteRepository.findAll();

        for (Lote lote: lotes){
            lotesResponseDTO.add(
                    new LoteResponseDTO(lote)
            );
        }

        return lotesResponseDTO;
    }


}
