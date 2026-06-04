package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoInfraestructura;
import sgpiv.enums.TipoInfraestructura;
import sgpiv.model.Infraestructura;
import sgpiv.repository.InfraestructuraRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InfraestructuraController {

    private final InfraestructuraRepository infraestructuraRepository;

    @GetMapping("/infraestructuras")
    public String mostrarGestionInfraestructura(Model model, HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/";
        }

        List<Infraestructura> infraestructuras = infraestructuraRepository.findAll();

        long totalInfraestructuras = infraestructuras.size();

        long operativas = infraestructuras.stream()
                .filter(infraestructura -> infraestructura.getEstado() == EstadoInfraestructura.OPERATIVA)
                .count();

        long enMantenimiento = infraestructuras.stream()
                .filter(infraestructura ->
                        infraestructura.getEstado() == EstadoInfraestructura.EN_MANTENIMIENTO
                                || infraestructura.getEstado() == EstadoInfraestructura.REQUIERE_MANTENIMIENTO
                )
                .count();

        long fueraDeServicio = infraestructuras.stream()
                .filter(infraestructura -> infraestructura.getEstado() == EstadoInfraestructura.FUERA_DE_SERVICIO)
                .count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "infraestructuras");

        model.addAttribute("infraestructuras", infraestructuras);

        model.addAttribute("totalInfraestructuras", totalInfraestructuras);
        model.addAttribute("infraestructurasOperativas", operativas);
        model.addAttribute("infraestructurasMantenimiento", enMantenimiento);
        model.addAttribute("infraestructurasFueraServicio", fueraDeServicio);

        model.addAttribute("cantidadElectricas", contarPorTipo(infraestructuras, TipoInfraestructura.ELECTRICA));
        model.addAttribute("cantidadAguaPotable", contarPorTipo(infraestructuras, TipoInfraestructura.AGUA_POTABLE));
        model.addAttribute("cantidadAlumbradoPublico", contarPorTipo(infraestructuras, TipoInfraestructura.ALUMBRADO_PUBLICO));
        model.addAttribute("cantidadGasNatural", contarPorTipo(infraestructuras, TipoInfraestructura.GAS_NATURAL));
        model.addAttribute("cantidadTelecomunicaciones", contarPorTipo(infraestructuras, TipoInfraestructura.TELECOMUNICACIONES));
        model.addAttribute("cantidadAguaResidual", contarPorTipo(infraestructuras, TipoInfraestructura.AGUA_RESIDUAL));
        model.addAttribute("cantidadRedCloacal", contarPorTipo(infraestructuras, TipoInfraestructura.RED_CLOACAL));
        model.addAttribute("cantidadVial", contarPorTipo(infraestructuras, TipoInfraestructura.VIAL));
        model.addAttribute("cantidadSeguridad", contarPorTipo(infraestructuras, TipoInfraestructura.SEGURIDAD));

        return "gerente/gestionInfraestructura";
    }

    private long contarPorTipo(List<Infraestructura> infraestructuras, TipoInfraestructura tipo) {
        return infraestructuras.stream()
                .filter(infraestructura -> infraestructura.getTipo() == tipo)
                .count();
    }

    @GetMapping("/infraestructuras/nueva")
    public String mostrarFormularioNuevaInfraestructura(Model model, HttpSession session) {

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagina", "infraestructuras");

        model.addAttribute("infraestructura", new Infraestructura());
        model.addAttribute("tipos", TipoInfraestructura.values());
        model.addAttribute("estados", EstadoInfraestructura.values());

        return "gerente/formularioInfraestructura";
    }

    @PostMapping("/infraestructuras")
    public String guardarInfraestructura(@ModelAttribute Infraestructura infraestructura, HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";
        infraestructuraRepository.save(infraestructura);
        return "redirect:/infraestructuras";
    }
}