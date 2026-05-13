package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.repository.EmpresaRepository;

import java.util.List;

@Controller
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping("/empresas")
    public String listarEmpresas(
            @RequestParam(required = false) String buscar,
            Model model,
            HttpSession session
    ){

        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if(usuario == null){
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);

        List<Empresa> empresas;

        if(buscar != null && !buscar.isBlank()){

            empresas = empresaRepository
                    .findByRazonSocialContainingIgnoreCase(buscar);

        }else{

            empresas = empresaRepository.findAll();

        }

        long interesadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.INTERESADA)
                .count();

        long radicadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.RADICADA)
                .count();

        long adjudicadas = empresas.stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.ADJUDICADA)
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
}