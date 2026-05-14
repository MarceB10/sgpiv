package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import sgpiv.dtos.response.EmpresaResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.service.EmpresaService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empresas")
public class EmpresaController {

    private EmpresaService empresaService;

    @GetMapping
    public String listarEmpresas(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) EstadoEmpresa estado,
            Model model,
            HttpSession session
    ){

        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if(usuario == null){
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);

        List<EmpresaResponseDTO> empresas;

        if(buscar != null && !buscar.isBlank()){

            empresas = empresaService.listarPorRazonSocial(buscar);//razon social == nombre

        }else if (estado != null ){
            empresas = empresaService.listarPorEstado(estado);
        } else {

            empresas = empresaService.listarTodas();

        }

        long interesadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.INTERESADA.toString())
                .count();

        long radicadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.RADICADA.toString())
                .count();

        long adjudicadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.ADJUDICADA.toString())
                .count();

        model.addAttribute("empresas", empresas);

        model.addAttribute("buscar", buscar);

        model.addAttribute("totalEmpresas", empresas.size());
        model.addAttribute("interesadas", interesadas);
        model.addAttribute("radicadas", radicadas);
        model.addAttribute("adjudicadas", adjudicadas);
        model.addAttribute("pagina", "empresas");
        return "empresas";
    }

    @PostMapping("/{id}/radicar")
    public String radicar(@PathVariable Long id){

        empresaService.radicar(id);

        return "redirect:/empresas";
    }

    @PostMapping("/{id}/baja")
    public String baja(@PathVariable Long id){
        empresaService.darDeBaja(id);

        return "redirect:/empresas";
    }
}