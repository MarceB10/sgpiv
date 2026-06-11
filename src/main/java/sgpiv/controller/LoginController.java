package sgpiv.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import sgpiv.dtos.request.LoginDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.UsuarioService;


@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String landing(){
        return "landing";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }


    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute LoginDTO dto,
                                BindingResult result, Model model, HttpSession session){

        if (result.hasErrors()) return "login";
        try{
            UsuarioResponseDTO usuario = usuarioService.iniciarSesion(dto);
//            model.addAttribute("usuario", usuario); ESTO SE USA MAS ADELANTE NO SE BORRA
            session.setAttribute("usuario", usuario); //Asi guardamos la session por el momento para hacer redirects
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginDTO", dto);
            return "login";
        }
    }

    @GetMapping("/cerrarSesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // destruye la sesión actual
        return "redirect:/login";
    }

}
