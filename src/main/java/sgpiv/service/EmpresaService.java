package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.request.EmpresaRequestDTO;
import sgpiv.dtos.response.EmpresaResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.repository.EmpresaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class EmpresaService {

    private final EmpresaRepository empresaRepository; //para acceso a la bd
    private final RepresentanteService representanteService;

    public EmpresaResponseDTO registrar(EmpresaRequestDTO dto, RepresentanteEmpresa representanteEmpresa){
        if( empresaRepository.existsByCuit(dto.getCuit())){
            throw new RuntimeException("Ya existe una dto con ese CUIT");
        }

        if(empresaRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Ya existe una empresa con ese CUIT");
        }

        Empresa empresa = new Empresa();
        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setCuit(dto.getCuit());
        empresa.setTelefono(dto.getTelefono());
        empresa.setIngresoBrutos(dto.getIngresoBrutos());
        empresa.setDescripcionBienServicio(dto.getDescripcionBienServicio());
        empresa.setRubro(dto.getRubro());
        empresa.setTipoIndustria(dto.getTipoIndustria());
        empresa.setEmail(dto.getEmail());
        empresa.setDireccion(dto.getDireccion());

        empresa.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

        Empresa guardada = empresaRepository.save(empresa);

        representanteService.asignarEmpresa(representanteEmpresa,empresa);//vinculamos la empresa al representante

        return new EmpresaResponseDTO(guardada);

    }

    public List<EmpresaResponseDTO> listarTodas(){
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponseDTO::new)
                .toList();
    }

    public EmpresaResponseDTO buscarPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        return new EmpresaResponseDTO(empresa);
    }
    public List<EmpresaResponseDTO> listarPorRazonSocial(String razonSocial) {
        return empresaRepository.findByRazonSocialStartingWithIgnoreCase(razonSocial)
                .stream()
                .map(EmpresaResponseDTO::new)
                .toList();

    }

    public List<EmpresaResponseDTO> listarPorEstado(EstadoEmpresa estado) {

        return empresaRepository.findByEstadoEmpresa(estado)
                .stream()
                .map(EmpresaResponseDTO::new)
                .toList();
    }

    public EmpresaResponseDTO radicar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);

        return new EmpresaResponseDTO(
                empresaRepository.save(empresa)
        );
    }

    public EmpresaResponseDTO darDeBaja(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        empresa.setEstadoEmpresa(EstadoEmpresa.BAJA);

        return new EmpresaResponseDTO(
                empresaRepository.save(empresa)
        );
    }

    public EmpresaResponseDTO buscarEmpresaDelRepresentante(String cuit) {
        RepresentanteEmpresa representante = representanteService.buscarPorCuit(cuit);

        //El error estaba en que se buscaba el representante por Empresa, y empresa no tiene al representante
        //primero hay que traer al representante, y desde ahi traer la empresa
        Empresa empresa = representante.miEmpresaEs();

        if (empresa == null) {
            throw new RuntimeException("El representante no tiene una empresa asignada");
        }

        return new EmpresaResponseDTO(empresa);
    }
}
