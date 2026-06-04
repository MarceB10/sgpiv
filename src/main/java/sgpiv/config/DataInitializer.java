package sgpiv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.enums.*;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
/// Pruebas
    private final EmpresaRepository empresaRepository;
    private final LoteRepository loteRepository;
    private final InfraestructuraRepository infraestructuraRepository;
//    private final SolicitudRepository solicitudRadicacionRepository;
/// /
    @Override
    public void run(String... args) throws Exception {
        for(NombreRol nombreRol :  NombreRol.values()){
            if (rolRepository.findByNombre(nombreRol).isEmpty()){
                rolRepository.save(new Rol(nombreRol));
            }
        }

//        Infraestructura precargada
        if (infraestructuraRepository.count() > 0) {
            return;
        }

        infraestructuraRepository.save(new Infraestructura(
                "Estación Transformadora 1",
                TipoInfraestructura.ELECTRICA,
                EstadoInfraestructura.OPERATIVA,
                "Sector A",
                "Ingreso principal",
                "Estación encargada de distribuir energía eléctrica a los lotes del Sector A.",
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 8, 10)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Red de Agua Norte",
                TipoInfraestructura.AGUA_POTABLE,
                EstadoInfraestructura.OPERATIVA,
                "Sector B",
                "Zona norte del parque",
                "Red de distribución de agua potable para empresas ubicadas en el Sector B.",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 8, 5)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Alumbrado Calle 3",
                TipoInfraestructura.ALUMBRADO_PUBLICO,
                EstadoInfraestructura.EN_MANTENIMIENTO,
                "Sector A",
                "Calle interna 3",
                "Sistema de iluminación pública sobre la calle interna número 3.",
                LocalDate.of(2025, 5, 15),
                LocalDate.of(2025, 7, 15)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Red de Gas Principal",
                TipoInfraestructura.GAS_NATURAL,
                EstadoInfraestructura.OPERATIVA,
                "Sector D",
                "Troncal principal del parque",
                "Red principal de distribución de gas natural hacia los lotes industriales.",
                LocalDate.of(2025, 4, 20),
                LocalDate.of(2025, 7, 20)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Fibra Óptica Sector B",
                TipoInfraestructura.TELECOMUNICACIONES,
                EstadoInfraestructura.EN_MANTENIMIENTO,
                "Sector B",
                "Canalización subterránea Sector B",
                "Tendido de fibra óptica para conexión a internet de las empresas.",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 7, 1)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Planta de Tratamiento",
                TipoInfraestructura.AGUA_RESIDUAL,
                EstadoInfraestructura.OPERATIVA,
                "Sector C",
                "Zona posterior del parque",
                "Planta destinada al tratamiento de líquidos residuales industriales.",
                LocalDate.of(2025, 4, 12),
                LocalDate.of(2025, 7, 12)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Red Cloacal Sector C",
                TipoInfraestructura.RED_CLOACAL,
                EstadoInfraestructura.OPERATIVA,
                "Sector C",
                "Calles internas del Sector C",
                "Sistema cloacal que conecta los lotes del sector con la planta de tratamiento.",
                LocalDate.of(2025, 4, 18),
                LocalDate.of(2025, 7, 18)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Calle Interna Principal",
                TipoInfraestructura.VIAL,
                EstadoInfraestructura.REQUIERE_MANTENIMIENTO,
                "Sector General",
                "Acceso principal hasta rotonda central",
                "Calle principal de circulación interna del parque industrial.",
                LocalDate.of(2025, 3, 25),
                LocalDate.of(2025, 6, 25)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Cerco Perimetral Este",
                TipoInfraestructura.SEGURIDAD,
                EstadoInfraestructura.OPERATIVA,
                "Sector Este",
                "Límite este del parque",
                "Cerramiento perimetral destinado al control y seguridad del predio.",
                LocalDate.of(2025, 4, 8),
                LocalDate.of(2025, 7, 8)
        ));

        infraestructuraRepository.save(new Infraestructura(
                "Cámara Acceso Principal",
                TipoInfraestructura.SEGURIDAD,
                EstadoInfraestructura.FUERA_DE_SERVICIO,
                "Ingreso",
                "Portón de acceso principal",
                "Cámara de vigilancia ubicada en el ingreso principal del parque.",
                LocalDate.of(2025, 4, 1),
                null
        ));

        // crea el gerente inicial si no existe
        precargarGerente();
        precargarUsuarioNulo();

        /// Pruebas
        var empresas = precargarEmpresas();
        precargarLotes();
