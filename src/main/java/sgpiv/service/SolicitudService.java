package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.request.SolicitudProyectoRequestDTO;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.dtos.request.TareaSoliDTORequest;
import sgpiv.dtos.response.SolicitudResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.EstadoSolicitudProyecto;
import sgpiv.enums.NombreRol;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final SolicitudProyectoRepository solicitudProyectoRepository;

    private final UsuarioRepository usuarioRepository;

    private final OcupacionLoteService ocupacionLoteService;

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

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


        solicitudRadicacionRepository.save(solicitud);
    }

    @Transactional
    public void aprobarSolicitudPrimeraParte(Long id) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        // Cambiar estado a pendiente_proyecto
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_PROYECTO);
        solicitudRadicacionRepository.save(solicitud);
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
        empresa.setCuit(sp.getSolicitudRadicacion().getCuitEmpresa());
        empresa.setRubro(sp.getSolicitudRadicacion().getRubro());
        empresa = empresaRepository.save(empresa);

        // 2. Dar rol representante al usuario y asociarlo a la empresa
        usuarioService.asignarRol(usuario.getCuit(), NombreRol.ROL_REPRESENTANTE_EMPRESA);
        usuario = usuarioRepository
                .findByCuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario No encontrado"));
        usuarioRepository.save(usuario);
        // TODO ESTO SE HACE EN LA ETAPA 2 AHORA AL APROBAR EL PROYECTO

//        Lote lote = loteService.obtenerPorId(idLote);

//        List<Tarea> tareasProyecto = new ArrayList<>();

//        for (TareaSolicitud tareaSolicitud: solicitud.getTareas()){
//            tareasProyecto.add(new Tarea(
//                    tareaSolicitud.getTitulo(),
//                    tareaSolicitud.getDescripcion()
//            ));
//        }

//        //Creacion de Proyecto
//        Proyecto proyecto = new Proyecto(
//                solicitud.getActividadPrincipal(),
//                solicitud.getObjetivoProyecto(),
//                LocalDate.now(),
//                solicitud.getPersonalAOcupar()
//        );

//        proyecto.agregarTareas(tareasProyecto);
        //proyecto.setFechaFin(LocalDate.now().plus(solicitud.getTiempoDeRadicacion()));

        //creacion de empresa
//        Empresa empresa = new Empresa();
//        empresa.setRazonSocial(solicitud.getRazonSocial());
//        empresa.setCuit(solicitud.getCuitEmpresa());
//        empresa.setRubro(solicitud.getRubro());
//        empresa.setEmail(solicitud.getEmailEmpresa());
//        empresa.setTelefono(Long.valueOf(solicitud.getTelefonoEmpresa()));
//        empresa.setDireccion(solicitud.getDireccion());
//        empresa.setIngresoBrutos(solicitud.getIngresoBrutos());
//        empresa.setDescripcionBienServicio(solicitud.getDescripcionBienServicio());
//        empresa.setTipoIndustria(solicitud.getTipoIndustria());

        //asociar Proyecto a empresa
//        empresa.agregarProyecto(proyecto);

//        empresa.setEstadoEmpresa(EstadoEmpresa.ADJUDICADA);//por ahora interesada hasta que tenga la adjudicacion


//        Usuario usuario = solicitud.getUsuario();

//        usuarioService.asignarRol(
//                usuario.getCuit(),
//                NombreRol.ROL_REPRESENTANTE_EMPRESA
//        );

        //creacion y asignacion de representante
//        RepresentanteEmpresa representante =
//                representanteService.buscarPorCuit(usuario.getCuit());
//
////        proyecto.setRepresentanteEmpresa(representante);
//        representanteService.asignarEmpresa(representante, empresa);
//        empresaRepository.saveAndFlush(empresa);
//
//
//        //cambio de estado de la solicitud
//        solicitud.setEstado(EstadoSolicitud.APROBADA);
//
//        solicitudRepository.save(solicitud);

        RepresentanteEmpresa representanteEmpresa = representanteService.buscarPorCuit(usuario.getCuit());

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
        proyecto.setEmpresa(empresa);
        proyecto.setRepresentanteEmpresa(representanteEmpresa);
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

    }




    public void rechazar(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRadicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitudRadicacionRepository.save(solicitud);
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
}