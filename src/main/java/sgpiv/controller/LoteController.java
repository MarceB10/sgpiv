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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sgpiv.dtos.request.LoteRequestDTO;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.service.LoteService;

import java.util.List;

@Controller
@RequiredArgsConstructor

public class LoteController {

    private final LoteService loteService;

    @GetMapping("/gerente/lotes")
    public String listarLotes(Model model, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        List<LoteResponseDTO> lotes = loteService.obtenerTodosLosLotes();
        model.addAttribute("lotes", lotes);
        model.addAttribute("totalLotes", lotes.size());
        model.addAttribute("totalDisponibles",
                lotes.stream().filter(l -> "DISPONIBLE".equals(l.getEstadoLote().name())).count());
        model.addAttribute("totalEnUso",
                lotes.stream().filter(l -> "EN_USO".equals(l.getEstadoLote().name())).count());
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
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "formularioLote";
        }

        loteService.definirLote(loteRequestDTO);
        redirectAttributes.addFlashAttribute("mensaje", "Lote creado correctamente.");
        return "redirect:/gerente/lotes";
    }

    //SOLICITUD DE RADICACION----------------------------------------------------
    @GetMapping("gerente/lotes/disponibles")
    public String lotesDisponibles(@RequestParam Float superficie,
                                   Model model,
                                   HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        List<LoteResponseDTO> lotes = loteService.obtenerLotesParaSolicitud(superficie);
        model.addAttribute("lotes", lotes);
        model.addAttribute("superficie", superficie);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "lotes");
        return "gerente/adjudicarLote";
    }
    //------------------------------------------------------------------------------


}
