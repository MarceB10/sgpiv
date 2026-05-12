package sgpiv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sgpiv.repository.EmpresaRepository;

@Controller
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping("/empresas")
    public String listarEmpresas(
            @RequestParam(required = false) String buscar,
            Model model
    ){

        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute(
                    "empresas",
                    empresaRepository.buscarEmpresas(buscar)
            );
        } else {
            model.addAttribute(
                    "empresas",
                    empresaRepository.findAll()
            );
        }

        model.addAttribute("buscar", buscar);

        return "empresas";
    }
}