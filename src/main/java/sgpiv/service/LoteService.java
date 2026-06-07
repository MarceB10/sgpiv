package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.request.LoteRequestDTO;
import sgpiv.enums.ServicioLote;
import sgpiv.model.Lote;
import sgpiv.model.OcupacionLote;
import sgpiv.model.Proyecto;
import sgpiv.repository.LoteRepository;
import sgpiv.repository.OcupacionLoteRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final String LOTE_NOT_FOUND = "El Lote no fue encontrado";
    private final LoteRepository loteRepository;
    private final ProyectoService proyectoService;
    private final OcupacionLoteRepository ocupacionLoteRepository;


    public void definirLote(LoteRequestDTO loteDTO){

        Lote loteNuevo = new Lote(loteDTO.getSuperficie(), loteDTO.getUbicacion(), loteDTO.getPrecio(), loteDTO.getRestricciones());
        loteNuevo.setServicios(
                loteDTO.getServicios()
        );
        loteRepository.save(loteNuevo);
    }

    public  LoteResponseDTO obtenerLoteParaAdjudicar(Long idLote){
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new RuntimeException(LOTE_NOT_FOUND));

        return new LoteResponseDTO(lote, null);
    }

    public List<LoteResponseDTO> obtenerLotesParaAdjudicar(Long idProyecto){
        Proyecto proyecto = proyectoService.obtenerPorId(idProyecto);
        Double superficie = proyecto.getNecesidadM2();

        List<Lote> lotes = loteRepository
                .findLotesDisponiblesConSuperficieMinima(superficie)
                .orElse(Collections.emptyList()); //devuelvo lista vacia para mostrar un mensaje en front
        List<LoteResponseDTO> lotesDTOS = new ArrayList<>();


        for (Lote lote: lotes){
            if (tieneServiciosRequeridos(lote, proyecto)){
            lotesDTOS.add(new LoteResponseDTO(lote, null));
            }
        }

        return lotesDTOS;
    }

    private boolean tieneServiciosRequeridos(Lote lote, Proyecto proyecto){
        return lote.getServicios().
                containsAll( proyecto.getServiciosRequeridos() );


    }


    public Lote obtenerPorId(Long id){
        return loteRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(LOTE_NOT_FOUND));

    }


    public List<LoteResponseDTO> obtenerTodosLosLotes(){
        List<LoteResponseDTO> lotesResponseDTO = new ArrayList<>();
        List<Lote> lotes = loteRepository.findAll();

        for (Lote lote : lotes){
            LocalDate fechaAdjudicacion = ocupacionLoteRepository
                    .findOcupacionActivaPorLote(lote.getId())
                    .map(OcupacionLote::getFechaInicio)
                    .orElse(null);

            LoteResponseDTO dto = new LoteResponseDTO(lote, fechaAdjudicacion);
            dto.setFechaAdjudicacion(fechaAdjudicacion);
            lotesResponseDTO.add(dto);
        }

        return lotesResponseDTO;
    }

    public LoteRequestDTO obtenerLoteParaEditar(Long id) {

        Lote lote = loteRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Lote no encontrado")
        );

        LoteRequestDTO dto = new LoteRequestDTO();

        dto.setSuperficie(lote.getSuperficie());
        dto.setUbicacion(lote.getUbicacion());
        dto.setPrecio(lote.getPrecio());
        dto.setFechaUso(lote.getFechaUso());
        dto.setFechaAdjudicacion(lote.getFechaAdjudicacion());
        dto.setRestricciones(lote.getRestricciones());
        dto.setServicios(lote.getServicios());

        return dto;
    }

    public void actualizarLote(Long id, LoteRequestDTO dto) {

        Lote lote = loteRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Lote no encontrado")
        );

        lote.setSuperficie(dto.getSuperficie());
        lote.setUbicacion(dto.getUbicacion());
        lote.setPrecio(dto.getPrecio());
        lote.setFechaUso(dto.getFechaUso());
        lote.setFechaAdjudicacion(dto.getFechaAdjudicacion());
        lote.setRestricciones(dto.getRestricciones());

        if (dto.getServicios() == null) {
            lote.setServicios(new HashSet<>());
        } else {
            lote.setServicios(dto.getServicios());
        }

        loteRepository.save(lote);
    }
}
