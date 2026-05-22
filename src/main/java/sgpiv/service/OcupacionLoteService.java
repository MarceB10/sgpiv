package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Empresa;
import sgpiv.model.Lote;
import sgpiv.model.OcupacionLote;
import sgpiv.repository.OcupacionLoteRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OcupacionLoteService {

    private final OcupacionLoteRepository ocupacionLoteRepository;


    public void ocuparLote(Lote lote, Empresa empresa){
        OcupacionLote ocupacionLote = new OcupacionLote(empresa, lote, LocalDate.now());
        ocupacionLoteRepository.save(ocupacionLote);
    }


}
