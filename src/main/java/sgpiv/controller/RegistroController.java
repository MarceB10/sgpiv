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
import sgpiv.dtos.request.UsuarioRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class RegistroController {

    private final UsuarioService usuarioService;

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuarioRequestDTO", new UsuarioRequestDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute UsuarioRequestDTO dto,
                                   BindingResult result,
                                   Model model,
                                   HttpSession session){

        if (result.hasErrors()){
            return "registro";
        }

        try{
            UsuarioResponseDTO usuario = usuarioService.registrarse(dto);
            session.setAttribute("usuario", usuario);
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

}
