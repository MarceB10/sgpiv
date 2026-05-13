package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;

    @GetMapping("/home")
    public String home(Model model,
                       HttpSession session){

        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null){
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "home");
        return "home";
    }
}
