package sgpiv.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sgpiv.dtos.request.SolicitudOrganismoRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.model.SolicitudOrganismoPublico;
import sgpiv.repository.SolicitudOrganismoPublicoRepository;
import sgpiv.service.SolicitudOrganismoService;

@Controller
@RequestMapping("/solicitudOrganismo")
@RequiredArgsConstructor
public class SolicitudOrganismoController {

    private final SolicitudOrganismoService solicitudOrganismoService;
    private final SolicitudOrganismoPublicoRepository solicitudOrganismoPublicoRepository;

    @GetMapping
    public String formulario(HttpSession session, Model model) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/";

        model.addAttribute("solicitudOrganismoDTO", new SolicitudOrganismoRequestDTO());
        model.addAttribute("usuario", usuario);
        return "solicitudOrganismo";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute SolicitudOrganismoRequestDTO dto,
                          BindingResult result,
                          @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                          HttpSession session,
                          Model model) {

        if (result.hasErrors()) {
            UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
            model.addAttribute("usuario", usuario);
            return "solicitudOrganismo";
        }

        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        try {

            solicitudOrganismoService.guardar(dto, archivo, usuario.getCuit());
            return "redirect:/home";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("solicitudOrganismoDTO", dto);

            return "solicitudOrganismo";
        }
    }


    @GetMapping("/miSolicitudOrganismo/archivo")
    public ResponseEntity<byte[]> verMiArchivo(@RequestParam(defaultValue = "inline") String modo,
                                               HttpSession session) {
        UsuarioResponseDTO usuario = (UsuarioResponseDTO) session.getAttribute("usuario");
        if (usuario == null) return ResponseEntity.status(401).build();

        SolicitudOrganismoPublico solicitud = solicitudOrganismoPublicoRepository
                .findByUsuarioId(usuario.getId())
                .orElseThrow();

        if (solicitud.getArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        String disposition = modo.equals("download")
                ? "attachment; filename=\"" + solicitud.getNombreArchivo() + "\""
                : "inline; filename=\"" + solicitud.getNombreArchivo() + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .contentType(MediaType.parseMediaType(solicitud.getTipoArchivo()))
                .body(solicitud.getArchivo());
    }

}