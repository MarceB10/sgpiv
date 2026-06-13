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
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Proyecto;
import sgpiv.model.RepresentanteEmpresa;
import sgpiv.model.SolicitudProyecto;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.repository.SolicitudProyectoRepository;
import sgpiv.service.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empresas")
public class EmpresaController {

    private final OcupacionLoteService ocupacionLoteService;
    private final EmpresaService empresaService;
    private final RepresentanteService representanteService;
    private final ProyectoService proyectoService;
    private final SolicitudService solicitudService;
    private final SolicitudProyectoRepository solicitudProyectoRepository;

    private UsuarioResponseDTO cargarUsuarioEnModelo(HttpSession session, Model model) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario != null) model.addAttribute("usuario", usuario);
        return usuario;
    }

    @GetMapping
    public String listarEmpresas(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) EstadoEmpresa estado,
            Model model, HttpSession session) {

        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        if (usuario == null) return "redirect:/";

        List<EmpresaResponseDTO> empresas = empresaService.filtrarEmpresas(buscar, estado);

        model.addAttribute("empresas", empresas);
        model.addAttribute("buscar", buscar);
        model.addAttribute("estado", estado);
        model.addAttribute("estadosEmpresa", EstadoEmpresa.values());

        model.addAttribute("totalEmpresas", empresas.size());
        model.addAttribute(
                "dadasDeBaja",
                empresaService.contarPorEstado(empresas, EstadoEmpresa.BAJA)
        );
        model.addAttribute(
                "radicadas",
                empresaService.contarPorEstado(empresas, EstadoEmpresa.RADICADA)
        );
        model.addAttribute(
                "pendienteLote",
                empresaService.contarPorEstado(empresas, EstadoEmpresa.PENDIENTE_LOTE)
        );

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
        empresaService.darDeBaja(id, "");
        return "redirect:/empresas";
    }

    @GetMapping("/mi-empresa")
    public String miEmpresa(Model model, HttpSession session){

        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        if (usuario == null) return "redirect:/";
        model.addAttribute("pagina", "mi-empresa");

        SolicitudRadicacion solicitudActiva = solicitudService.obtenerSolicitudActiva(usuario.getCuit());

        model.addAttribute("solicitudActiva", solicitudActiva);

        SolicitudProyecto solicitudProyectoActiva = null;

        if (solicitudActiva != null) {
            solicitudProyectoActiva = solicitudProyectoRepository
                    .findBySolicitudRadicacionId(solicitudActiva.getId())
                    .orElse(null);
        }

        model.addAttribute("solicitudProyectoActiva", solicitudProyectoActiva);
        try {
            EmpresaResponseDTO empresa = empresaService.buscarEmpresaDelRepresentante(usuario.getCuit());
            model.addAttribute("empresa", empresa);
        } catch (Exception e) {
            System.out.println("Error al buscar empresa: " + e.getMessage());
            model.addAttribute("empresa", null);
            model.addAttribute("mensaje", "Aún no has registrado ninguna empresa en el sistema.");
        }

        return "mi-empresa";
    }

    @GetMapping("/registrarMiEmpresa")
    public String mostrarFormulario(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        if (usuario == null) return "redirect:/";

        model.addAttribute("empresaDTO", new EmpresaRequestDTO());
        model.addAttribute("pagina", "mi-empresa");
        return "registrarMiEmpresa";
    }

    @PostMapping("/registrarMiEmpresa")
    public String registrarEmpresa(@Valid @ModelAttribute("empresaDTO") EmpresaRequestDTO dto,
                                   BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "registrarMiEmpresa";
        }
        try {
            //OJO ACA
            UsuarioResponseDTO usuarioDTO = cargarUsuarioEnModelo(session, model);

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

        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);

        if(usuario == null) return "redirect:/";

        EmpresaResponseDTO empresa = empresaService.buscarEmpresaDelRepresentante(usuario.getCuit());
        SolicitudRadicacion solicitudActiva = solicitudService.obtenerSolicitudActiva(usuario.getCuit());

        model.addAttribute("empresa", empresa);
        model.addAttribute("proyectos",empresa.getProyectos());
        model.addAttribute("pagina","proyectos-empresa");
        model.addAttribute("solicitudActiva", solicitudActiva);

        SolicitudProyecto solicitudProyectoActiva = null;

        if (solicitudActiva != null) {
            solicitudProyectoActiva = solicitudProyectoRepository
                    .findBySolicitudRadicacionId(solicitudActiva.getId())
                    .orElse(null);
        }

        model.addAttribute("solicitudProyectoActiva", solicitudProyectoActiva);

        return "representante_empresa/proyectosEmpresa";
    }

    @GetMapping("/proyecto/{id}")
    public String detalleProyecto(@PathVariable Long id, Model model, HttpSession session){

        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        if(usuario == null) return "redirect:/";

        Proyecto proyecto = proyectoService.obtenerPorId(id);

        model.addAttribute("proyecto",proyecto);
        model.addAttribute("pagina","proyectos-empresa");

        SolicitudRadicacion solicitudActiva =
                solicitudService.obtenerSolicitudActiva(usuario.getCuit());

        model.addAttribute("solicitudActiva", solicitudActiva);

        SolicitudProyecto solicitudProyectoActiva = null;

        if (solicitudActiva != null) {
            solicitudProyectoActiva = solicitudProyectoRepository
                    .findBySolicitudRadicacionId(solicitudActiva.getId())
                    .orElse(null);
        }

        model.addAttribute("solicitudProyectoActiva", solicitudProyectoActiva);

        return "representante_empresa/proyectoEmpresa";
    }

    @GetMapping("/{id}")
    public String detalleEmpresa(@PathVariable Long id, Model model, HttpSession session) {
        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        if (usuario == null) return "redirect:/";

        EmpresaResponseDTO empresa = empresaService.buscarPorId(id);

//        boolean tieneProyectosActivos = empresa.getProyectos().stream()
//                .anyMatch(p -> p.getEstado() != null &&
//                        p.getEstado() == EstadoProyecto.ACTIVO);

        model.addAttribute("empresa", empresa);
        model.addAttribute("tieneProyectosActivos", false);//cambiar a true mas adelante
        model.addAttribute("pagina", "empresas");
        return "gerente/detalleEmpresa";
    }

    @PostMapping("/{id}/desadjudicar")
    public String darBaja(@PathVariable Long id,
                          @RequestParam String motivo,
                          Model model, HttpSession session) {
        UsuarioResponseDTO usuario = cargarUsuarioEnModelo(session, model);
        try {
//            ocupacionLoteService.desadjudicar(id, motivo);
            empresaService.darDeBaja(id, motivo);
            return "redirect:/empresas";
        } catch (RuntimeException e) {
            EmpresaResponseDTO empresa = empresaService.buscarPorId(id);
            boolean tieneProyectosActivos = empresa.getProyectos().stream()
                    .anyMatch(p -> p.getEstado().equals("ACTIVO"));
            model.addAttribute("empresa", empresa);
            model.addAttribute("tieneProyectosActivos", tieneProyectosActivos);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "empresas");
            return "gerente/detalleEmpresa";
        }
    }
}