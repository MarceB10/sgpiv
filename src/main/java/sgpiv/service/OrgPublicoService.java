package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Usuario;
import sgpiv.repository.OrganismoPublicoRepository;

@Service
@RequiredArgsConstructor
public class OrgPublicoService {

    private final OrganismoPublicoRepository organismoPublicoRepository;

    public void crearPerfil(Usuario usuario){

    }

}
