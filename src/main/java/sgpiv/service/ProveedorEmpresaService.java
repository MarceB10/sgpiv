package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.request.ProveedorEmpresaRequestDTO;
import sgpiv.model.Empresa;
import sgpiv.model.ProveedorEmpresa;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.repository.ProveedorEmpresaRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorEmpresaService {

    private final ProveedorEmpresaRepository proveedorEmpresaRepository;
    private final RepresentanteService representanteService;

    public void agregar(ProveedorEmpresaRequestDTO dto, String cuitUsuario) {
        RepresentanteEmpresa representante =
                representanteService.buscarPorCuit(cuitUsuario);

        Empresa empresa = representante.miEmpresaEs();
        if (empresa == null) {
            throw new RuntimeException("No tenés una empresa asignada");
        }

        ProveedorEmpresa proveedor = new ProveedorEmpresa();
        proveedor.setNombre(dto.getNombre());
        proveedor.setRubro(dto.getRubro());
        proveedor.setContacto(dto.getContacto());
        proveedor.setEmpresa(empresa);
        proveedorEmpresaRepository.save(proveedor);
    }

    public List<ProveedorEmpresa> listarPorEmpresa(String cuitUsuario) {
        RepresentanteEmpresa representante =
                representanteService.buscarPorCuit(cuitUsuario);

        Empresa empresa = representante.miEmpresaEs();
        if (empresa == null) {
            throw new RuntimeException("No tenés una empresa asignada");
        }

        return proveedorEmpresaRepository.findByEmpresa(empresa);
    }

    public void eliminar(Long id) {
        proveedorEmpresaRepository.deleteById(id);
    }
}