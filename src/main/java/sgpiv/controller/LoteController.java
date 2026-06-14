package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sgpiv.dtos.request.LoteRequestDTO;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.response.OcupacionLoteResponseDTO;
import sgpiv.dtos.response.ProyectoResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.LoteService;
import sgpiv.service.OcupacionLoteService;
import sgpiv.service.ProyectoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor

public class LoteController {

    private final LoteService loteService;
    private final OcupacionLoteService ocupacionLoteService;
    private final ProyectoService proyectoService;

    @GetMapping("/gerente/lotes")
    public String listarLotes(
            @RequestParam(required = false) Boolean activa,
            Model model,
            HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";


        List<OcupacionLoteResponseDTO> ocupaciones =
                ocupacionLoteService.obtenerTodas();

        if (activa != null) {
            ocupaciones = ocupaciones.stream()
                    .filter(o -> o.isActiva() == activa)
                    .toList();
        }
        //Agregue Esto -------------------------------------------------------------------
        model.addAttribute("ocupaciones", ocupaciones);
        /// ----------------------------------------------------------------------------

        Map<Long, String> representantesPorLote = ocupaciones.stream()
                .filter(OcupacionLoteResponseDTO::isActiva)
                .filter(o -> o.getIdLote() != null)
                .filter(o -> o.getNombreRepresentante() != null || o.getApellidoRepresentante() != null)
                .collect(Collectors.toMap(
                        OcupacionLoteResponseDTO::getIdLote,
                        o -> ((o.getNombreRepresentante() != null ? o.getNombreRepresentante() : "") + " " +
                                (o.getApellidoRepresentante() != null ? o.getApellidoRepresentante() : "")).trim(),
                        (representanteExistente, representanteNuevo) -> representanteExistente
                ));

        model.addAttribute("representantesPorLote", representantesPorLote);

        List<LoteResponseDTO> lotes = loteService.obtenerTodosLosLotes();
        model.addAttribute("lotes", lotes);
        model.addAttribute("totalLotes", lotes.size());
        model.addAttribute("totalDisponibles",
                lotes.stream().filter(l -> "DISPONIBLE".equals(l.getEstadoLote().name())).count());
        model.addAttribute("totalEnUso",
                lotes.stream().filter(l -> "EN_USO".equals(l.getEstadoLote().name())).count());
        //Agregue Esto -------------------------------------------------------------------
        /// ----------------------------------------------------------------------------

        model.addAttribute("proyectos", proyectoService.obtenerProyectosPendientesDeLote());

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "gerente/lotes";
    }

    @GetMapping("/gerente/lotes/nuevo-lote")
    public String mostrarFormulario(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("loteRequestDTO", new LoteRequestDTO());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "formularioLote";
    }

    // POST /lotes/nuevo — procesar formulario
    @PostMapping("/gerente/lotes/nuevo-lote")
    public String crearLote(
            @Valid @ModelAttribute LoteRequestDTO loteRequestDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        UsuarioResponseDTO usuario =
                (UsuarioResponseDTO) session.getAttribute("usuario");

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "lotes");

