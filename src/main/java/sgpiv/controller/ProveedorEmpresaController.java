package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sgpiv.dtos.request.ProveedorEmpresaRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.ProveedorEmpresaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empresas/mis-proveedores")
public class ProveedorEmpresaController {

    private final ProveedorEmpresaService proveedorEmpresaService;

    @GetMapping
    public String listar(Model model, HttpSession session) {
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("proveedores",
                proveedorEmpresaService.listarPorEmpresa(usuario.getCuit()));
        model.addAttribute("proveedorDTO", new ProveedorEmpresaRequestDTO());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "mis-proveedores");
        return "representante_empresa/misProveedores";
    }

    @PostMapping
    public String agregar(@Valid @ModelAttribute("proveedorDTO") ProveedorEmpresaRequestDTO dto,
                          BindingResult result,
                          Model model,
                          HttpSession session) {
        if (result.hasErrors()) {
            UsuarioResponseDTO usuario =
                    (UsuarioResponseDTO) session.getAttribute("usuario");
            model.addAttribute("proveedores",
                    proveedorEmpresaService.listarPorEmpresa(usuario.getCuit()));
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "mis-proveedores");
            return "representante_empresa/misProveedores";
        }

        try {
            UsuarioResponseDTO usuario =
                    (UsuarioResponseDTO) session.getAttribute("usuario");
            proveedorEmpresaService.agregar(dto, usuario.getCuit());
            return "redirect:/empresas/mis-proveedores";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "representante_empresa/misProveedores";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        proveedorEmpresaService.eliminar(id);
        return "redirect:/empresas/mis-proveedores";
    }
}