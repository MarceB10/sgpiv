package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sgpiv.dtos.request.ProveedorParqueRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.ProveedorParqueService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gerente/proveedores")
public class ProveedorParqueController {

    private final ProveedorParqueService proveedorParqueService;

    @GetMapping
    public String listar(Model model, HttpSession session) {
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("proveedores", proveedorParqueService.listarTodos());
        model.addAttribute("proveedorDTO", new ProveedorParqueRequestDTO());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "proveedores-parque");
        return "gerente/proveedoresParque";
    }

    @PostMapping
    public String agregar(@Valid @ModelAttribute("proveedorDTO") ProveedorParqueRequestDTO dto,
                          BindingResult result,
                          Model model,
                          HttpSession session) {
        if (result.hasErrors()) {
            UsuarioResponseDTO usuario =
                    (UsuarioResponseDTO) session.getAttribute("usuario");
            model.addAttribute("proveedores", proveedorParqueService.listarTodos());
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "proveedores-parque");
            return "gerente/proveedoresParque";
        }
        proveedorParqueService.agregar(dto);
        return "redirect:/gerente/proveedores";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        proveedorParqueService.eliminar(id);
        return "redirect:/gerente/proveedores";
    }
}