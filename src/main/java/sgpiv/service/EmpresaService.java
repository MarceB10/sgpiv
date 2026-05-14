package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.request.EmpresaRequestDTO;
import sgpiv.dtos.response.EmpresaResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.repository.EmpresaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class EmpresaService {

    private final EmpresaRepository empresaRepository; //para acceso a la bd

    public EmpresaResponseDTO registrar(EmpresaRequestDTO dto){
        if( empresaRepository.existsByCuit(dto.getCuit())){
            throw new RuntimeException("Ya existe una dto con ese CUIT");
        }

        if(empresaRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Ya existe una dto con ese mail");
        }

        Empresa empresa = new Empresa();

        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setCuit(dto.getCuit());
        empresa.setRubro(dto.getRubro());
        empresa.setEmail(dto.getEmail());
        empresa.setDireccion(dto.getDireccion());
        empresa.setIngresoBrutos(dto.getIngresoBrutos());
        empresa.setTipoIndustria(dto.getTipoIndustria());
        empresa.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

        Empresa guardada = empresaRepository.save(empresa);

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
        return empresaRepository.findByRazonSocialContainingIgnoreCase(razonSocial)
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
        Empresa empresa = empresaRepository.findByCuit(cuit)
                .orElseThrow(() -> new RuntimeException("No se encontró una empresa asociada a este representante"));

        return new EmpresaResponseDTO(empresa);
    }
}
