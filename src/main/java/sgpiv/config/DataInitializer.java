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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROL_NOT_FOUND = "El Rol no fue encontrado";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    private final EmpresaRepository empresaRepository;
    private final LoteRepository loteRepository;
    private final InfraestructuraRepository infraestructuraRepository;

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final SolicitudProyectoRepository solicitudProyectoRepository;
    private final ProyectoRepository proyectoRepository;
    private final RepresentanteRepository representanteRepository;
    private final TareaRepository tareaRepository;
    private final TareaSolicitudRepository tareaSolicitudRepository;

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
        adjudicarLoteAProyectoAlan();
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
        solicitud.setFechaEnvio(LocalDate.now());

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

        solicitudProyectoRepository.save(solicitudProyecto);

        System.out.println("Solicitud de proyecto de Alan Turing aprobada");
    }

    private void convertirAlanEnRepresentante() {
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

        ocupacionLoteService.ocuparLote(loteSeleccionado.getId(), proyecto.getId());

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