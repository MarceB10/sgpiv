package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.repository.EmpresaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class EmpresaService {

    private final EmpresaRepository empresaRepository; //para acceso a la bd

    public Empresa registrar(Empresa empresa){
        if( empresaRepository.existsByCuit(empresa.getCuit())){
            throw new RuntimeException("Ya existe una empresa con ese CUIT");
        }

        if(empresaRepository.existsByEmail(empresa.getEmail())){
            throw new RuntimeException("Ya existe una empresa con ese mail");
        }

        empresa.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

        return empresaRepository.save(empresa);

    }

    public List<Empresa> listarTodas(){
        return empresaRepository.findAll();
    }

    public Empresa buscarPorId(Long id){
        return empresaRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }
    public List<Empresa> listarPorEstado(EstadoEmpresa estado){
        return empresaRepository.findByEstadoEmpresa(estado);
    }

    public Empresa radicar(Long id){
        Empresa empresa = buscarPorId(id);
        empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        return empresaRepository.save(empresa);
    }

    public Empresa darDeBaja(Long id){ //marca logica
        Empresa empresa = buscarPorId(id);
        empresa.setEstadoEmpresa(EstadoEmpresa.BAJA);
        return empresaRepository.save(empresa);
    }

}
