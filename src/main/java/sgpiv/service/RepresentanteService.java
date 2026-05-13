package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Empresa;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.model.Usuario;
import sgpiv.repository.RepresentanteRepository;

@Service
@RequiredArgsConstructor
public class RepresentanteService {

    private final RepresentanteRepository representanteRepository;

    public void crearPerfil(Usuario usuario){
        RepresentanteEmpresa representante = new RepresentanteEmpresa(usuario);
        representanteRepository.save(representante);
    }

    public void asignarEmpresa(RepresentanteEmpresa representante, Empresa empresa){
       representante.asignarEmpresa(empresa);
       representanteRepository.save(representante);
    }

}
