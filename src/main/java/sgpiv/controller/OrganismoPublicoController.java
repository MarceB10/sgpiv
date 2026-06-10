package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sgpiv.dtos.response.OrganismoPublicoResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.OrgPublicoService;

import java.util.List;

@Controller
@RequestMapping("/gerente/organismos")
@RequiredArgsConstructor
public class OrganismoPublicoController {

    private final OrgPublicoService organismoService;

    @GetMapping
    public String listar(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<OrganismoPublicoResponseDTO> organismos = organismoService.listarTodos();

        long activos   = organismos.stream().filter(OrganismoPublicoResponseDTO::isActivo).count();
        long inactivos = organismos.size() - activos;

        model.addAttribute("organismos", organismos);
        model.addAttribute("totalOrganismos", organismos.size());
        model.addAttribute("activos", activos);
        model.addAttribute("inactivos", inactivos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "organismos");
        return "gerente/organismos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("organismo", organismoService.obtenerPorId(id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "organismos");
        return "gerente/detalleOrganismo";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id) {
        organismoService.darDeBaja(id);
        return "redirect:/gerente/organismos";
    }
}
