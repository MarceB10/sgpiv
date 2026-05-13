package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.repository.ProveedorRepository;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public void crearPerfil() {

    }
}
