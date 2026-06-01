package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.dtos.request.TareaSoliDTORequest;
import sgpiv.dtos.response.SolicitudResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.NombreRol;
import sgpiv.model.*;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.ProyectoRepository;
import sgpiv.repository.SolicitudRepository;
import sgpiv.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    private final OcupacionLoteService ocupacionLoteService;

    private final ProyectoRepository proyectoRepository;
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
        List<TareaSolicitud> tareas = new ArrayList<>();
        for (TareaSoliDTORequest tareaDTO : dto.getTareas()){
            tareas.add(new TareaSolicitud(
                    tareaDTO.getTitulo(),
                    tareaDTO.getDescripcion(),
                    solicitud
            ));
        }
        solicitud.setTareas(tareas);
        /// NOTA: Las Tareas se guardan al guardar la Solicitud

        // RESETEAR ESTADO
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        // LIMPIAR MOTIVO ANTERIOR
        solicitud.setMotivoRechazo(null);

        System.out.println("Cant Tareas: " + tareas.size());

        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void aprobar(Long id, Long idLote) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Lote lote = loteService.obtenerPorId(idLote);

        List<Tarea> tareasProyecto = new ArrayList<>();

        for (TareaSolicitud tareaSolicitud: solicitud.getTareas()){
            tareasProyecto.add(new Tarea(
                    tareaSolicitud.getTitulo(),
                    tareaSolicitud.getDescripcion()
            ));
        }

        //Creacion de Proyecto
        Proyecto proyecto = new Proyecto(
                solicitud.getActividadPrincipal(),
                solicitud.getObjetivoProyecto(),
                LocalDate.now(),
                solicitud.getPersonalAOcupar()
        );

        proyecto.agregarTareas(tareasProyecto);
        //proyecto.setFechaFin(LocalDate.now().plus(solicitud.getTiempoDeRadicacion()));



        //creacion de empresa
        Empresa empresa = new Empresa();
        empresa.setRazonSocial(solicitud.getRazonSocial());
        empresa.setCuit(solicitud.getCuitEmpresa());
        empresa.setRubro(solicitud.getRubro());
        empresa.setEmail(solicitud.getEmailEmpresa());
        empresa.setTelefono(Long.valueOf(solicitud.getTelefonoEmpresa()));
        empresa.setDireccion(solicitud.getDireccion());
        empresa.setIngresoBrutos(solicitud.getIngresoBrutos());
        empresa.setDescripcionBienServicio(solicitud.getDescripcionBienServicio());
        empresa.setTipoIndustria(solicitud.getTipoIndustria());

        //asociar Proyecto a empresa
        empresa.agregarProyecto(proyecto);

        empresa.setEstadoEmpresa(EstadoEmpresa.ADJUDICADA);//por ahora interesada hasta que tenga la adjudicacion


        Usuario usuario = solicitud.getUsuario();

        usuarioService.asignarRol(
                usuario.getCuit(),
                NombreRol.ROL_REPRESENTANTE_EMPRESA
        );

        //creacion y asignacion de representante
        RepresentanteEmpresa representante =
                representanteService.buscarPorCuit(usuario.getCuit());

        proyecto.setRepresentanteEmpresa(representante);
        representanteService.asignarEmpresa(representante, empresa);
        empresaRepository.saveAndFlush(empresa);


        //cambio de estado de la solicitud
        solicitud.setEstado(EstadoSolicitud.APROBADA);

        solicitudRepository.save(solicitud);

        //OcupacionLote
        ocupacionLoteService.ocuparLote(lote, proyecto);

    }

    public void rechazar(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitudRepository.save(solicitud);
        // Si se rechaza se puede desactivar el usuario
//        Usuario usuario = solicitud.getUsuario();
//        usuario.desactivar();
//        usuarioRepository.save(usuario);
    }

    public SolicitudRadicacion obtenerSolicitudActiva(String cuit){

        Usuario usuario = usuarioRepository.findByCuit(cuit)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return solicitudRepository
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
        return solicitudRepository
                .findByEstado(EstadoSolicitud.PENDIENTE);
    }

    public SolicitudResponseDTO obtenerPorId(Long id) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        return new SolicitudResponseDTO(solicitud);
    }

    public void requiereModificacion(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.REQUIERE_MODIFICACION);
        solicitud.setMotivoRechazo(motivo);
        solicitudRepository.save(solicitud);
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

        return dto;
    }



}