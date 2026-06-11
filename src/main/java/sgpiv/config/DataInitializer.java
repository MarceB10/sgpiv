package sgpiv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sgpiv.dtos.request.SolicitudProyectoRequestDTO;
import sgpiv.dtos.request.TareaSoliDTORequest;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.enums.*;
import sgpiv.model.*;
import sgpiv.repository.*;
import sgpiv.service.LoteService;
import sgpiv.service.OcupacionLoteService;
import sgpiv.service.SolicitudService;
import sgpiv.service.UsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    private final EmpresaRepository empresaRepository;
    private final LoteRepository loteRepository;
    private final InfraestructuraRepository infraestructuraRepository;

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final SolicitudProyectoRepository solicitudProyectoRepository;
    private final ProyectoRepository proyectoRepository;
    private final RepresentanteRepository representanteRepository;
    private final TareaRepository tareaRepository;
    private final TareaSolicitudRepository tareaSolicitudRepository;

    private final SolicitudOrganismoPublicoRepository solicitudOrganismoPublicoRepository;
    private final OrganismoPublicoRepository organismoPublicoRepository;

    private final SolicitudService solicitudService;

    private final LoteService loteService;
    private final OcupacionLoteService ocupacionLoteService;

    

    @Override
    public void run(String... args) {
        precargarRoles();

        precargarGerente();
        precargarUsuariosNulos();

        precargarInfraestructura();
        precargarLotes();

        precargarSolicitudRadicacionAlan();
        aceptarSolicitudRadicacionAlan();

        precargarSolicitudProyectoAlan();
        aprobarSolicitudProyectoAlan();
        convertirAlanEnRepresentante();

        //Nuevos Precargados
        precargarSolicitudRadicacionLinus();
        precargarSolicitudRadicacionTim();
        precargarSolicitudProyectoDennis();
        precargarSolicitudProyectoBjarne();
        precargarEmpresaSinLoteJames();
        precargarEmpresaConLoteGuido();

        precargarEmpresasLotesEnUso();
        precargarSolicitudYProyectoMetalurgica();
        precargarSolicitudYProyectoFrigorifico();

        cargarFlujoOrgPublico();

        System.out.println("SOLICITUDES DE RADICACION: \n");
        solicitudRadicacionRepository.findAll().forEach(sr ->
                System.out.println(
                        sr.getId() + " | " +
                                sr.getRazonSocial() + " | " +
                                sr.getEstado()
                )
        );

        System.out.println("SOLICITUDES DE PROYECTO: \n");
        solicitudProyectoRepository.findAll().forEach(sp ->
                System.out.println(
                        sp.getId() + " | " +
                                sp.getSolicitudRadicacion().getRazonSocial() + " | " +
                                sp.getEstado()
                )
        );

    }

    private void precargarRoles() {
        for (NombreRol nombreRol : NombreRol.values()) {
            if (rolRepository.findByNombre(nombreRol).isEmpty()) {
                rolRepository.save(new Rol(nombreRol));
            }
        }
    }
    private void precargarGerente() {
        if (!usuarioRepository.existsByCuit("00000000000")) {
            Rol rolGerente = rolRepository.findByNombre(NombreRol.ROL_GERENTE)
                    .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND));

            Usuario gerente = new Usuario(
                    "Martin",
                    "Lemos",
                    "gerenteSGPIV@gmail.com",
                    10000L,
                    "1234",
                    "00000000000"
            );

            gerente.getRoles().clear();
            gerente.getRoles().add(rolGerente);

            usuarioRepository.save(gerente);
        }
    }
    private void precargarUsuariosNulos() {
        crearUsuarioNuloSiNoExiste(
                "Alan",
                "Turing",
                "alan@gmail.com",
                10001L,
                "1234",
                "00000000001"
        );

        crearUsuarioNuloSiNoExiste(
                "Rodrigo",
                "Quichan",
                "ro@gmail.com",
                10002L,
                "1234",
                "00000000002"
        );

        crearUsuarioNuloSiNoExiste(
                "Grace",
                "Hopper",
                "grace@gmail.com",
                10003L,
                "1234",
                "00000000003"
        );

        crearUsuarioNuloSiNoExiste(
                "Ada",
                "Lovelace",
                "ada@gmail.com",
                10004L,
                "1234",
                "00000000004"
        );

        /// nuevas incorporaciones
        crearUsuarioNuloSiNoExiste("Linus",   "Torvalds", "linus@gmail.com",  10005L, "1234", "00000000005"); // SR pendiente
        crearUsuarioNuloSiNoExiste("Tim",     "Berners",  "tim@gmail.com",    10006L, "1234", "00000000006"); // SR requiere modificacion
        crearUsuarioNuloSiNoExiste("Dennis",  "Ritchie",  "dennis@gmail.com", 10007L, "1234", "00000000007"); // SP pendiente
        crearUsuarioNuloSiNoExiste("Bjarne",  "Stroustrup","bjarne@gmail.com",10008L, "1234", "00000000008"); // SP requiere modificacion
        crearUsuarioNuloSiNoExiste("James",   "Gosling",  "james@gmail.com",  10009L, "1234", "00000000009"); // empresa sin lote
        crearUsuarioNuloSiNoExiste("Guido",   "VanRossum","guido@gmail.com",  10010L, "1234", "00000000010"); // empresa con lote

    }
    private void crearUsuarioNuloSiNoExiste(
            String nombre,
            String apellido,
            String email,
            Long legajo,
            String password,
            String cuit
    ) {
        if (!usuarioRepository.existsByCuit(cuit)) {
            Rol rolUsuarioNulo = rolRepository.findByNombre(NombreRol.ROL_NULO)
                    .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND));

            Usuario usuario = new Usuario(
                    nombre,
                    apellido,
                    email,
                    legajo,
                    password,
                    cuit
            );

            usuario.getRoles().clear();
            usuario.getRoles().add(rolUsuarioNulo);

            usuarioRepository.save(usuario);
        }
    }

    private void precargarSolicitudRadicacionAlan() {
        if (solicitudRadicacionRepository.count() > 0) return;

        Usuario alan = usuarioRepository.findByCuit("00000000001")
                .orElseThrow(() -> new RuntimeException("Alan Turing no fue encontrado"));

        SolicitudRadicacion solicitudRadicacion = crearSRparaAlan(alan);

        solicitudRadicacionRepository.save(solicitudRadicacion);

        System.out.println("Solicitud de radicación de Alan Turing cargada");
    }

    private SolicitudRadicacion crearSRparaAlan(Usuario alan) {
        SolicitudRadicacion solicitud = new SolicitudRadicacion();

        // DATOS EMPRESA
        solicitud.setRazonSocial("Patagonia Logística");
        solicitud.setCuitEmpresa("20-12345678-9");
        solicitud.setRubro("Logística");
        solicitud.setEmailEmpresa("contacto@patagonialogistica.com");
        solicitud.setTelefonoEmpresa("2920-111111");
        solicitud.setDireccion("Ruta Provincial 1 - Sector A");
        solicitud.setIngresoBrutos("IB-123456");
        solicitud.setDescripcionBienServicio("Servicios de logística, depósito y distribución regional.");
        solicitud.setTipoIndustria("Logística y distribución");

        // DATOS PROYECTO
        solicitud.setTipoEmpresa("Nueva");
        solicitud.setObjetivoProyecto("Instalar un centro logístico dentro del parque industrial.");
        solicitud.setActividadPrincipal("Almacenamiento y distribución de mercadería.");
        solicitud.setNecesidadM2(1200D);
        solicitud.setTienePlanos(false);

        // ESTADO
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
//        solicitud.setFechaEnvio(LocalDate.now());
        solicitud.setFechaEnvio(LocalDate.of(2025, 1, 1));

        // USUARIO NULO
        solicitud.setUsuario(alan);

        return solicitud;
    }

    private void aceptarSolicitudRadicacionAlan() {
        Usuario alan = usuarioRepository.findByCuit("00000000001")
                .orElseThrow(() -> new RuntimeException("Alan Turing no fue encontrado"));

        SolicitudRadicacion solicitud = solicitudRadicacionRepository
                .findFirstByUsuarioIdAndEstadoIn(alan.getId(), List.of(EstadoSolicitud.PENDIENTE));

        if (solicitud == null) return;

        solicitud.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        solicitudRadicacionRepository.save(solicitud);

        System.out.println("Solicitud de radicación de Alan Turing aceptada");
    }

    private void precargarSolicitudProyectoAlan() {
        Usuario alan = usuarioRepository.findByCuit("00000000001")
                .orElseThrow(() -> new RuntimeException("Alan Turing no fue encontrado"));

        SolicitudRadicacion solicitudRadicacion =
                solicitudRadicacionRepository.findFirstByUsuarioIdAndEstadoIn(
                        alan.getId(),
                        List.of(EstadoSolicitud.PENDIENTE_PROYECTO)
                );

        if (solicitudRadicacion == null) {
            return;
        }

        if (solicitudRadicacion.getSolicitudProyecto() != null) {
            return;
        }

        SolicitudProyectoRequestDTO dto = new SolicitudProyectoRequestDTO();

        dto.setSolicitudRadicacionId(solicitudRadicacion.getId());

        dto.setTitulo("Centro logístico Patagonia");
        dto.setDescripcion("Proyecto para instalar un centro logístico dentro del parque industrial.");
        dto.setObjetivo("Centralizar almacenamiento, distribución y despacho de mercadería regional.");
        dto.setRubro("Logística");

        dto.setInversionEstimada(new BigDecimal("15000000.00"));

        dto.setActividadPrincipal("Almacenamiento y distribución de mercadería.");
        dto.setActividadSecundaria("Servicios de depósito para terceros.");

        dto.setPersonalAOcupar(18);
        dto.setTiempoDeRadicacion(12);

        dto.setSupCubiertaTrabajoM2(800.0);
        dto.setSupCubiertaDepositoM2(400.0);
        dto.setSupExpansionM2(300.0);

        dto.setTienePlanos(false);
        dto.setGeneraResiduos(false);
        dto.setDescripcionResiduos(null);

        dto.setProduccionEstimada("Movimiento estimado de 200 toneladas mensuales.");

        dto.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD,
                ServicioLote.AGUA,
                ServicioLote.INTERNET
        ));

        dto.setTareas(List.of(
                new TareaSoliDTORequest(
                        "Presentar documentación técnica final",
                        "Adjuntar documentación técnica definitiva del proyecto."
                ),
                new TareaSoliDTORequest(
                        "Validar planos del proyecto",
                        "El área técnica debe revisar los planos presentados."
                ),
                new TareaSoliDTORequest(
                        "Inspección inicial del lote",
                        "Verificar las condiciones iniciales del lote solicitado."
                )
        ));

        solicitudService.guardarSolicitudProyecto(dto);

        System.out.println("Solicitud de proyecto de Alan Turing cargada con tareas");
    }

    private void aprobarSolicitudProyectoAlan() {
        Usuario alan = usuarioRepository.findByCuit("00000000001")
                .orElseThrow(() -> new RuntimeException("Alan Turing no fue encontrado"));

        SolicitudRadicacion solicitudRadicacion =
                solicitudRadicacionRepository.findFirstByUsuarioIdAndEstadoIn(
                        alan.getId(), List.of(EstadoSolicitud.PENDIENTE_PROYECTO));
        if (solicitudRadicacion == null) return;

        SolicitudProyecto solicitudProyecto = solicitudRadicacion.getSolicitudProyecto();
        if (solicitudProyecto == null) return;

        if (solicitudProyecto.getEstado() != EstadoSolicitudProyecto.PENDIENTE) return;

        solicitudProyecto.setEstado(EstadoSolicitudProyecto.APROBADA);

        solicitudRadicacion.setEstado(EstadoSolicitud.APROBADA);

        solicitudProyectoRepository.save(solicitudProyecto);
        solicitudRadicacionRepository.save(solicitudRadicacion);
        System.out.println("Solicitud de proyecto de Alan Turing aprobada");
    }

    private void convertirAlanEnRepresentante() {
        Usuario alan = usuarioRepository.findByCuit("00000000001")
                .orElseThrow(() -> new RuntimeException("Alan Turing no fue encontrado"));

        SolicitudRadicacion solicitudRadicacion =
                solicitudRadicacionRepository.findFirstByUsuarioIdAndEstadoIn(
                        alan.getId(),
                        List.of(EstadoSolicitud.APROBADA)
                );

        if (solicitudRadicacion == null) {

            return;
        }

        SolicitudProyecto solicitudProyecto = solicitudRadicacion.getSolicitudProyecto();

        if (solicitudProyecto == null) {

            return;
        }

        if (solicitudProyecto.getEstado() != EstadoSolicitudProyecto.APROBADA) {
            return;
        }

        if (empresaRepository.existsByCuit(solicitudRadicacion.getCuitEmpresa())) {
            return;
        }

        Rol rolRepresentante = rolRepository.findByNombre(NombreRol.ROL_REPRESENTANTE_EMPRESA)
                .orElseThrow(() -> new RuntimeException(ROL_NOT_FOUND));

        alan.getRoles().clear();
        alan.getRoles().add(rolRepresentante);
        usuarioRepository.save(alan);

        Empresa empresa = new Empresa();
        empresa.setRazonSocial(solicitudRadicacion.getRazonSocial());
        empresa.setCuit(solicitudRadicacion.getCuitEmpresa());
        empresa.setRubro(solicitudRadicacion.getRubro());
        empresa.setEmail(solicitudRadicacion.getEmailEmpresa());
        empresa.setDireccion(solicitudRadicacion.getDireccion());
        empresa.setIngresoBrutos(solicitudRadicacion.getIngresoBrutos());
        empresa.setDescripcionBienServicio(solicitudRadicacion.getDescripcionBienServicio());
        empresa.setTipoIndustria(solicitudRadicacion.getTipoIndustria());

        empresa.setEstadoEmpresa(EstadoEmpresa.PENDIENTE_LOTE);

        Empresa empresaGuardada = empresaRepository.save(empresa);

        RepresentanteEmpresa representante = new RepresentanteEmpresa();
        representante.setUsuario(alan);
        representante.setEmpresa(empresaGuardada);

        RepresentanteEmpresa representanteGuardado = representanteRepository.save(representante);

        Proyecto proyecto = new Proyecto();

        proyecto.setTitulo(solicitudProyecto.getTitulo());
        proyecto.setDescripcion(solicitudProyecto.getDescripcion());
        proyecto.setObjetivo(solicitudProyecto.getObjetivo());
        proyecto.setRubro(solicitudProyecto.getRubro());
        proyecto.setInversionEstimada(solicitudProyecto.getInversionEstimada());
        proyecto.setActividadPrincipal(solicitudProyecto.getActividadPrincipal());
        proyecto.setActividadSecundaria(solicitudProyecto.getActividadSecundaria());
        proyecto.setPersonalAOcupar(solicitudProyecto.getPersonalAOcupar());
        proyecto.setTiempoDeRadicacion(solicitudProyecto.getTiempoDeRadicacion());
        proyecto.setSupCubiertaTrabajoM2(solicitudProyecto.getSupCubiertaTrabajoM2());
        proyecto.setSupCubiertaDepositoM2(solicitudProyecto.getSupCubiertaDepositoM2());
        proyecto.setSupExpansionM2(solicitudProyecto.getSupExpansionM2());
        proyecto.setTienePlanos(solicitudProyecto.getTienePlanos());
        proyecto.setGeneraResiduos(solicitudProyecto.isGeneraResiduos());
        proyecto.setDescripcionResiduos(solicitudProyecto.getDescripcionResiduos());
        proyecto.setProduccionEstimada(solicitudProyecto.getProduccionEstimada());

        proyecto.setServiciosRequeridos(solicitudProyecto.getServiciosRequeridos());

        proyecto.setNecesidadM2(solicitudRadicacion.getNecesidadM2());
        proyecto.setFechaInicio(LocalDate.now());

        proyecto.setEmpresa(empresaGuardada);
        proyecto.setRepresentanteEmpresa(representanteGuardado);

        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        crearTareasDesdeSolicitudProyecto(solicitudProyecto, proyectoGuardado);

        System.out.println("Alan Turing convertido en representante, empresa creada, proyecto creado y tareas asignadas");
    }

    private void crearTareasDesdeSolicitudProyecto(SolicitudProyecto solicitudProyecto, Proyecto proyecto) {
        List<TareaSolicitud> tareasSolicitud =
                tareaSolicitudRepository.findBySolicitudProyecto(solicitudProyecto);
        if (tareasSolicitud.isEmpty()) return;

        for (TareaSolicitud tareaSolicitud : tareasSolicitud) {
            Tarea tarea = new Tarea();
            tarea.setTitulo(tareaSolicitud.getTitulo());
            tarea.setDescripcion(tareaSolicitud.getDescripcion());
            tarea.setCompleta(false);
            tarea.setProyecto(proyecto);

            tareaRepository.save(tarea);
        }

        System.out.println("Tareas creadas desde la solicitud de proyecto");
    }

    private void adjudicarLoteAProyectoAlan() {
        Proyecto proyecto = proyectoRepository.findAll()
                .stream()
                .filter(p -> "Centro logístico Patagonia".equals(p.getTitulo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Proyecto de Alan no encontrado"));

        List<LoteResponseDTO> lotesDisponibles =
                loteService.obtenerLotesParaAdjudicar(proyecto.getId());

        if (lotesDisponibles.isEmpty()) {
            System.out.println("No hay lotes compatibles para adjudicar al proyecto de Alan");
            return;
        }

        LoteResponseDTO loteSeleccionado = lotesDisponibles.get(0);

//        ocupacionLoteService.ocuparLote(
//                loteSeleccionado.getId(),
//                proyecto.getId());

        ocupacionLoteService.ocuparLote(
                loteSeleccionado.getId(),
                proyecto.getId(),
                LocalDate.of(2025, 8, 10));

        System.out.println("Lote adjudicado al proyecto de Alan Turing");
    }






    private void precargarInfraestructura() {
        if (infraestructuraRepository.count() > 0) return;

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

        System.out.println("Infraestructura de prueba cargada");
    }

    private void precargarLotes() {
        if (loteRepository.count() > 0) return;

        Set<ServicioLote> servicios1 = Set.of(ServicioLote.AGUA, ServicioLote.CLOACAS,
                ServicioLote.ALUMBRADO_PUBLICO, ServicioLote.ELECTRICIDAD,
                ServicioLote.CALLES_PAVIMENTADAS, ServicioLote.DESAGUE_PLUVIAL,
                ServicioLote.GAS_NATURAL, ServicioLote.INTERNET, ServicioLote.SEGURIDAD_24HS);


        Set<ServicioLote> servicios2 = Set.of(ServicioLote.AGUA, ServicioLote.ELECTRICIDAD,
                ServicioLote.GAS_NATURAL, ServicioLote.INTERNET);

        Set<ServicioLote> servicios3 = Set.of(ServicioLote.AGUA);


        Lote loteAlan = new Lote(
                3000D,
                "Sector D - Lote 8",
                50000000f,
                "restric"
        );

        loteAlan.setEstadoLote(EstadoLote.DISPONIBLE);

        loteAlan.getServicios().add(ServicioLote.INTERNET);
        loteAlan.getServicios().add(ServicioLote.AGUA);
        loteAlan.getServicios().add(ServicioLote.ELECTRICIDAD);

        loteRepository.save(loteAlan);

        Lote lote1 = new Lote(
                1200D,
                "Sector A - Lote 1",
                4500000f,
                "Uso industrial liviano"
        );

        Lote lote2 = new Lote(
                1800D,
                "Sector A - Lote 2",
                6200000f,
                "Sin restricciones"
        );

        Lote lote3 = new Lote(
                950D,
                "Sector B - Lote 3",
                3900000f,
                "No apto almacenamiento químico"
        );

        Lote lote4 = new Lote(
                2500D,
                "Sector C - Lote 4",
                9100000f,
                "Uso industrial pesado"
        );

        Lote lote5 = new Lote(
                1500D,
                "Sector D - Lote 5",
                5400000f,
                "Altura máxima 12m"
        );

        lote1.setEstadoLote(EstadoLote.DISPONIBLE);
        lote1.setServicios(servicios1);

        lote2.setEstadoLote(EstadoLote.EN_USO);
        lote2.setFechaUso(LocalDate.now().minusMonths(6));

        lote3.setEstadoLote(EstadoLote.DISPONIBLE);
        lote3.setServicios(servicios2);

        lote4.setEstadoLote(EstadoLote.EN_USO);
        lote4.setFechaAdjudicacion(LocalDate.now().minusMonths(2));
        lote4.setServicios(servicios1);

        lote5.setEstadoLote(EstadoLote.DISPONIBLE);
        lote5.setServicios(servicios3);

        loteRepository.save(lote1);
        loteRepository.save(lote2);
        loteRepository.save(lote3);
        loteRepository.save(lote4);
        loteRepository.save(lote5);

        System.out.println("Lotes de prueba cargados");
    }


    public void cargarFlujoOrgPublico() {

        // ── ETAPA 1: USUARIO NUEVO (ROL_NULO) con solicitud PENDIENTE ──
        Usuario usuarioPendiente = new Usuario(
                "María",
                "Lopez",
                "maria.lopez@municipalidad.gob.ar",
                2920222222L,
                "1234",
                "27222222222"
        );
        usuarioRepository.save(usuarioPendiente);
        asignarRol(usuarioPendiente, NombreRol.ROL_NULO);

        SolicitudOrganismoPublico solicitudPendiente = new SolicitudOrganismoPublico();
        solicitudPendiente.setUsuario(usuarioPendiente);
        solicitudPendiente.setNombreOrganismo("Municipalidad de Viedma");
        solicitudPendiente.setTipoOrganismo("Municipal");
        solicitudPendiente.setCargoSolicitante("Directora de Industria y Comercio");
        solicitudPendiente.setMotivoAcceso("Solicitamos acceso al sistema SGPIV para realizar seguimiento " +
                "de las empresas radicadas en el Parque Industrial Viedma.");
        solicitudPendiente.setEstado(EstadoSolicitudOrganismo.PENDIENTE);
        solicitudPendiente.setFechaEnvio(LocalDate.now());
        solicitudOrganismoPublicoRepository.save(solicitudPendiente);


        // ── ETAPA 2: USUARIO CON SOLICITUD APROBADA → ROL_ORGANISMO_PUBLICO ──
        Usuario usuarioAprobado = new Usuario(
                "Roberto",
                "Fernandez",
                "roberto.fernandez@provincial.gob.ar",
                2920333333L,
                "1234",
                "20333333333"
        );
        usuarioRepository.save(usuarioAprobado);
        asignarRol(usuarioAprobado, NombreRol.ROL_ORGANISMO_PUBLICO);

        SolicitudOrganismoPublico solicitudAprobada = new SolicitudOrganismoPublico();
        solicitudAprobada.setUsuario(usuarioAprobado);
        solicitudAprobada.setNombreOrganismo("Ministerio de Producción de Río Negro");
        solicitudAprobada.setTipoOrganismo("Provincial");
        solicitudAprobada.setCargoSolicitante("Subsecretario de Industria");
        solicitudAprobada.setMotivoAcceso("El Ministerio de Producción requiere acceso para auditoría " +
                "y seguimiento de los proyectos productivos radicados en el Parque Industrial.");
        solicitudAprobada.setEstado(EstadoSolicitudOrganismo.APROBADA);
        solicitudAprobada.setFechaEnvio(LocalDate.now().minusDays(10));
        solicitudOrganismoPublicoRepository.save(solicitudAprobada);

        OrganismoPublico organismoAprobado = new OrganismoPublico();
        organismoAprobado.setUsuario(usuarioAprobado);
        organismoAprobado.setNombreOrganismo("Ministerio de Producción de Río Negro");
        organismoAprobado.setTipoOrganismo("Provincial");
        organismoAprobado.setCargoSolicitante("Subsecretario de Industria");
        organismoAprobado.setActivo(true);
        organismoPublicoRepository.save(organismoAprobado);


        // ── ETAPA 3: USUARIO CON SOLICITUD RECHAZADA → puede reintentar ──
        Usuario usuarioRechazado = new Usuario(
                "Ana",
                "Gutierrez",
                "ana.gutierrez@nacional.gob.ar",
                2920444444L,
                "1234",
                "27444444444"
        );
        usuarioRepository.save(usuarioRechazado);
        asignarRol(usuarioRechazado, NombreRol.ROL_NULO);

        SolicitudOrganismoPublico solicitudRechazada = new SolicitudOrganismoPublico();
        solicitudRechazada.setUsuario(usuarioRechazado);
        solicitudRechazada.setNombreOrganismo("AFIP Delegación Viedma");
        solicitudRechazada.setTipoOrganismo("Nacional");
        solicitudRechazada.setCargoSolicitante("Jefa de Departamento");
        solicitudRechazada.setMotivoAcceso("Solicito acceso para control fiscal de empresas radicadas.");
        solicitudRechazada.setEstado(EstadoSolicitudOrganismo.RECHAZADA);
        solicitudRechazada.setFechaEnvio(LocalDate.now().minusDays(5));
        solicitudRechazada.setMotivoRechazo("La solicitud no adjunta documentación respaldatoria suficiente. " +
                "Por favor reenviar con nota oficial y resolución que avale el acceso.");
        solicitudOrganismoPublicoRepository.save(solicitudRechazada);


        // ── ETAPA 4: ORGANISMO DADO DE BAJA ──
        Usuario usuarioBaja = new Usuario(
                "Luis",
                "Martinez",
                "luis.martinez@municipal.gob.ar",
                2920555555L,
                "1234",
                "20555555555"
        );
        usuarioBaja.desactivar();
        usuarioRepository.save(usuarioBaja);
        asignarRol(usuarioBaja, NombreRol.ROL_ORGANISMO_PUBLICO);

        SolicitudOrganismoPublico solicitudBaja = new SolicitudOrganismoPublico();
        solicitudBaja.setUsuario(usuarioBaja);
        solicitudBaja.setNombreOrganismo("Municipalidad de Guardia Mitre");
        solicitudBaja.setTipoOrganismo("Municipal");
        solicitudBaja.setCargoSolicitante("Secretario de Obras");
        solicitudBaja.setMotivoAcceso("Acceso para seguimiento de obra en el parque industrial.");
        solicitudBaja.setEstado(EstadoSolicitudOrganismo.APROBADA);
        solicitudBaja.setFechaEnvio(LocalDate.now().minusDays(30));
        solicitudOrganismoPublicoRepository.save(solicitudBaja);

        OrganismoPublico organismoBaja = new OrganismoPublico();
        organismoBaja.setUsuario(usuarioBaja);
        organismoBaja.setNombreOrganismo("Municipalidad de Guardia Mitre");
        organismoBaja.setTipoOrganismo("Municipal");
        organismoBaja.setCargoSolicitante("Secretario de Obras");
        organismoBaja.setActivo(false);
        organismoPublicoRepository.save(organismoBaja);
    }


    private void asignarRol(Usuario usuario, NombreRol nombreRol) {
        Rol rol = rolRepository.findByNombre(nombreRol).orElseThrow();
        usuario.getRoles().clear();
        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    /// Nuevos metodos relacionados a los nuevos usuarios precargados
    // ── ETAPA A: SOLICITUD DE RADICACION PENDIENTE ──
    private void precargarSolicitudRadicacionLinus() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("00000000005")) return;

        Usuario linus = usuarioRepository.findByCuit("00000000005").orElseThrow();

        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setRazonSocial("TechViedma SA");
        solicitud.setCuitEmpresa("30-55555555-5");
        solicitud.setRubro("Tecnología");
        solicitud.setEmailEmpresa("contacto@techviedma.com");
        solicitud.setTelefonoEmpresa("2920-555555");
        solicitud.setDireccion("Ruta 1 Sector B");
        solicitud.setIngresoBrutos("IB-555555");
        solicitud.setDescripcionBienServicio("Desarrollo de software y hardware industrial.");
        solicitud.setTipoIndustria("Tecnológica");
        solicitud.setTipoEmpresa("Nueva");
        solicitud.setObjetivoProyecto("Instalar una planta de desarrollo tecnológico.");
        solicitud.setActividadPrincipal("Desarrollo de software.");
        solicitud.setNecesidadM2(1800D);
        solicitud.setTienePlanos(false);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaEnvio(LocalDate.of(2025, 2, 10));
        solicitud.setUsuario(linus);
        solicitudRadicacionRepository.save(solicitud);

        System.out.println("Solicitud de radicación de Linus Torvalds cargada (PENDIENTE)");
    }

    // ── ETAPA B: SOLICITUD DE RADICACION REQUIERE MODIFICACION ──
    private void precargarSolicitudRadicacionTim() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("00000000006")) return;

        Usuario tim = usuarioRepository.findByCuit("00000000006").orElseThrow();

        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setRazonSocial("WebPatagonia SRL");
        solicitud.setCuitEmpresa("30-66666666-6");
        solicitud.setRubro("Comunicaciones");
        solicitud.setEmailEmpresa("info@webpatagonia.com");
        solicitud.setTelefonoEmpresa("2920-666666");
        solicitud.setDireccion("Av. Pioneros 100");
        solicitud.setIngresoBrutos("IB-666666");
        solicitud.setDescripcionBienServicio("Servicios de telecomunicaciones y redes.");
        solicitud.setTipoIndustria("Telecomunicaciones");
        solicitud.setTipoEmpresa("Existente");
        solicitud.setObjetivoProyecto("Ampliar infraestructura de telecomunicaciones.");
        solicitud.setActividadPrincipal("Instalación de redes.");
        solicitud.setNecesidadM2(1200D);
        solicitud.setTienePlanos(true);
        solicitud.setEstado(EstadoSolicitud.REQUIERE_MODIFICACION);
        solicitud.setMotivoRechazo("Falta adjuntar documentación técnica de la empresa existente.");
        solicitud.setFechaEnvio(LocalDate.of(2025, 2, 15));
        solicitud.setUsuario(tim);
        solicitudRadicacionRepository.save(solicitud);

        System.out.println("Solicitud de radicación de Tim Berners cargada (REQUIERE_MODIFICACION)");
    }

    // ── ETAPA C: SOLICITUD DE PROYECTO PENDIENTE ──
    private void precargarSolicitudProyectoDennis() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("00000000007")) return;

        Usuario dennis = usuarioRepository.findByCuit("00000000007").orElseThrow();

        SolicitudRadicacion sr = new SolicitudRadicacion();
        sr.setRazonSocial("CRichard Systems");
        sr.setCuitEmpresa("30-77777777-7");
        sr.setRubro("Sistemas");
        sr.setEmailEmpresa("info@crichard.com");
        sr.setTelefonoEmpresa("2920-777777");
        sr.setDireccion("Calle Unix 1");
        sr.setIngresoBrutos("IB-777777");
        sr.setDescripcionBienServicio("Desarrollo de sistemas operativos y software de bajo nivel.");
        sr.setTipoIndustria("Sistemas");
        sr.setTipoEmpresa("Nueva");
        sr.setObjetivoProyecto("Instalar centro de desarrollo de sistemas.");
        sr.setActividadPrincipal("Programación de sistemas.");
        sr.setNecesidadM2(2500D);
        sr.setTienePlanos(false);
        sr.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        sr.setFechaEnvio(LocalDate.of(2025, 3, 1));
        sr.setUsuario(dennis);
        solicitudRadicacionRepository.save(sr);

        SolicitudProyecto sp = new SolicitudProyecto();
        sp.setSolicitudRadicacion(sr);
        sp.setTitulo("Centro de Desarrollo CRichard");
        sp.setDescripcion("Centro especializado en desarrollo de sistemas de bajo nivel.");
        sp.setObjetivo("Crear el primer centro de desarrollo de sistemas en la Patagonia.");
        sp.setRubro("Sistemas");
        sp.setInversionEstimada(new BigDecimal("8000000.00"));
        sp.setActividadPrincipal("Programación de sistemas operativos.");
        sp.setActividadSecundaria("Consultoría tecnológica.");
        sp.setPersonalAOcupar(12);
        sp.setTiempoDeRadicacion(24);
        sp.setSupCubiertaTrabajoM2(600.0);
        sp.setSupCubiertaDepositoM2(200.0);
        sp.setSupExpansionM2(300.0);
        sp.setTienePlanos(false);
        sp.setGeneraResiduos(false);
        sp.setProduccionEstimada("50 proyectos anuales.");
        sp.setServiciosRequeridos(List.of(ServicioLote.ELECTRICIDAD, ServicioLote.INTERNET));
        sp.setEstado(EstadoSolicitudProyecto.PENDIENTE);
        sp.setFechaEnvio(LocalDate.of(2025, 3, 15));
        solicitudProyectoRepository.save(sp);

        TareaSolicitud t1 = new TareaSolicitud();
        t1.setTitulo("Acondicionamiento del espacio");
        t1.setDescripcion("Preparar el espacio físico para el equipo de desarrollo.");
        t1.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(t1);

        TareaSolicitud t2 = new TareaSolicitud();
        t2.setTitulo("Instalación de equipamiento");
        t2.setDescripcion("Instalar servidores y equipos de desarrollo.");
        t2.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(t2);

        System.out.println("Solicitud de proyecto de Dennis Ritchie cargada (PENDIENTE)");
    }

    // ── ETAPA D: SOLICITUD DE PROYECTO REQUIERE MODIFICACION ──
    private void precargarSolicitudProyectoBjarne() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("00000000008")) return;

        Usuario bjarne = usuarioRepository.findByCuit("00000000008").orElseThrow();

        SolicitudRadicacion sr = new SolicitudRadicacion();
        sr.setRazonSocial("CppIndustria SA");
        sr.setCuitEmpresa("30-88888888-8");
        sr.setRubro("Manufactura");
        sr.setEmailEmpresa("info@cppindustria.com");
        sr.setTelefonoEmpresa("2920-888888");
        sr.setDireccion("Sector C - Lote 12");
        sr.setIngresoBrutos("IB-888888");
        sr.setDescripcionBienServicio("Fabricación de componentes electrónicos.");
        sr.setTipoIndustria("Manufactura electrónica");
        sr.setTipoEmpresa("Existente");
        sr.setObjetivoProyecto("Ampliar línea de producción de componentes.");
        sr.setActividadPrincipal("Fabricación electrónica.");
        sr.setNecesidadM2(3000D);
        sr.setTienePlanos(true);
        sr.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        sr.setFechaEnvio(LocalDate.of(2025, 3, 5));
        sr.setUsuario(bjarne);
        solicitudRadicacionRepository.save(sr);

        SolicitudProyecto sp = new SolicitudProyecto();
        sp.setSolicitudRadicacion(sr);
        sp.setTitulo("Ampliación CppIndustria");
        sp.setDescripcion("Ampliación de la línea de producción de componentes electrónicos.");
        sp.setObjetivo("Duplicar capacidad productiva en 12 meses.");
        sp.setRubro("Manufactura");
        sp.setInversionEstimada(new BigDecimal("25000000.00"));
        sp.setActividadPrincipal("Fabricación de componentes.");
        sp.setPersonalAOcupar(30);
        sp.setTiempoDeRadicacion(12);
        sp.setSupCubiertaTrabajoM2(1500.0);
        sp.setSupCubiertaDepositoM2(500.0);
        sp.setSupExpansionM2(800.0);
        sp.setTienePlanos(true);
        sp.setGeneraResiduos(true);
        sp.setDescripcionResiduos("Residuos de soldadura y componentes electrónicos descartados.");
        sp.setProduccionEstimada("10.000 unidades mensuales.");
        sp.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.AGUA,
                ServicioLote.GAS_NATURAL, ServicioLote.INTERNET
        ));
        sp.setEstado(EstadoSolicitudProyecto.REQUIERE_MODIFICACION);
        sp.setMotivoRechazo("Falta especificar el plan de tratamiento de residuos electrónicos.");
        sp.setFechaEnvio(LocalDate.of(2025, 3, 20));
        solicitudProyectoRepository.save(sp);

        TareaSolicitud t1 = new TareaSolicitud();
        t1.setTitulo("Ampliar línea de producción");
        t1.setDescripcion("Incorporar nueva maquinaria para duplicar capacidad.");
        t1.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(t1);

        System.out.println("Solicitud de proyecto de Bjarne Stroustrup cargada (REQUIERE_MODIFICACION)");
    }

    // ── ETAPA E: EMPRESA RADICADA SIN LOTE ──
    private void precargarEmpresaSinLoteJames() {
        if (empresaRepository.existsByCuit("30-99999999-9")) return;

        Usuario james = usuarioRepository.findByCuit("00000000009").orElseThrow();

        Rol rolRepresentante = rolRepository.findByNombre(NombreRol.ROL_REPRESENTANTE_EMPRESA).orElseThrow();
        james.getRoles().clear();
        james.getRoles().add(rolRepresentante);
        usuarioRepository.save(james);

        Empresa empresa = new Empresa();
        empresa.setRazonSocial("JavaPatagonia SRL");
        empresa.setCuit("30-99999999-9");
        empresa.setRubro("Software");
        empresa.setEmail("info@javapatagonia.com");
        empresa.setDireccion("Sector A - Lote 5");
        empresa.setIngresoBrutos("IB-999999");
        empresa.setDescripcionBienServicio("Desarrollo de aplicaciones Java para industria.");
        empresa.setTipoIndustria("Software");
        empresa.setEstadoEmpresa(EstadoEmpresa.PENDIENTE_LOTE);
        Empresa empresaGuardada = empresaRepository.save(empresa);

        RepresentanteEmpresa representante = new RepresentanteEmpresa();
        representante.setUsuario(james);
        representante.setEmpresa(empresaGuardada);
        RepresentanteEmpresa representanteGuardado = representanteRepository.save(representante);

        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo("Planta JavaPatagonia");
        proyecto.setDescripcion("Centro de desarrollo Java para la industria patagónica.");
        proyecto.setObjetivo("Desarrollar soluciones Java para empresas del parque.");
        proyecto.setRubro("Software");
        proyecto.setInversionEstimada(new BigDecimal("5000000.00"));
        proyecto.setActividadPrincipal("Desarrollo de software.");
        proyecto.setPersonalAOcupar(10);
        proyecto.setTiempoDeRadicacion(12);
        proyecto.setSupCubiertaTrabajoM2(400.0);
        proyecto.setSupCubiertaDepositoM2(100.0);
        proyecto.setNecesidadM2(1200D);
        proyecto.setTienePlanos(false);
        proyecto.setGeneraResiduos(false);
        proyecto.setServiciosRequeridos(List.of(ServicioLote.ELECTRICIDAD, ServicioLote.INTERNET));
        proyecto.setEmpresa(empresaGuardada);
        proyecto.setRepresentanteEmpresa(representanteGuardado);
        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 4, 1));
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        Tarea t1 = new Tarea();
        t1.setTitulo("Instalar entorno de desarrollo");
        t1.setDescripcion("Configurar servidores y entorno Java.");
        t1.setCompleta(false);
        t1.setProyecto(proyectoGuardado);
        tareaRepository.save(t1);

        Tarea t2 = new Tarea();
        t2.setTitulo("Contratar personal");
        t2.setDescripcion("Incorporar desarrolladores Java senior y junior.");
        t2.setCompleta(true);
        t2.setProyecto(proyectoGuardado);
        tareaRepository.save(t2);

        System.out.println("Empresa JavaPatagonia (sin lote) y proyecto cargados");
    }

    // ── ETAPA F: EMPRESA RADICADA CON LOTE ──
    private void precargarEmpresaConLoteGuido() {
        if (empresaRepository.existsByCuit("30-10101010-1")) return;

        Usuario guido = usuarioRepository.findByCuit("00000000010").orElseThrow();

        Rol rolRepresentante = rolRepository.findByNombre(NombreRol.ROL_REPRESENTANTE_EMPRESA).orElseThrow();
        guido.getRoles().clear();
        guido.getRoles().add(rolRepresentante);
        usuarioRepository.save(guido);

        Empresa empresa = new Empresa();
        empresa.setRazonSocial("PythonIndustria SA");
        empresa.setCuit("30-10101010-1");
        empresa.setRubro("Automatización");
        empresa.setEmail("info@pythonindustria.com");
        empresa.setDireccion("Sector D - Lote 8");
        empresa.setIngresoBrutos("IB-101010");
        empresa.setDescripcionBienServicio("Automatización de procesos industriales con Python.");
        empresa.setTipoIndustria("Automatización");
        empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(empresa);

        RepresentanteEmpresa representante = new RepresentanteEmpresa();
        representante.setUsuario(guido);
        representante.setEmpresa(empresa);
        representanteRepository.save(representante);

        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo("Automatización PythonIndustria");
        proyecto.setDescripcion("Planta de automatización industrial usando Python y IoT.");
        proyecto.setObjetivo("Automatizar procesos industriales en el parque.");
        proyecto.setRubro("Automatización");
        proyecto.setInversionEstimada(new BigDecimal("12000000.00"));
        proyecto.setActividadPrincipal("Automatización industrial.");
        proyecto.setActividadSecundaria("Consultoría en IoT.");
        proyecto.setPersonalAOcupar(20);
        proyecto.setTiempoDeRadicacion(24);
        proyecto.setSupCubiertaTrabajoM2(900.0);
        proyecto.setSupCubiertaDepositoM2(300.0);
        proyecto.setSupExpansionM2(400.0);
        proyecto.setNecesidadM2(1800D);
        proyecto.setTienePlanos(true);
        proyecto.setGeneraResiduos(false);
        proyecto.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.INTERNET,
                ServicioLote.AGUA, ServicioLote.SEGURIDAD_24HS
        ));
        proyecto.setEmpresa(empresa);
        proyecto.setRepresentanteEmpresa(representante);
        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);
        proyecto.setFechaInicio(LocalDate.of(2025, 5, 1));
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        Tarea t1 = new Tarea();
        t1.setTitulo("Instalar sensores IoT");
        t1.setDescripcion("Colocar sensores en la línea de producción.");
        t1.setCompleta(true);
        t1.setProyecto(proyectoGuardado);
        tareaRepository.save(t1);

        Tarea t2 = new Tarea();
        t2.setTitulo("Desarrollar dashboard de monitoreo");
        t2.setDescripcion("Crear panel de control para visualizar datos en tiempo real.");
        t2.setCompleta(true);
        t2.setProyecto(proyectoGuardado);
        tareaRepository.save(t2);

        Tarea t3 = new Tarea();
        t3.setTitulo("Capacitar al personal");
        t3.setDescripcion("Entrenar operarios en el uso del sistema automatizado.");
        t3.setCompleta(false);
        t3.setProyecto(proyectoGuardado);
        tareaRepository.save(t3);

        // Buscar un lote disponible y adjudicar
        Lote lote = loteRepository.findAll().stream()
                .filter(l -> l.getEstadoLote() == EstadoLote.DISPONIBLE)
                .findFirst()
                .orElse(null);

        if (lote != null) {
            ocupacionLoteService.ocuparLote(
                    lote.getId(),
                    proyectoGuardado.getId(),
                    LocalDate.of(2025, 5, 15)
            );
            System.out.println("Lote adjudicado a PythonIndustria");

            //Actualizo estados
            empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
            proyecto.setEmpresa(empresa);
            empresaRepository.save(empresa);
            representante.setEmpresa(empresa);
            representanteRepository.save(representante);
            proyectoRepository.save(proyecto);
        }

        System.out.println("Empresa PythonIndustria (con lote) y proyecto cargados");
    }


    private void precargarEmpresasLotesEnUso() {
        if (empresaRepository.existsByCuit("30-22222222-2")) return;

        // ── EMPRESA 1 para lote2 ──
        Lote lote2 = loteRepository.findAll().stream()
                .filter(l -> "Sector A - Lote 2".equals(l.getUbicacion()))
                .findFirst().orElse(null);
        if (lote2 == null) return;

        Empresa empresa2 = new Empresa();
        empresa2.setRazonSocial("Metalúrgica Sur SA");
        empresa2.setCuit("30-22222222-2");
        empresa2.setRubro("Metalurgia");
        empresa2.setEmail("info@metalurgicasur.com");
        empresa2.setDireccion("Sector A - Lote 2");
        empresa2.setIngresoBrutos("IB-222222");
        empresa2.setDescripcionBienServicio("Fabricación de estructuras metálicas.");
        empresa2.setTipoIndustria("Metalurgia");
        empresa2.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(empresa2);

        // Usuario representante empresa2
        Usuario rep2 = new Usuario(
                "Carlos", "Mendez",
                "carlos.mendez@metalurgicasur.com",
                20111L, "1234", "20111111112"
        );

        usuarioRepository.save(rep2);

        usuarioService.asignarRol(rep2.getCuit(), NombreRol.ROL_REPRESENTANTE_EMPRESA);

        RepresentanteEmpresa representante2 = representanteRepository
                .findByUsuario(rep2).orElseThrow(() -> new RuntimeException("Usuario: " + rep2.getNombre() +"\n"
                                                                           +  "Cuit: " + rep2.getCuit() + "\n " + "NO ENCONTRADO"));
        representante2.setUsuario(rep2);
        representante2.setEmpresa(empresa2);
        representanteRepository.save(representante2);


        Proyecto proyecto2 = new Proyecto();
        proyecto2.setTitulo("Planta Metalúrgica Sur");
        proyecto2.setDescripcion("Planta de fabricación de estructuras metálicas.");
        proyecto2.setObjetivo("Producir estructuras metálicas para la región patagónica.");
        proyecto2.setRubro("Metalurgia");
        proyecto2.setInversionEstimada(new BigDecimal("18000000.00"));
        proyecto2.setActividadPrincipal("Fabricación de estructuras metálicas.");
        proyecto2.setPersonalAOcupar(25);
        proyecto2.setTiempoDeRadicacion(24);
        proyecto2.setSupCubiertaTrabajoM2(1000.0);
        proyecto2.setSupCubiertaDepositoM2(400.0);
        proyecto2.setNecesidadM2(1800D);
        proyecto2.setTienePlanos(true);
        proyecto2.setGeneraResiduos(true);
        proyecto2.setDescripcionResiduos("Virutas y residuos metálicos del proceso de corte.");
        proyecto2.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.AGUA, ServicioLote.GAS_NATURAL
        ));
        proyecto2.setEmpresa(empresa2);
        proyecto2.setRepresentanteEmpresa(representante2);
        proyecto2.setEstadoProyecto(EstadoProyecto.ACTIVO);
        proyecto2.setFechaInicio(LocalDate.now().minusMonths(6));
        proyectoRepository.save(proyecto2);

        Tarea tp2t1 = new Tarea();
        tp2t1.setTitulo("Instalar línea de corte");
        tp2t1.setDescripcion("Instalar maquinaria de corte láser.");
        tp2t1.setCompleta(true);
        tp2t1.setProyecto(proyecto2);
        tareaRepository.save(tp2t1);

        Tarea tp2t2 = new Tarea();
        tp2t2.setTitulo("Habilitación municipal");
        tp2t2.setDescripcion("Gestionar habilitación ante el municipio.");
        tp2t2.setCompleta(false);
        tp2t2.setProyecto(proyecto2);
        tareaRepository.save(tp2t2);

        List<Tarea> lista = List.of(tp2t1, tp2t2);

        proyecto2.setTareas(lista);
        proyectoRepository.save(proyecto2);

        ocupacionLoteService.ocuparLote(
                lote2.getId(),
                proyecto2.getId(),
                LocalDate.now().minusMonths(6)
        );

        System.out.println("Empresa Metalúrgica Sur adjudicada a Lote 2");

        // ── EMPRESA 2 para lote4 ──
        Lote lote4 = loteRepository.findAll().stream()
                .filter(l -> "Sector C - Lote 4".equals(l.getUbicacion()))
                .findFirst().orElse(null);
        if (lote4 == null) return;

        Empresa empresa4 = new Empresa();
        empresa4.setRazonSocial("Frigorífico Patagónico SRL");
        empresa4.setCuit("30-44444444-4");
        empresa4.setRubro("Alimenticia");
        empresa4.setEmail("info@frigopata.com");
        empresa4.setDireccion("Sector C - Lote 4");
        empresa4.setIngresoBrutos("IB-444444");
        empresa4.setDescripcionBienServicio("Procesamiento y almacenamiento de productos cárnicos.");
        empresa4.setTipoIndustria("Alimenticia");
        empresa4.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(empresa4);

        Usuario rep4 = new Usuario(
                "Sandra", "Quiroga",
                "sandra.quiroga@frigopata.com",
                20222L, "1234", "27222222223"
        );

        usuarioRepository.save(rep4);

        usuarioService.asignarRol(rep4.getCuit(), NombreRol.ROL_REPRESENTANTE_EMPRESA);

        RepresentanteEmpresa representante4 = representanteRepository
                .findByUsuario(rep4).orElseThrow(() -> new RuntimeException("Usuario: " + rep4.getNombre() +"\n"
                        +  "Cuit: " + rep4.getCuit() + "\n " + "NO ENCONTRADO"));
        representante4.setUsuario(rep4);
        representante4.setEmpresa(empresa4);
        representanteRepository.save(representante4);


        Proyecto proyecto4 = new Proyecto();
        proyecto4.setTitulo("Frigorífico Patagónico");
        proyecto4.setDescripcion("Planta de procesamiento y almacenamiento frigorífico.");
        proyecto4.setObjetivo("Proveer servicios de frío industrial para la región.");
        proyecto4.setRubro("Alimenticia");
        proyecto4.setInversionEstimada(new BigDecimal("30000000.00"));
        proyecto4.setActividadPrincipal("Procesamiento de productos cárnicos.");
        proyecto4.setActividadSecundaria("Almacenamiento frigorífico para terceros.");
        proyecto4.setPersonalAOcupar(40);
        proyecto4.setTiempoDeRadicacion(36);
        proyecto4.setSupCubiertaTrabajoM2(1800.0);
        proyecto4.setSupCubiertaDepositoM2(700.0);
        proyecto4.setSupExpansionM2(500.0);
        proyecto4.setNecesidadM2(2500D);
        proyecto4.setTienePlanos(true);
        proyecto4.setGeneraResiduos(true);
        proyecto4.setDescripcionResiduos("Residuos orgánicos del proceso de faena y desposte.");
        proyecto4.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.AGUA,
                ServicioLote.CLOACAS, ServicioLote.GAS_NATURAL, ServicioLote.SEGURIDAD_24HS
        ));
        proyecto4.setEmpresa(empresa4);
        proyecto4.setRepresentanteEmpresa(representante4);
        proyecto4.setEstadoProyecto(EstadoProyecto.ACTIVO);
        proyecto4.setFechaInicio(LocalDate.now().minusMonths(2));
        Proyecto proyecto4Guardado = proyectoRepository.save(proyecto4);

        Tarea tp4t1 = new Tarea();
        tp4t1.setTitulo("Instalar cámaras frigoríficas");
        tp4t1.setDescripcion("Montar e instalar las cámaras de frío industrial.");
        tp4t1.setCompleta(true);
        tp4t1.setProyecto(proyecto4Guardado);
        tareaRepository.save(tp4t1);

        Tarea tp4t2 = new Tarea();
        tp4t2.setTitulo("Habilitación SENASA");
        tp4t2.setDescripcion("Tramitar habilitación ante SENASA para operar.");
        tp4t2.setCompleta(false);
        tp4t2.setProyecto(proyecto4Guardado);
        tareaRepository.save(tp4t2);

        Tarea tp4t3 = new Tarea();
        tp4t3.setTitulo("Contratar personal operativo");
        tp4t3.setDescripcion("Incorporar operarios para las distintas líneas de trabajo.");
        tp4t3.setCompleta(false);
        tp4t3.setProyecto(proyecto4Guardado);
        tareaRepository.save(tp4t3);

        ocupacionLoteService.ocuparLote(
                lote4.getId(),
                proyecto4Guardado.getId(),
                LocalDate.now().minusMonths(2)
        );

        System.out.println("Empresa Frigorífico Patagónico adjudicada a Lote 4");
    }




    private void precargarSolicitudYProyectoMetalurgica() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("20111111112")) return;

        Usuario rep2 = usuarioRepository.findByCuit("20111111112").orElseThrow();
        Empresa empresa2 = empresaRepository.findByCuit("30-22222222-2").orElseThrow();
        RepresentanteEmpresa representante2 = representanteRepository.findByUsuario(rep2).orElseThrow();

        // Solicitud de radicacion APROBADA
        SolicitudRadicacion sr = new SolicitudRadicacion();
        sr.setRazonSocial("Metalúrgica Sur SA");
        sr.setCuitEmpresa("30-22222222-2");
        sr.setRubro("Metalurgia");
        sr.setEmailEmpresa("info@metalurgicasur.com");
        sr.setTelefonoEmpresa("2920-111112");
        sr.setDireccion("Sector A - Lote 2");
        sr.setIngresoBrutos("IB-222222");
        sr.setDescripcionBienServicio("Fabricación de estructuras metálicas.");
        sr.setTipoIndustria("Metalurgia");
        sr.setTipoEmpresa("Nueva");
        sr.setObjetivoProyecto("Instalar planta de fabricación de estructuras metálicas.");
        sr.setActividadPrincipal("Fabricación de estructuras metálicas.");
        sr.setNecesidadM2(1800D);
        sr.setTienePlanos(true);
        sr.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        sr.setFechaEnvio(LocalDate.of(2025, 1, 10));
        sr.setUsuario(rep2);
        solicitudRadicacionRepository.save(sr);

        // Solicitud de proyecto APROBADA
        SolicitudProyecto sp = new SolicitudProyecto();
        sp.setSolicitudRadicacion(sr);
        sp.setTitulo("Planta Metalúrgica Sur");
        sp.setDescripcion("Planta de fabricación de estructuras metálicas.");
        sp.setObjetivo("Producir estructuras metálicas para la región patagónica.");
        sp.setRubro("Metalurgia");
        sp.setInversionEstimada(new BigDecimal("18000000.00"));
        sp.setActividadPrincipal("Fabricación de estructuras metálicas.");
        sp.setPersonalAOcupar(25);
        sp.setTiempoDeRadicacion(24);
        sp.setSupCubiertaTrabajoM2(1000.0);
        sp.setSupCubiertaDepositoM2(400.0);
        sp.setSupExpansionM2(200.0);
        sp.setTienePlanos(true);
        sp.setGeneraResiduos(true);
        sp.setDescripcionResiduos("Virutas y residuos metálicos del proceso de corte.");
        sp.setProduccionEstimada("500 toneladas mensuales.");
        sp.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.AGUA, ServicioLote.GAS_NATURAL
        ));

        //Soli radicacion
        sr.setEstado(EstadoSolicitud.APROBADA);
        //Soli proyecto
        sp.setEstado(EstadoSolicitudProyecto.APROBADA);
        sp.setFechaEnvio(LocalDate.of(2025, 2, 1));

        solicitudRadicacionRepository.save(sr);
        solicitudProyectoRepository.save(sp);

        // Tareas de la solicitud
        TareaSolicitud ts1 = new TareaSolicitud();
        ts1.setTitulo("Instalar línea de corte");
        ts1.setDescripcion("Instalar maquinaria de corte láser.");
        ts1.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(ts1);

        TareaSolicitud ts2 = new TareaSolicitud();
        ts2.setTitulo("Habilitación municipal");
        ts2.setDescripcion("Gestionar habilitación ante el municipio.");
        ts2.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(ts2);

        System.out.println("Solicitud de radicación y proyecto de Metalúrgica Sur cargados");
    }

    private void precargarSolicitudYProyectoFrigorifico() {
        if (solicitudRadicacionRepository.existsByUsuarioCuit("27222222223")) return;

        Usuario rep4 = usuarioRepository.findByCuit("27222222223").orElseThrow();
        Empresa empresa4 = empresaRepository.findByCuit("30-44444444-4").orElseThrow();
        RepresentanteEmpresa representante4 = representanteRepository.findByUsuario(rep4).orElseThrow();

        // Solicitud de radicacion APROBADA
        SolicitudRadicacion sr = new SolicitudRadicacion();
        sr.setRazonSocial("Frigorífico Patagónico SRL");
        sr.setCuitEmpresa("30-44444444-4");
        sr.setRubro("Alimenticia");
        sr.setEmailEmpresa("info@frigopata.com");
        sr.setTelefonoEmpresa("2920-444444");
        sr.setDireccion("Sector C - Lote 4");
        sr.setIngresoBrutos("IB-444444");
        sr.setDescripcionBienServicio("Procesamiento y almacenamiento de productos cárnicos.");
        sr.setTipoIndustria("Alimenticia");
        sr.setTipoEmpresa("Existente");
        sr.setObjetivoProyecto("Instalar planta frigorífica en el parque industrial.");
        sr.setActividadPrincipal("Procesamiento de productos cárnicos.");
        sr.setNecesidadM2(2500D);
        sr.setTienePlanos(true);
        sr.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        sr.setFechaEnvio(LocalDate.of(2025, 1, 20));
        sr.setUsuario(rep4);
        solicitudRadicacionRepository.save(sr);

        // Solicitud de proyecto APROBADA
        SolicitudProyecto sp = new SolicitudProyecto();
        sp.setSolicitudRadicacion(sr);
        sp.setTitulo("Frigorífico Patagónico");
        sp.setDescripcion("Planta de procesamiento y almacenamiento frigorífico.");
        sp.setObjetivo("Proveer servicios de frío industrial para la región.");
        sp.setRubro("Alimenticia");
        sp.setInversionEstimada(new BigDecimal("30000000.00"));
        sp.setActividadPrincipal("Procesamiento de productos cárnicos.");
        sp.setActividadSecundaria("Almacenamiento frigorífico para terceros.");
        sp.setPersonalAOcupar(40);
        sp.setTiempoDeRadicacion(36);
        sp.setSupCubiertaTrabajoM2(1800.0);
        sp.setSupCubiertaDepositoM2(700.0);
        sp.setSupExpansionM2(500.0);
        sp.setTienePlanos(true);
        sp.setGeneraResiduos(true);
        sp.setDescripcionResiduos("Residuos orgánicos del proceso de faena y desposte.");
        sp.setProduccionEstimada("200 toneladas mensuales.");
        sp.setServiciosRequeridos(List.of(
                ServicioLote.ELECTRICIDAD, ServicioLote.AGUA,
                ServicioLote.CLOACAS, ServicioLote.GAS_NATURAL, ServicioLote.SEGURIDAD_24HS
        ));
        //Soli proyecto
        sp.setEstado(EstadoSolicitudProyecto.APROBADA);
        sp.setFechaEnvio(LocalDate.of(2025, 2, 10));
        //Soli radicacion
        sr.setEstado(EstadoSolicitud.APROBADA);

        solicitudRadicacionRepository.save(sr);
        solicitudProyectoRepository.save(sp);

        // Tareas de la solicitud
        TareaSolicitud ts1 = new TareaSolicitud();
        ts1.setTitulo("Instalar cámaras frigoríficas");
        ts1.setDescripcion("Montar e instalar las cámaras de frío industrial.");
        ts1.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(ts1);

        TareaSolicitud ts2 = new TareaSolicitud();
        ts2.setTitulo("Habilitación SENASA");
        ts2.setDescripcion("Tramitar habilitación ante SENASA para operar.");
        ts2.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(ts2);

        TareaSolicitud ts3 = new TareaSolicitud();
        ts3.setTitulo("Contratar personal operativo");
        ts3.setDescripcion("Incorporar operarios para las distintas líneas de trabajo.");
        ts3.setSolicitudProyecto(sp);
        tareaSolicitudRepository.save(ts3);

        System.out.println("Solicitud de radicación y proyecto de Frigorífico Patagónico cargados");
    }

}