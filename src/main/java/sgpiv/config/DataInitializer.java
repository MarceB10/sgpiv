package sgpiv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.NombreRol;
import sgpiv.model.Empresa;
import sgpiv.model.Rol;
import sgpiv.model.Usuario;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.RolRepository;
import sgpiv.repository.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
/// Pruebas
    private final EmpresaRepository empresaRepository;
/// /
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

        if (!usuarioRepository.existsByCuit("00000000001")) {
            Rol rolUsuarioNulo = rolRepository.findByNombre(NombreRol.ROL_NULO)
                    .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND) );

            Usuario nulo = new Usuario(
                    "Alan",
                    "Turing",
                    "alan@gmail.com",
                    10001L,
                    "1234",
                    "00000000001"
            );

            nulo.getRoles().clear(); // saca ROL_NULO que agrega el constructor
            nulo.getRoles().add(rolUsuarioNulo);

            usuarioRepository.save(nulo);
        }

        /// Pruebas
        if (empresaRepository.count() == 0) {

            Empresa empresa1 = new Empresa();
            empresa1.setRazonSocial("Google");
            empresa1.setCuit("20-12345678-9");
            empresa1.setRubro("Tecnologia");
            empresa1.setEmail("google@gmail.com");
            empresa1.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

            Empresa empresa2 = new Empresa();
            empresa2.setRazonSocial("Microsoft");
            empresa2.setCuit("27-98765432-1");
            empresa2.setRubro("Software");
            empresa2.setEmail("microsoft@gmail.com");
            empresa2.setEstadoEmpresa(EstadoEmpresa.RADICADA);

            empresaRepository.save(empresa1);
            empresaRepository.save(empresa2);

            System.out.println("Empresas de prueba cargadas");
        }
        ///


    }
}