            return "formularioLote";
        }

        loteService.definirLote(loteRequestDTO);
        redirectAttributes.addFlashAttribute("mensaje", "Lote creado correctamente.");
        return "redirect:/gerente/lotes";
    }

    //SOLICITUD DE RADICACION----------------------------------------------------
    @GetMapping("gerente/lotes/disponibles")
    public String lotesDisponibles(@RequestParam Float superficie,
                                   @RequestParam Long idProyecto,
                                   Model model,
                                   HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        List<LoteResponseDTO> lotes = loteService.obtenerLotesParaAdjudicar(idProyecto);
        model.addAttribute("lotes", lotes);
        model.addAttribute("superficie", superficie);
        model.addAttribute("idProyecto", idProyecto);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "gerente/adjudicarLote";
    }
    //-------------------Adjudicar Lote A Poyecto-----------------------------------------------------------
    @GetMapping("gerente/lotes/seleccionar/{idLote}")
    public String seleccionarLote(@PathVariable Long idLote,
                                  @RequestParam Long idProyecto,
                                  HttpSession session) {

        LoteResponseDTO lote = loteService.obtenerLoteParaAdjudicar(idLote);
        session.setAttribute("loteSeleccionado", lote);
        return "redirect:/gerente/proyectos/" + idProyecto + "/detalle";
    }

    @GetMapping("/gerente/proyectos/{id}/detalle")
    public String detalleProyecto(@PathVariable Long id,
                                  Model model,
                                  HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("proyecto", proyectoService.obtenerProyectoDTOPorId(id));
        model.addAttribute("usuario", usuario);
        return "gerente/detalleProcesoAdjudicacion";
    }


    // Confirmar adjudicación
    @PostMapping("/gerente/proyectos/{idProyecto}/confirmarLote")
    public String confirmarLote(@PathVariable Long idProyecto,
                                HttpSession session) {
        LoteResponseDTO lote = (LoteResponseDTO) session.getAttribute("loteSeleccionado");
        if (lote == null) return "redirect:/gerente/proyectos/" + idProyecto + "/detalle";


        ocupacionLoteService.ocuparLote(lote.getId(), idProyecto, LocalDate.now());
        session.removeAttribute("loteSeleccionado");
        return "redirect:/gerente/lotes";
    }

    //--------------Ocupacion Lotes-----------------------------------------
    @GetMapping("/ocupaciones")
    public String listarOcupaciones(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("ocupaciones", ocupacionLoteService.obtenerTodas());
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "gerente/tablaOcupaciones";
    }
    //-----------------------------------------------------------------------

    @GetMapping("/gerente/proyectos/{idProyecto}/adjudicarLote")
    public String adjudicarLote(
            @PathVariable Long idProyecto) {

        ProyectoResponseDTO proyecto =
                proyectoService.obtenerProyectoDTOPorId(idProyecto);

        double superficieNecesaria =
                proyecto.getNecesidadM2() == null
                        ? 0
                        : proyecto.getNecesidadM2();

        return "redirect:/gerente/lotes/disponibles"
                + "?superficie=" + superficieNecesaria
                + "&idProyecto=" + idProyecto;
    }

    @GetMapping("/gerente/lotes/{id}/editar")
    public String mostrarFormularioEditarLote(@PathVariable Long id, Model model, HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        LoteRequestDTO loteRequestDTO = loteService.obtenerLoteParaEditar(id);

        model.addAttribute("loteRequestDTO", loteRequestDTO);
        model.addAttribute("idLote", id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");

        return "gerente/editarLote";
    }

    @PostMapping("/gerente/lotes/{id}/editar")
    public String actualizarLote(@PathVariable Long id,
                                 @Valid @ModelAttribute LoteRequestDTO loteRequestDTO,
                                 BindingResult result, RedirectAttributes redirectAttributes,
                                 HttpSession session, Model model) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null) return "redirect:/";

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "lotes");
            model.addAttribute("idLote", id);
            return "gerente/editarLote";
        }

        loteService.actualizarLote(id, loteRequestDTO);
        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Lote actualizado correctamente."
        );

        return "redirect:/gerente/lotes";
    }

    //DESADJUDICACION DEL LOTE
    @GetMapping("/gerente/ocupaciones/{id}/detalle")
    public String detalleOcupacion(@PathVariable Long id,
                                   Model model,
                                   HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("ocupacion", ocupacionLoteService.obtenerPorId(id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "gerente/detalleOcupacion";
    }

    @PostMapping("/gerente/ocupaciones/{id}/desadjudicar")
    public String desadjudicarOcupacion(@PathVariable Long id,
                                        @RequestParam String motivo,
                                        Model model,
                                        HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        try {
            ocupacionLoteService.desadjudicar(id, motivo);
            return "redirect:/gerente/lotes";
        } catch (RuntimeException e) {
            model.addAttribute("ocupacion", ocupacionLoteService.obtenerPorId(id));
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("pagina", "lotes");
            return "gerente/detalleOcupacion";
        }
    }
}