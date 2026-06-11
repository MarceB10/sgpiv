package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.NombreRol;
import sgpiv.model.SolicitudOrganismoPublico;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.repository.SolicitudOrganismoPublicoRepository;
import sgpiv.repository.SolicitudRadicacionRepository;
import sgpiv.service.SolicitudOrganismoService;
import sgpiv.service.SolicitudService;
import sgpiv.service.UsuarioService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GerenteController {

    private final SolicitudService solicitudService;
    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final UsuarioService usuarioService;
    private  final SolicitudOrganismoService solicitudOrganismoService;
    private final SolicitudOrganismoPublicoRepository solicitudOrgRepo;

    @GetMapping("/gerente/usuarios")
    public String usuariosPendientes(Model model,
                                     HttpSession session){

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null){
            return "redirect:/";
        }

        List<UsuarioResponseDTO> pendientes = usuarioService.usuariosSinRol();
        model.addAttribute("usuario", usuario);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("todos", usuarioService.todosLosUsuarios());
        model.addAttribute("pagina", "usuarios");
        return "gerente/usuarios";
    }

    @PostMapping("/gerente/usuarios/{cuit}/rol")
    public String asignarRol(@PathVariable String cuit,
                             @RequestParam NombreRol rol,
                             HttpSession session){
        usuarioService.asignarRol(cuit, rol);
        return "redirect:/gerente/usuarios";
    }

    @PostMapping("/gerente/usuarios/{cuit}/baja")
    public String darDeBaja(@PathVariable String cuit) {
        usuarioService.darDeBajaUsuario(cuit);
        return "redirect:/gerente/usuarios";
    }



    @GetMapping("/solicitudesGerente")
    public String solicitudesGerente(Model model,
                                     HttpSession session){
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null){
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        model.addAttribute(
                "solicitudes",
                solicitudService.listarPendientes()
        );

        model.addAttribute("proyectos",
                solicitudService.listarProyectosPendientes());

        model.addAttribute("solicitudesOrganismo", solicitudOrganismoService.listarPendientes());

        model.addAttribute("pagina", "solicitudes-gerente");

        return "solicitudesGerente";
    }

    @PostMapping("/solicitudesGerente/{id}/aprobar")
    public String aprobar(@PathVariable Long id,
                          HttpSession session){
        LoteResponseDTO lote = (LoteResponseDTO) session.getAttribute("loteSeleccionado");
        if (lote == null){
            return "redirect:/solicitudesGerente" + id;
        }


        solicitudService.aprobarSolicitudPrimeraParte(id);
        session.removeAttribute("loteSeleccionado");
        return "redirect:/solicitudesGerente";
    }

    @GetMapping("/solicitudesGerente/{id}")
    public String detalleSolicitud(@PathVariable Long id,
                                   Model model,
                                   HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("solicitud", solicitudService.obtenerPorId(id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "solicitud-radicacion");

        return "gerente/detalleSolicitud";
    }

    @PostMapping("/solicitudesGerente/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam String motivo) {

        solicitudService.rechazar(id, motivo);
        return "redirect:/solicitudesGerente";
    }

    @PostMapping("/solicitudesGerente/{id}/requiereModificacion")
    public String requiereModificacion(@PathVariable Long id,
                                       @RequestParam String motivo) {
        solicitudService.requiereModificacion(id, motivo);

        return "redirect:/solicitudesGerente";
    }

    // ===== NUEVOS MÉTODOS PARA ETAPA INICIAL =====

    @GetMapping("/gerente/solicitudes")
    public String listarSolicitudes(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<SolicitudRadicacion> solicitudesIniciales = solicitudService.listarSolicitudesInicialesPendientes();
        model.addAttribute("solicitudesIniciales", solicitudesIniciales);
        model.addAttribute("usuario", usuario);

        return "/solicitudesGerente";
    }

    @PostMapping("/gerente/solicitudes/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Long id, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            solicitudService.aceptarSolicitudInicial(id);
            return "redirect:/solicitudesGerente?ok=Solicitud aceptada";
        } catch (RuntimeException e) {
            return "redirect:/solicitudesGerente?error=" + e.getMessage();
        }
    }

    @PostMapping("/gerente/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id,
                                    @RequestParam String motivo,
                                    HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        try {
            solicitudService.rechazarSolicitudInicial(id, motivo);
            return "redirect:/solicitudesGerente?ok=Solicitud rechazada";
        } catch (RuntimeException e) {
            return "redirect:/solicitudesGerente?error=" + e.getMessage();
        }
    }


    @GetMapping("/proyectos/{id}")
    public String detalleProyecto(@PathVariable Long id,
                                  Model model,
                                  HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("proyecto", solicitudService.obtenerProyectoPorId(id));
        model.addAttribute("pagina", "detalle-proyecto");

        return "gerente/detalleProyecto";
    }

    @PostMapping("/proyectos/{id}/rechazar")
    public String rechazarProyecto(@PathVariable Long id,
                                   @RequestParam String motivo) {
        solicitudService.rechazarSolicitudProyecto(id, motivo);
        return "redirect:/solicitudesGerente";

    }

    @PostMapping("/proyectos/{id}/modificar")
    public String solicitarModificacionProyecto(@PathVariable Long id,
                                                @RequestParam String motivo) {
        solicitudService.requiereModificacionProyecto(id, motivo);
        return "redirect:/solicitudesGerente";

    }

    @PostMapping("/proyectos/{id}/aceptar")
    public String aceptarProyecto(@PathVariable Long id) {
        solicitudService.aprobarSolicitudProyecto(id);
        return "redirect:/solicitudesGerente";

    }


    /// ----------------------------Solicitudes Org Publico ----------------------------------------

    @GetMapping("/gerente/solicitudesOrganismo")
    public String listarSolicitudesOrganismo(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("solicitudes", solicitudOrganismoService.listarPendientes());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "solicitudes-organismo");
        return "solicitudesGerente";
    }

    @GetMapping("/gerente/solicitudesOrganismo/{id}")
    public String detalleSolicitudOrganismo(@PathVariable Long id,
                                            Model model,
                                            HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("solicitud", solicitudOrganismoService.obtenerSolicitudPorId(id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "solicitudes-organismo");
        return "gerente/detalleSolicitudOrganismo";
    }

    @PostMapping("/gerente/solicitudesOrganismo/{id}/aprobar")
    public String aprobarOrganismo(@PathVariable Long id) {
        solicitudOrganismoService.aprobar(id);
        return "redirect:/solicitudesGerente";
    }

    @PostMapping("/gerente/solicitudesOrganismo/{id}/rechazar")
    public String rechazarOrganismo(@PathVariable Long id,
                                    @RequestParam String motivo) {
        solicitudOrganismoService.rechazar(id, motivo);
        return "redirect:/solicitudesGerente";
    }

    @GetMapping("/gerente/solicitudesOrganismo/{id}/archivo")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        SolicitudOrganismoPublico solicitud = solicitudOrgRepo.findById(id).orElseThrow();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + solicitud.getNombreArchivo() + "\"")
                .contentType(MediaType.parseMediaType(solicitud.getTipoArchivo()))
                .body(solicitud.getArchivo());
    }

}
