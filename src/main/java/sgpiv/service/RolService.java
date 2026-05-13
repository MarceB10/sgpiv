package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.model.Rol;
import sgpiv.repository.RolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> obtenerRoles(){
        List<Rol> roles = rolRepository.findAll();
        return roles;
    }

}
