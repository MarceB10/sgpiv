package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.request.SolicitudProyectoRequestDTO;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.dtos.request.TareaSoliDTORequest;
import sgpiv.dtos.response.ProyectoResponseDTO;
import sgpiv.dtos.response.SolicitudProyectoResponseDTO;
import sgpiv.dtos.response.SolicitudResponseDTO;
import sgpiv.enums.*;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final NotificacionService notificacionService;

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final SolicitudProyectoRepository solicitudProyectoRepository;

    private final UsuarioRepository usuarioRepository;

    private final OcupacionLoteService ocupacionLoteService;

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final RepresentanteRepository representanteRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final RepresentanteService representanteService;
    private final LoteService loteService;


    public void enviarSolicitudInicial(SolicitudRequestDTO dto, String cuitUsuario) {

        Usuario usuario = usuarioRepository.findByCuit(cuitUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SolicitudRadicacion solicitudActiva =
                obtenerSolicitudActiva(cuitUsuario);

        SolicitudRadicacion solicitud;

        // SI ESTA EN MODIFICACION → EDITAR LA EXISTENTE
        if (solicitudActiva != null &&
                solicitudActiva.getEstado() == EstadoSolicitud.REQUIERE_MODIFICACION) {

            solicitud = solicitudActiva;

        }
        // SI YA TIENE OTRA ACTIVA → BLOQUEAR
        else if (solicitudActiva != null) {

            throw new RuntimeException("Ya tenés una solicitud activa.");

        }
        // SI NO EXISTE → CREAR NUEVA
        else {

            solicitud = new SolicitudRadicacion();
            solicitud.setUsuario(usuario);
            solicitud.setFechaEnvio(LocalDate.now());

        }

        // DATOS EMPRESA
        solicitud.setRazonSocial(dto.getRazonSocial());
        solicitud.setCuitEmpresa(dto.getCuitEmpresa());
        solicitud.setRubro(dto.getRubro());
        solicitud.setEmailEmpresa(dto.getEmailEmpresa());
        solicitud.setTelefonoEmpresa(dto.getTelefonoEmpresa());
        solicitud.setDireccion(dto.getDireccion());
        solicitud.setIngresoBrutos(dto.getIngresoBrutos());
        solicitud.setDescripcionBienServicio(dto.getDescripcionBienServicio());
        solicitud.setTipoIndustria(dto.getTipoIndustria());

        // DATOS PROYECTO
        solicitud.setTipoEmpresa(dto.getTipoEmpresa());
        solicitud.setObjetivoProyecto(dto.getObjetivoProyecto());
        solicitud.setActividadPrincipal(dto.getActividadPrincipal());
        solicitud.setNecesidadM2(dto.getNecesidadM2());
        solicitud.setTienePlanos(dto.getTienePlanos());

        // RESETEAR ESTADO
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        // LIMPIAR MOTIVO ANTERIOR
        solicitud.setMotivoRechazo(null);

        //NOTIFICACION A GERENTE
        List<Usuario> gerentes = usuarioRepository.findByRol(NombreRol.ROL_GERENTE);
        for (Usuario gerente : gerentes) {
            notificacionService.crearNotificacion(
                    "Nueva solicitud de radicación de: " + solicitud.getRazonSocial(),
                    gerente
            );
        }

        notificacionService.crearNotificacion(
                "Tu solicitud fue enviada para la evaluacion de la gerencia del parque industrial",
                usuario
        );

        solicitudRadicacionRepository.save(solicitud);
    }

    @Transactional
    public void aprobarSolicitudPrimeraParte(Long id) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        // Cambiar estado a pendiente_proyecto
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);

//        Usuario usuario = usuarioRepository
//                .findByCuit(solicitud.getUsuario().getCuit())
//                .orElseThrow(() -> new RuntimeException("Usuario No encontrado"));

