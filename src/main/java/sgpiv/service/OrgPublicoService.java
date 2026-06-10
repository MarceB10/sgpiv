package sgpiv.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.OrganismoPublicoResponseDTO;
import sgpiv.model.OrganismoPublico;
import sgpiv.model.Usuario;
import sgpiv.repository.OrganismoPublicoRepository;
import sgpiv.repository.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgPublicoService {

    private final OrganismoPublicoRepository organismoPublicoRepository;
    private final UsuarioRepository usuarioRepository;

    public void crearPerfil(Usuario usuario){
        OrganismoPublico organismo = new OrganismoPublico();
        organismo.setUsuario(usuario);
        organismoPublicoRepository.save(organismo);
    }


    public List<OrganismoPublicoResponseDTO> listarTodos() {
        return organismoPublicoRepository.findAll()
                .stream()
                .map(OrganismoPublicoResponseDTO::new)
                .toList();
    }

    public OrganismoPublicoResponseDTO obtenerPorId(Long id) {
        return new OrganismoPublicoResponseDTO(
                organismoPublicoRepository.findById(id).orElseThrow()
        );
    }

    @Transactional
    public void darDeBaja(Long id) {
        OrganismoPublico organismo = organismoPublicoRepository.findById(id).orElseThrow();
        organismo.setActivo(false);

        Usuario user = organismo.getUsuario();
        user.desactivar();
        organismoPublicoRepository.save(organismo);
        usuarioRepository.save(user);
    }

}
