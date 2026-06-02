package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.dtos.request.TareaSoliDTORequest;
import sgpiv.dtos.response.SolicitudResponseDTO;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.EstadoSolicitudProyecto;
import sgpiv.enums.NombreRol;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;
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


    public void enviarSolicitud(SolicitudRequestDTO dto, String cuitUsuario) {

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
        solicitud.setActividadSecundaria(dto.getActividadSecundaria());
        solicitud.setNecesidadM2(dto.getNecesidadM2());
        solicitud.setSupCubiertaTrabajoM2(dto.getSupCubiertaTrabajoM2());
        solicitud.setSupCubiertaDepositoM2(dto.getSupCubiertaDepositoM2());
        solicitud.setSupExpansionM2(dto.getSupExpansionM2());
        solicitud.setTienePlanos(dto.getTienePlanos());
        solicitud.setPersonalAOcupar(dto.getPersonalAOcupar());
        solicitud.setTiempoDeRadicacion(dto.getTiempoDeRadicacion());

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


        //cambio de estado de la solicitud
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudRadicacionRepository.save(solicitud);
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
        dto.setActividadSecundaria(solicitud.getActividadSecundaria());
        dto.setNecesidadM2(solicitud.getNecesidadM2());
        dto.setSupCubiertaTrabajoM2(solicitud.getSupCubiertaTrabajoM2());
        dto.setSupCubiertaDepositoM2(solicitud.getSupCubiertaDepositoM2());
        dto.setSupExpansionM2(solicitud.getSupExpansionM2());
        dto.setTienePlanos(solicitud.getTienePlanos());
        dto.setPersonalAOcupar(solicitud.getPersonalAOcupar());
        dto.setTiempoDeRadicacion(solicitud.getTiempoDeRadicacion());

        // MAPEAR TAREAS - ESTA ERA LA PARTE FALTANTE
        if (solicitud.getTareas() != null && !solicitud.getTareas().isEmpty()) {
            List<TareaSoliDTORequest> tareasDTO = solicitud.getTareas().stream()
                    .map(tarea -> new TareaSoliDTORequest(
                            tarea.getTitulo(),
                            tarea.getDescripcion()
                    ))
                    .collect(Collectors.toList());
            dto.setTareas(tareasDTO);
        }

        return dto;
    }



}