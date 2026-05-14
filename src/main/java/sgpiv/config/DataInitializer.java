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
            empresa1.setRazonSocial("Patagonia Logística");
            empresa1.setCuit("20-12345678-9");
            empresa1.setRubro("Logística");
            empresa1.setEmail("contacto@patagonialogistica.com");
            empresa1.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

            Empresa empresa2 = new Empresa();
            empresa2.setRazonSocial("Premoldeados Viedma");
            empresa2.setCuit("27-98765432-1");
            empresa2.setRubro("Construcción");
            empresa2.setEmail("ventas@premoldeadosviedma.com");
            empresa2.setEstadoEmpresa(EstadoEmpresa.RADICADA);

            Empresa empresa3 = new Empresa();
            empresa3.setRazonSocial("Frigorífico Río Negro");
            empresa3.setCuit("30-45678912-3");
            empresa3.setRubro("Frigorífico");
            empresa3.setEmail("info@frigorificiorn.com");
            empresa3.setEstadoEmpresa(EstadoEmpresa.ADJUDICADA);

            Empresa empresa4 = new Empresa();
            empresa4.setRazonSocial("Aberturas del Sur");
            empresa4.setCuit("30-22223333-4");
            empresa4.setRubro("Aberturas de aluminio");
            empresa4.setEmail("contacto@aberturasdelsur.com");
            empresa4.setEstadoEmpresa(EstadoEmpresa.RADICADA);

            Empresa empresa5 = new Empresa();
            empresa5.setRazonSocial("Valle Inferior Agro");
            empresa5.setCuit("30-99887766-5");
            empresa5.setRubro("Agroindustria");
            empresa5.setEmail("administracion@vigro.com");
            empresa5.setEstadoEmpresa(EstadoEmpresa.INTERESADA);

            Empresa empresa6 = new Empresa();
            empresa6.setRazonSocial("Maderas Patagónicas");
            empresa6.setCuit("30-11112222-6");
            empresa6.setRubro("Aserradero");
            empresa6.setEmail("ventas@maderaspatagonicas.com");
            empresa6.setEstadoEmpresa(EstadoEmpresa.BAJA);

            empresaRepository.save(empresa1);
            empresaRepository.save(empresa2);
            empresaRepository.save(empresa3);
            empresaRepository.save(empresa4);
            empresaRepository.save(empresa5);
            empresaRepository.save(empresa6);

            System.out.println("Empresas de prueba cargadas");
        }
        ///


    }
}
