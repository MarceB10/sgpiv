package sgpiv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.NombreRol;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
/// Pruebas
    private final EmpresaRepository empresaRepository;
    private final LoteRepository loteRepository;
//    private final SolicitudRepository solicitudRepository;
/// /
    @Override
    public void run(String... args) throws Exception {
        for(NombreRol nombreRol :  NombreRol.values()){
            if (rolRepository.findByNombre(nombreRol).isEmpty()){
                rolRepository.save(new Rol(nombreRol));
            }
        }

        // crea el gerente inicial si no existe
        precargarGerente();
        precargarUsuarioNulo();

        /// Pruebas
        precargarEmpresas();
        precargarLotes();
        ///


//        if (solicitudRepository.count() == 0) {
//
//            SolicitudRadicacion s1 = new SolicitudRadicacion();
//            s1.setTipoEmpresa("Solicitud de radicación");
//            s1.setEstado(EstadoSolicitud.PENDIENTE);
//
//            SolicitudRadicacion s2 = new SolicitudRadicacion();
//            s2.setTipoEmpresa("Ampliación de nave");
//            s2.setEstado(EstadoSolicitud.APROBADA);
//
//            SolicitudRadicacion s3 = new SolicitudRadicacion();
//            s3.setTipoEmpresa("Instalación eléctrica");
//            s3.setEstado(EstadoSolicitud.EN_REVISION);
//
//            SolicitudRadicacion s4 = new SolicitudRadicacion();
//            s4.setTipoEmpresa("Conexión eléctrica");
//            s4.setEstado(EstadoSolicitud.RECHAZADA);
//
//            solicitudRepository.save(s1);
//            solicitudRepository.save(s2);
//            solicitudRepository.save(s3);
//            solicitudRepository.save(s4);
//
//            System.out.println("Solicitudes de prueba cargadas");
//        }

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

    private void precargarEmpresas() {
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