//        precargarSolicitudes(empresas);
        ///
    }

//    private void precargarSolicitudes(List<Empresa> empresas) {
//        if(solicitudRadicacionRepository.count() == 0){
//
//            Empresa empresa1 = empresas.get(0);
//            SolicitudRadicacion s1 = new SolicitudRadicacion();
//
//            s1.setRazonSocial(empresa1.getRazonSocial());
//            s1.setCuitEmpresa(empresa1.getCuit());
//            s1.setRubro(empresa1.getRubro());
//            s1.setTipoIndustria(empresa1.getTipoIndustria());
//            s1.setEmailEmpresa(empresa1.getEmail());
//            s1.setDireccion(empresa1.getDireccion());
//            s1.setDescripcionBienServicio(empresa1.getDescripcionBienServicio());
//
//            solicitudRadicacionRepository.save(s1);
//        }
//    }

    private List<Empresa> precargarEmpresas() {
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
        return empresaRepository.findAll();
    }

    private void precargarUsuarioNulo() {
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
        if (!usuarioRepository.existsByCuit("00000000002")) {
            Rol rolUsuarioNulo = rolRepository.findByNombre(NombreRol.ROL_NULO)
                    .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND) );

            Usuario nulo = new Usuario(
                    "Rodrigo",
                    "Quichan",
                    "ro@gmail.com",
                    10002L,
                    "1234",
                    "00000000002"
            );

            nulo.getRoles().clear(); // saca ROL_NULO que agrega el constructor
            nulo.getRoles().add(rolUsuarioNulo);

            usuarioRepository.save(nulo);
        }
    }

    private void precargarGerente() {
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

    private void precargarLotes() {
        if (loteRepository.count() == 0) {
            Lote lote1 = new Lote(
                    1200f,
                    "Sector A - Lote 1",
                    4500000f,
                    "Uso industrial liviano"
            );
            Lote lote2 = new Lote(
                    1800f,
                    "Sector A - Lote 2",
                    6200000f,
                    "Sin restricciones"
            );
            Lote lote3 = new Lote(
                    950f,
                    "Sector B - Lote 3",
                    3900000f,
                    "No apto almacenamiento químico"
            );
            Lote lote4 = new Lote(
                    2500f,
                    "Sector C - Lote 4",
                    9100000f,
                    "Uso industrial pesado"
            );
            Lote lote5 = new Lote(
                    1500f,
                    "Sector D - Lote 5",
                    5400000f,
                    "Altura máxima 12m"
            );

            // estados distintos para prueba

            lote1.setEstadoLote(EstadoLote.DISPONIBLE);

            lote2.setEstadoLote(EstadoLote.EN_USO);
            lote2.setFechaUso(LocalDate.now().minusMonths(6));

            lote3.setEstadoLote(EstadoLote.DISPONIBLE);

            lote4.setEstadoLote(EstadoLote.EN_USO);
            lote4.setFechaAdjudicacion(LocalDate.now().minusMonths(2));

            lote5.setEstadoLote(EstadoLote.DISPONIBLE);

            loteRepository.save(lote1);
            loteRepository.save(lote2);
            loteRepository.save(lote3);
            loteRepository.save(lote4);
            loteRepository.save(lote5);

            System.out.println("Lotes de prueba cargados");
        }
    }
}
