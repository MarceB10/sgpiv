package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.request.SolicitudRequestDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.NombreRol;
import sgpiv.model.Empresa;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.model.Usuario;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.SolicitudRepository;
import sgpiv.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final RepresentanteService representanteService;

    public void enviarSolicitud(SolicitudRequestDTO dto, String cuitUsuario) {
        Usuario usuario = usuarioRepository.findByCuit(cuitUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SolicitudRadicacion solicitudActiva =
                obtenerSolicitudActiva(cuitUsuario);

        if (solicitudActiva != null){
            throw new RuntimeException("Ya tenés una solicitud activa.");
        }

        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setRazonSocial(dto.getRazonSocial());
        solicitud.setCuitEmpresa(dto.getCuitEmpresa());
        solicitud.setRubro(dto.getRubro());
        solicitud.setEmailEmpresa(dto.getEmailEmpresa());
        solicitud.setTelefonoEmpresa(dto.getTelefonoEmpresa());
        solicitud.setDireccion(dto.getDireccion());
        solicitud.setIngresoBrutos(dto.getIngresoBrutos());
        solicitud.setDescripcionBienServicio(dto.getDescripcionBienServicio());
        solicitud.setTipoIndustria(dto.getTipoIndustria());
        solicitud.setTipoEmpresa(dto.getTipoEmpresa());
        solicitud.setObjetivoProyecto(dto.getObjetivoProyecto());
        solicitud.setActividadPrincipal(dto.getActividadPrincipal());
        solicitud.setActividadSecundaria(dto.getActividadSecundaria());
        solicitud.setNecesidadM2(dto.getNecesidadM2());
        solicitud.setSupCubiertaTrabajoM2(dto.getSupCubiertaTrabajoM2());
        solicitud.setSupCubiertaDepositoM2(dto.getSupCubiertaDepositoM2());
//        solicitud.setSupExpansionM2(dto.getSupExpansionM2());
        solicitud.setTienePlanos(dto.getTienePlanos());
        solicitud.setPersonalAOcupar(dto.getPersonalAOcupar());
        solicitud.setTiempoDeRadicacion(dto.getTiempoDeRadicacion());
        solicitud.setFechaEnvio(LocalDate.now());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setUsuario(usuario);

        solicitudRepository.save(solicitud);
    }

    public List<SolicitudRadicacion> listarTodas() {
        return solicitudRepository.findAll();
    }

    @Transactional
    public void aprobar(Long id) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

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

        empresa.setEstadoEmpresa(EstadoEmpresa.INTERESADA);//por ahora interesada hasta que tenga la adjudicacion

        empresaRepository.save(empresa);

        Usuario usuario = solicitud.getUsuario();

        usuarioService.asignarRol(
                usuario.getCuit(),
                NombreRol.ROL_REPRESENTANTE_EMPRESA
        );

        //creacion y asignacion de representante
        RepresentanteEmpresa representante =
                representanteService.buscarPorCuit(usuario.getCuit());

        representanteService.asignarEmpresa(representante, empresa);

        //cambio de estado de la solicitud
        solicitud.setEstado(EstadoSolicitud.APROBADA);

        solicitudRepository.save(solicitud);
    }

    public void rechazar(Long id, String motivo) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitudRepository.save(solicitud);
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
                                EstadoSolicitud.APROBADA
                        )
                );
    }

    public List<SolicitudRadicacion> listarPendientes(){
        return solicitudRepository
                .findByEstado(EstadoSolicitud.PENDIENTE);
    }
}