//        System.out.println(">>> Creando notificación para: " + usuario.getCuit());

        solicitudRadicacionRepository.save(solicitud);

        try {
            notificacionService.crearNotificacion(
                    "Tu solicitud de radicación fue aprobada. Ahora debes presentar un proyecto",
                    solicitud.getUsuario()

            );
            System.out.println(">>> Notificación creada OK");

        }catch (Exception e){
            System.out.println(">>> ERROR al crear notificación: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void guardarSolicitudProyecto(SolicitudProyectoRequestDTO dto){
        if (dto.getGeneraResiduos() &&
                (dto.getDescripcionResiduos() == null || dto.getDescripcionResiduos().isBlank())) {
            throw new IllegalArgumentException("Debe describir los residuos que genera");
        }

        SolicitudProyecto solicitud = new SolicitudProyecto();
        solicitud.setTitulo(dto.getTitulo());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setObjetivo(dto.getObjetivo());
        solicitud.setRubro(dto.getRubro());
        solicitud.setActividadPrincipal(dto.getActividadPrincipal());
        solicitud.setActividadSecundaria(dto.getActividadSecundaria());
        solicitud.setDescripcionResiduos(dto.getDescripcionResiduos());
        solicitud.setGeneraResiduos(dto.getGeneraResiduos());
        solicitud.setInversionEstimada(dto.getInversionEstimada());
        solicitud.setPersonalAOcupar(dto.getPersonalAOcupar());
        solicitud.setProduccionEstimada(dto.getProduccionEstimada());
        solicitud.setServiciosRequeridos(dto.getServiciosRequeridos());
        solicitud.setSupCubiertaDepositoM2(dto.getSupCubiertaDepositoM2());
        solicitud.setSupCubiertaTrabajoM2(dto.getSupCubiertaTrabajoM2());
        solicitud.setSupExpansionM2(dto.getSupExpansionM2());
        solicitud.setTienePlanos(dto.getTienePlanos());
        solicitud.setTiempoDeRadicacion(dto.getTiempoDeRadicacion());

        List<TareaSolicitud> ts = new ArrayList<>();

        for (TareaSoliDTORequest tareaDto: dto.getTareas()){
            ts.add(new TareaSolicitud(
                    tareaDto.getTitulo(),
                    tareaDto.getDescripcion(),
                    solicitud
            ));
        }

        solicitud.setTareas(ts);
        SolicitudRadicacion soliRadicacion = solicitudRadicacionRepository
                .findById(dto.getSolicitudRadicacionId())
                        .orElseThrow(() -> new RuntimeException("Solicitud No encontrada"));

        solicitud.setSolicitudRadicacion(soliRadicacion);

        solicitud.setEstado(EstadoSolicitudProyecto.PENDIENTE);


        //NOTIFICACION A GERENTE
        List<Usuario> gerentes = usuarioRepository.findByRol(NombreRol.ROL_GERENTE);
        for (Usuario gerente : gerentes) {
            notificacionService.crearNotificacion(
                    "Nueva solicitud de Proyecto de: " + solicitud.getSolicitudRadicacion().getRazonSocial(),
                    gerente
            );
        }


        solicitudProyectoRepository.save(solicitud);
        soliRadicacion.setSolicitudProyecto(solicitud);
        solicitudRadicacionRepository.save(soliRadicacion);
    }

    public void aprobarSolicitudProyecto(Long solicitudProyectoId){
        SolicitudProyecto sp = solicitudProyectoRepository
                .findByIdConTareas(solicitudProyectoId).orElseThrow();

        Usuario usuario = sp.getSolicitudRadicacion().getUsuario();

        // 1. Crear empresa desde la solicitud de radicacion
        Empresa empresa = new Empresa();
        empresa.setRazonSocial(sp.getSolicitudRadicacion().getRazonSocial());
        empresa.setTelefono(Long.valueOf(sp.getSolicitudRadicacion().getTelefonoEmpresa()));
        empresa.setIngresoBrutos(sp.getSolicitudRadicacion().getIngresoBrutos());
        empresa.setDescripcionBienServicio(sp.getSolicitudRadicacion().getDescripcionBienServicio());
        empresa.setCuit(sp.getSolicitudRadicacion().getCuitEmpresa());
        empresa.setRubro(sp.getSolicitudRadicacion().getRubro());
        empresa.setTipoIndustria(sp.getSolicitudRadicacion().getTipoEmpresa());
        empresa.setEmail(sp.getSolicitudRadicacion().getEmailEmpresa());
        empresa.setTipoIndustria(sp.getSolicitudRadicacion().getTipoIndustria());
        empresa.setDireccion(sp.getSolicitudRadicacion().getDireccion());
        empresa.setEstadoEmpresa(EstadoEmpresa.PENDIENTE_LOTE);

        empresa = empresaRepository.save(empresa);

        // 2. Dar rol representante al usuario y asociarlo a la empresa
        usuarioService.asignarRol(usuario.getCuit(), NombreRol.ROL_REPRESENTANTE_EMPRESA);
        usuario = usuarioRepository
                .findByCuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario No encontrado"));
        usuarioRepository.save(usuario);

        RepresentanteEmpresa representanteEmpresa = representanteService.buscarPorCuit(usuario.getCuit());
        representanteEmpresa.setEmpresa(empresa);
        representanteRepository.save(representanteEmpresa);

        // 3. Crear proyecto copiando todos los campos de SolicitudProyecto
        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(sp.getTitulo());
        proyecto.setDescripcion(sp.getDescripcion());
        proyecto.setObjetivo(sp.getObjetivo());
        proyecto.setRubro(sp.getRubro());
        proyecto.setInversionEstimada(sp.getInversionEstimada());
        proyecto.setActividadPrincipal(sp.getActividadPrincipal());
        proyecto.setActividadSecundaria(sp.getActividadSecundaria());
        proyecto.setPersonalAOcupar(sp.getPersonalAOcupar());
        proyecto.setTiempoDeRadicacion(sp.getTiempoDeRadicacion());
        proyecto.setSupCubiertaTrabajoM2(sp.getSupCubiertaTrabajoM2());
        proyecto.setSupCubiertaDepositoM2(sp.getSupCubiertaDepositoM2());
        proyecto.setSupExpansionM2(sp.getSupExpansionM2());
        proyecto.setTienePlanos(sp.getTienePlanos());
        proyecto.setGeneraResiduos(sp.isGeneraResiduos());
        proyecto.setDescripcionResiduos(sp.getDescripcionResiduos());
        proyecto.setProduccionEstimada(sp.getProduccionEstimada());
        proyecto.setServiciosRequeridos(sp.getServiciosRequeridos());
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setEmpresa(empresa);
        proyecto.setRepresentanteEmpresa(representanteEmpresa);

        proyecto.setNecesidadM2(sp.getSolicitudRadicacion().getNecesidadM2());

        proyecto.setServiciosRequeridos(
                new ArrayList<>(sp.getServiciosRequeridos())
        );
        proyecto = proyectoRepository.save(proyecto);


        // 4. Convertir TareaSolicitud → Tarea del proyecto
        for (TareaSolicitud ts : sp.getTareas()) {
            Tarea tarea = new Tarea();
            tarea.setTitulo(ts.getTitulo());
            tarea.setDescripcion(ts.getDescripcion());
            tarea.setCompleta(false);
            tarea.setProyecto(proyecto);
            tareaRepository.save(tarea);
        }

        // 5. Actualizar estado
        sp.setEstado(EstadoSolicitudProyecto.APROBADA);
        solicitudProyectoRepository.save(sp);

        notificacionService.crearNotificacion(
                "Tu solicitud fue Aceptada. ¡Bienvenido al Parque Industrial de Viedma!",
                usuario
        );

    }

    public SolicitudProyecto obtenerSolicitudProyecto(String cuit){
        Usuario usuario = usuarioRepository.findByCuit(cuit)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        SolicitudRadicacion solicitudRadicacion = solicitudRadicacionRepository
                        .findFirstByUsuarioIdAndEstadoIn(usuario.getId(),
                                List.of(EstadoSolicitud.PENDIENTE_PROYECTO, EstadoSolicitud.APROBADA)
                        );

        if(solicitudRadicacion == null) return null;
        return solicitudRadicacion.getSolicitudProyecto();
    }


    public void rechazar(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitudRadicacionRepository.save(solicitud);

        notificacionService.crearNotificacion(
                "Tu solicitud de radicación fue rechazada. Motivo: " + motivo,
                solicitud.getUsuario()
        );
        // Si se rechaza se puede desactivar el usuario
//        Usuario usuario = solicitud.getUsuario();
//        usuario.desactivar();
//        usuarioRepository.save(usuario);
    }

    public SolicitudRadicacion obtenerSolicitudActiva(String cuit){

        Usuario usuario = usuarioRepository.findByCuit(cuit)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return solicitudRadicacionRepository
                .findFirstByUsuarioIdAndEstadoIn(
                        usuario.getId(),
                        List.of(
                                EstadoSolicitud.PENDIENTE,
                                EstadoSolicitud.PENDIENTE_PROYECTO,
                                EstadoSolicitud.EN_REVISION,
                                EstadoSolicitud.APROBADA,
                                EstadoSolicitud.REQUIERE_MODIFICACION,
                                EstadoSolicitud.RECHAZADA
                        )
                );
    }

    public List<SolicitudRadicacion> listarPendientes(){
        return solicitudRadicacionRepository
                .findByEstado(EstadoSolicitud.PENDIENTE);
    }

    public SolicitudResponseDTO obtenerPorId(Long id) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        return new SolicitudResponseDTO(solicitud);
    }

    public void requiereModificacion(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.REQUIERE_MODIFICACION);
        solicitud.setMotivoRechazo(motivo);
        notificacionService.crearNotificacion(
                "Tu solicitud requiere modificaciones. Motivo: " + motivo,
                solicitud.getUsuario()
        );
        solicitudRadicacionRepository.save(solicitud);
    }

    public SolicitudRequestDTO toRequestDTO(SolicitudRadicacion solicitud) {
        SolicitudRequestDTO dto = new SolicitudRequestDTO();

        // Datos empresa
        dto.setRazonSocial(solicitud.getRazonSocial());
        dto.setCuitEmpresa(solicitud.getCuitEmpresa());
        dto.setIngresoBrutos(solicitud.getIngresoBrutos());
        dto.setRubro(solicitud.getRubro());
        dto.setTipoIndustria(solicitud.getTipoIndustria());
        dto.setEmailEmpresa(solicitud.getEmailEmpresa());
        dto.setTelefonoEmpresa(solicitud.getTelefonoEmpresa());
        dto.setDireccion(solicitud.getDireccion());
        dto.setDescripcionBienServicio(solicitud.getDescripcionBienServicio());

        // Datos proyecto
        dto.setTipoEmpresa(solicitud.getTipoEmpresa());
        dto.setObjetivoProyecto(solicitud.getObjetivoProyecto());
        dto.setActividadPrincipal(solicitud.getActividadPrincipal());
        dto.setNecesidadM2(solicitud.getNecesidadM2());
        dto.setTienePlanos(solicitud.getTienePlanos());

        return dto;
    }

    // ===== MÉTODOS PARA EL GERENTE =====

    public List<SolicitudRadicacion> listarSolicitudesInicialesPendientes() {
        return solicitudRadicacionRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    public void aceptarSolicitudInicial(Long solicitudId) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("Solo se pueden aceptar solicitudes en estado PENDIENTE");
        }
        notificacionService.crearNotificacion(
                "Tu solicitud de radicación fue aprobada. Ahora debes presentar una solicitud de Proyecto",
                solicitud.getUsuario()

        );

        solicitud.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        solicitudRadicacionRepository.save(solicitud);
    }

    public void rechazarSolicitudInicial(Long solicitudId, String motivo) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.REQUIERE_MODIFICACION);
        solicitud.setMotivoRechazo(motivo);
        solicitudRadicacionRepository.save(solicitud);
    }

    public SolicitudResponseDTO obtenerSolicitudRadicacionAprobadaPrimerParte(String cuit) {
        Usuario user = usuarioRepository
                .findByCuit(cuit)
                .orElseThrow(() -> new RuntimeException("Usuario No encontrado"));

        List<SolicitudRadicacion> solicitudes = solicitudRadicacionRepository.findByUsuario(user);

        SolicitudRadicacion actual = solicitudes.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE_PROYECTO)
                .findFirst()
                .orElse(null);

        return new SolicitudResponseDTO(actual);
    }

    public void rechazarSolicitudProyecto(Long id, String motivo) {
        SolicitudProyecto sp = solicitudProyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        sp.setEstado(EstadoSolicitudProyecto.RECHAZADA);
        sp.setMotivoRechazo(motivo);
        solicitudProyectoRepository.save(sp);
    }

    public void requiereModificacionProyecto(Long id, String motivo) {
        SolicitudProyecto sp = solicitudProyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        sp.setEstado(EstadoSolicitudProyecto.REQUIERE_MODIFICACION);
        sp.setMotivoRechazo(motivo);
        solicitudProyectoRepository.save(sp);
    }

    public List<SolicitudProyecto> listarTodosProyectos() {
        return solicitudProyectoRepository.findAll();
    }
    public SolicitudProyecto obtenerProyectoPorId(Long id) {
        return solicitudProyectoRepository.findByIdConTareas(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }


    public List<SolicitudProyectoResponseDTO> listarProyectosPendientes() {
        List<SolicitudProyectoResponseDTO> proyectoResponseDTOS = new ArrayList<>();
        List<SolicitudProyecto> proyectos =  solicitudProyectoRepository.findByEstado(EstadoSolicitudProyecto.PENDIENTE);

        for (SolicitudProyecto sp: proyectos){
            proyectoResponseDTOS.add(
                    new SolicitudProyectoResponseDTO(sp)
            );
        }
        return proyectoResponseDTOS;
    }

    public SolicitudProyectoRequestDTO convertirARequestDTO(SolicitudProyecto proyecto) {
        SolicitudProyectoRequestDTO dto = new SolicitudProyectoRequestDTO();

        // Relación con la solicitud de radicación
        if (proyecto.getSolicitudRadicacion() != null) {
            dto.setSolicitudRadicacionId(proyecto.getSolicitudRadicacion().getId());
        }

        // Datos generales
        dto.setTitulo(proyecto.getTitulo());                // título del proyecto
        dto.setDescripcion(proyecto.getDescripcion());      // descripción del proyecto
        dto.setObjetivo(proyecto.getObjetivo());
        dto.setRubro(proyecto.getRubro());
        dto.setActividadPrincipal(proyecto.getActividadPrincipal());
        dto.setActividadSecundaria(proyecto.getActividadSecundaria());
        dto.setInversionEstimada(proyecto.getInversionEstimada());
        dto.setProduccionEstimada(proyecto.getProduccionEstimada());

        // Personal a ocupar
        dto.setPersonalAOcupar(proyecto.getPersonalAOcupar());

        // Superficies
        dto.setSupCubiertaTrabajoM2(proyecto.getSupCubiertaTrabajoM2());
        dto.setSupCubiertaDepositoM2(proyecto.getSupCubiertaDepositoM2());
        dto.setSupExpansionM2(proyecto.getSupExpansionM2());

        // Otros datos
        dto.setTienePlanos(proyecto.getTienePlanos());
        dto.setGeneraResiduos(proyecto.isGeneraResiduos());
        dto.setDescripcionResiduos(proyecto.getDescripcionResiduos());

        // Servicios seleccionados (enum)
        if (proyecto.getServiciosRequeridos() != null && !proyecto.getServiciosRequeridos().isEmpty()) {
            dto.setServiciosRequeridos(new ArrayList<>(proyecto.getServiciosRequeridos()));
        }


        // Tareas → usamos TareaSoliDTORequest
        if (proyecto.getTareas() != null && !proyecto.getTareas().isEmpty()) {
            dto.setTareas(
                    proyecto.getTareas().stream()
                            .map(t -> new TareaSoliDTORequest(t.getTitulo(), t.getDescripcion()))
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

}