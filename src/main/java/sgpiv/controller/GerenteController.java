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

}
