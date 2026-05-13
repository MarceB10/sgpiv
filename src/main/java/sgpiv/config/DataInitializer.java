package sgpiv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.enums.NombreRol;
import sgpiv.model.Rol;
import sgpiv.model.Usuario;
import sgpiv.repository.RolRepository;
import sgpiv.repository.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        for(NombreRol nombreRol :  NombreRol.values()){
            if (rolRepository.findByNombre(nombreRol).isEmpty()){
                rolRepository.save(new Rol(nombreRol));
            }
        }

        // crea el gerente inicial si no existe
        if (!usuarioRepository.existsByCuit("00000000000")) {
            Rol rolGerente = rolRepository.findByNombre(NombreRol.ROL_GERENTE)
                    .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND) );

            Usuario gerente = new Usuario(
                    "Martin",
                    "Lemos",
                    "gerenteSGPIV@gmail.com",
                    10000L,
                    "sgpiv1234",
                    "00000000000"
            );

            gerente.getRoles().clear(); // saca ROL_NULO que agrega el constructor
            gerente.getRoles().add(rolGerente);

            usuarioRepository.save(gerente);
        }

    }
}
