package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
import sgpiv.model.SolicitudRadicacion;
import sgpiv.repository.SolicitudRepository;
import sgpiv.service.SolicitudService;
import sgpiv.service.UsuarioService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GerenteController {

    private final SolicitudService solicitudService;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;

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

        model.addAttribute("pagina", "solicitudes-gerente");

        return "solicitudesGerente";
    }

    @PostMapping("/solicitudesGerente/{id}/aprobar")
    public String aprobar(@PathVariable Long id,
                          HttpSession session){
        LoteResponseDTO lote = (LoteResponseDTO) session.getAttribute("loteSeleccionado");
        if (lote == null){
            return "redirect:/solicitudesGerente/" + id;
        }


        solicitudService.aprobar(id, lote.getId());
        session.removeAttribute("loteSeleccionado");
        return "redirect:/solicitudesGerente";
    }

    @GetMapping("/solicitudesGerente/{id}")
    public String detalleSolicitud(@PathVariable Long id,
                                   Model model,
                                   HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

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
}
