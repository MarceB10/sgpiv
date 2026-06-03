package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import sgpiv.dtos.request.EmpresaRequestDTO;
import sgpiv.dtos.response.EmpresaResponseDTO;
import sgpiv.dtos.response.TareaResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Proyecto;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.repository.ProyectoRepository;
import sgpiv.service.EmpresaService;
import sgpiv.service.ProyectoService;
import sgpiv.service.RepresentanteService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final RepresentanteService representanteService;
    private final ProyectoService proyectoService;

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
            return "redirect:/";
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
                .filter(e -> EstadoEmpresa.INTERESADA.toString().equals(e.getEstadoEmpresa()))
                .count();

        long radicadas = empresas.stream()
                .filter(e -> EstadoEmpresa.RADICADA.toString().equals(e.getEstadoEmpresa()))
                .count();

        long adjudicadas = empresas.stream()
                .filter(e -> EstadoEmpresa.ADJUDICADA.toString().equals(e.getEstadoEmpresa()))
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

    @GetMapping("/mi-empresa")
    public String miEmpresa(Model model, HttpSession session){

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "mi-empresa");

        try {
            EmpresaResponseDTO empresa = empresaService.buscarEmpresaDelRepresentante(usuario.getCuit());
            model.addAttribute("empresa", empresa);
        } catch (Exception e) {
            // Si no hay empresa, mandamos el atributo como null o un mensaje
            System.out.println("Error al buscar empresa: " + e.getMessage()); // temporal
            model.addAttribute("empresa", null);
            model.addAttribute("mensaje", "Aún no has registrado ninguna empresa en el sistema.");
        }

        return "mi-empresa";
    }

    @GetMapping("/registrarMiEmpresa")
    public String mostrarFormulario(Model model, HttpSession session) {
        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("empresaDTO", new EmpresaRequestDTO());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "mi-empresa");
        return "registrarMiEmpresa";
    }

    @PostMapping("/registrarMiEmpresa")
    public String registrarEmpresa(@Valid @ModelAttribute("empresaDTO") EmpresaRequestDTO dto,
                                   BindingResult result,
                                   HttpSession session,
                                   Model model) {
        if (result.hasErrors()) {
            return "registrarMiEmpresa";
        }
        try {
            UsuarioResponseDTO usuarioDTO =
                    (UsuarioResponseDTO) session.getAttribute("usuario");

            RepresentanteEmpresa representante = representanteService
                    .buscarPorCuit(usuarioDTO.getCuit());

            empresaService.registrar(dto, representante);
            return "redirect:/empresas/mi-empresa";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registrarMiEmpresa";
        }
    }

    @GetMapping("/proyectos")
    public String verProyectosEmpresa(Model model, HttpSession session){

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");

        if(usuario == null) return "redirect:/";

        EmpresaResponseDTO empresa = empresaService.buscarEmpresaDelRepresentante(usuario.getCuit());

        model.addAttribute("usuario", usuario);
        model.addAttribute("empresa", empresa);
        model.addAttribute("proyectos",empresa.getProyectos());
        model.addAttribute("pagina","proyecto-empresa");

        return "representante_empresa/proyectosEmpresa";
    }

    @GetMapping("/proyecto/{id}")
    public String detalleProyecto(@PathVariable Long id, Model model, HttpSession session){

        UsuarioResponseDTO usuario =(UsuarioResponseDTO) session.getAttribute("usuario");
        if(usuario == null) return "redirect:/";

        Proyecto proyecto = proyectoService.obtenerPorId(id);

        model.addAttribute("usuario",usuario);
        model.addAttribute("proyecto",proyecto);
        model.addAttribute("pagina","proyecto-empresa");

        return "representante_empresa/proyectoEmpresa";
    }
}