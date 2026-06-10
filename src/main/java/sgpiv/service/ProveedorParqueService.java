package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.request.ProveedorParqueRequestDTO;
import sgpiv.model.ProveedorParque;
import sgpiv.repository.ProveedorParqueRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProveedorParqueService {

    private final ProveedorParqueRepository proveedorParqueRepository;

    public void agregar(ProveedorParqueRequestDTO dto) {
        ProveedorParque proveedor = new ProveedorParque();
        proveedor.setNombre(dto.getNombre());
        proveedor.setTipoServicio(dto.getTipoServicio());
        proveedor.setContacto(dto.getContacto());
        proveedor.setCompartido(dto.isCompartido());
        proveedorParqueRepository.save(proveedor);
    }

    public List<ProveedorParque> listarTodos() {
        return proveedorParqueRepository.findAll();
    }

    public void eliminar(Long id) {
        proveedorParqueRepository.deleteById(id);
    }
}