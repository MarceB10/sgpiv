package sgpiv.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sgpiv.dtos.request.SolicitudOrganismoRequestDTO;
import sgpiv.dtos.response.OrganismoPublicoResponseDTO;
import sgpiv.dtos.response.SolicitudOrganismoResponseDTO;
import sgpiv.enums.EstadoSolicitudOrganismo;
import sgpiv.enums.NombreRol;
import sgpiv.model.OrganismoPublico;
import sgpiv.model.SolicitudOrganismoPublico;
import sgpiv.model.Usuario;
import sgpiv.repository.OrganismoPublicoRepository;
import sgpiv.repository.SolicitudOrganismoPublicoRepository;
import sgpiv.repository.UsuarioRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudOrganismoService {

    private final SolicitudOrganismoPublicoRepository solicitudRepo;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final OrganismoPublicoRepository organismoPublicoRepository;


    @Transactional
    public void guardar(SolicitudOrganismoRequestDTO dto,
                        MultipartFile archivo,
                        String cuitUsuario) {

        Usuario usuario = obtenerUsuario(cuitUsuario);

        SolicitudOrganismoPublico solicitudExistente =
                validarSolicitudExistente(usuario.getId());

        // Si existe y está rechazada, se reutiliza
        if (solicitudExistente != null) {

            actualizarSolicitudRechazada(
                    solicitudExistente,
                    dto,
                    archivo
            );

            solicitudRepo.save(solicitudExistente);
            return;
        }

        SolicitudOrganismoPublico solicitud =
                construirSolicitud(dto, usuario);

        procesarArchivo(archivo, solicitud);

        solicitudRepo.save(solicitud);
    }


    public List<SolicitudOrganismoResponseDTO> listarPendientes() {
        return solicitudRepo.findByEstado(EstadoSolicitudOrganismo.PENDIENTE)
                .stream()
                .map(SolicitudOrganismoResponseDTO::new)
                .toList();
    }

    public SolicitudOrganismoResponseDTO obtenerSolicitudPorId(Long id) {
        return new SolicitudOrganismoResponseDTO(
                solicitudRepo.findById(id).orElseThrow()
        );
    }

    @Transactional
    public void aprobar(Long id) {
        SolicitudOrganismoPublico solicitud = solicitudRepo.findById(id).orElseThrow();
        Usuario usuario = solicitud.getUsuario();

        // Dar rol organismo publico
        usuarioService.asignarRol(usuario.getCuit(), NombreRol.ROL_ORGANISMO_PUBLICO);

        solicitud.setEstado(EstadoSolicitudOrganismo.APROBADA);
        solicitudRepo.save(solicitud);

        OrganismoPublico organismo = organismoPublicoRepository
                .findByUsuarioCuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("Organismo Publico No encontrado"));

        organismo.setNombreOrganismo(solicitud.getNombreOrganismo());
        organismo.setTipoOrganismo(solicitud.getTipoOrganismo());
        organismo.setCargoSolicitante(solicitud.getCargoSolicitante());
        organismo.setActivo(true);
        organismoPublicoRepository.save(organismo);
    }

    @Transactional
    public void rechazar(Long id, String motivo) {
        SolicitudOrganismoPublico solicitud = solicitudRepo.findById(id).orElseThrow();
        solicitud.setEstado(EstadoSolicitudOrganismo.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitudRepo.save(solicitud);
    }

    // Para verificar si el usuario ya tiene una solicitud activa
    public boolean tieneSolicitudActiva(Long usuarioId) {
        return solicitudRepo.findByUsuarioId(usuarioId)
                .map(s -> s.getEstado() == EstadoSolicitudOrganismo.PENDIENTE)
                .orElse(false);
    }


    private SolicitudOrganismoPublico validarSolicitudExistente(Long usuarioId) {

        SolicitudOrganismoPublico solicitudExistente = solicitudRepo
                .findByUsuarioId(usuarioId)
                .orElse(null);

        if (solicitudExistente == null) {
            return null;
        }

        if (solicitudExistente.getEstado()
                == EstadoSolicitudOrganismo.PENDIENTE) {

            throw new RuntimeException(
                    "Ya posee una solicitud de organismo público en curso."
            );
        }

        if (solicitudExistente.getEstado()
                == EstadoSolicitudOrganismo.APROBADA) {

            throw new RuntimeException(
                    "Su solicitud de organismo público ya fue aprobada."
            );
        }

        // Si llegó acá está RECHAZADA
        return solicitudExistente;
    }

    private Usuario obtenerUsuario(String cuitUsuario) {
        return usuarioRepository.findByCuit(cuitUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));
    }



    private SolicitudOrganismoPublico construirSolicitud(
            SolicitudOrganismoRequestDTO dto,
            Usuario usuario) {

        SolicitudOrganismoPublico solicitud =
                new SolicitudOrganismoPublico();

        solicitud.setNombreOrganismo(dto.getNombreOrganismo());
        solicitud.setTipoOrganismo(dto.getTipoOrganismo());
        solicitud.setCargoSolicitante(dto.getCargoSolicitante());
        solicitud.setMotivoAcceso(dto.getMotivoAcceso());

        solicitud.setUsuario(usuario);
        solicitud.setFechaEnvio(LocalDate.now());
        solicitud.setEstado(EstadoSolicitudOrganismo.PENDIENTE);

        return solicitud;
    }


    private void procesarArchivo(MultipartFile archivo,
                                 SolicitudOrganismoPublico solicitud) {

        if (archivo == null || archivo.isEmpty()) {
            return;
        }

        try {

            solicitud.setNombreArchivo(
                    archivo.getOriginalFilename());

            solicitud.setTipoArchivo(
                    archivo.getContentType());

            solicitud.setArchivo(
                    archivo.getBytes());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error al procesar el archivo adjunto");
        }
    }



    private void actualizarSolicitudRechazada(
            SolicitudOrganismoPublico solicitud,
            SolicitudOrganismoRequestDTO dto,
            MultipartFile archivo) {

        solicitud.setNombreOrganismo(
                dto.getNombreOrganismo());

        solicitud.setTipoOrganismo(
                dto.getTipoOrganismo());

        solicitud.setCargoSolicitante(
                dto.getCargoSolicitante());

        solicitud.setMotivoAcceso(
                dto.getMotivoAcceso());

        solicitud.setEstado(
                EstadoSolicitudOrganismo.PENDIENTE);

        solicitud.setMotivoRechazo(null);

        solicitud.setFechaEnvio(
                LocalDate.now());

        procesarArchivo(archivo, solicitud);
    }

}
