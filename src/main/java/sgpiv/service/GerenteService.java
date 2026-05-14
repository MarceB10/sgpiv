package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.enums.NombreRol;
import sgpiv.model.Gerente;
import sgpiv.model.Usuario;
import sgpiv.repository.*;


@Service
@RequiredArgsConstructor
public class GerenteService {

    private final GerenteRepository gerenteRepository;
    private final UsuarioService usuarioService;



    public void crearPerfil(Usuario usuario){
        Gerente gerente = new Gerente(usuario);
        gerenteRepository.save(gerente);
    }

    public void asignarRol(String cuit, NombreRol rol){
        usuarioService.asignarRol(cuit, rol);
    }

}
