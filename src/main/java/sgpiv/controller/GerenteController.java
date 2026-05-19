package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.NombreRol;
import sgpiv.model.Solicitud;
import sgpiv.repository.SolicitudRepository;
import sgpiv.service.UsuarioService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GerenteController {

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

    private final SolicitudRepository solicitudRepository;

    @GetMapping("/solicitudesGerente")
    public String solicitudesGerente(Model model,
                                     HttpSession session){
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        model.addAttribute("usuario", usuario);

        model.addAttribute(
                "solicitudes",
                solicitudRepository.findAll()
        );

        model.addAttribute("pagina", "solicitudes");

        return "solicitudesGerente";
    }

    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id){

        Solicitud solicitud =
                solicitudRepository.findById(id).get();

        solicitud.setEstado("APROBADA");

        solicitudRepository.save(solicitud);

        return "redirect:/solicitudesGerente";
    }
}
