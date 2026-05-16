package sgpiv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SolicitudRadicacionController {
    @GetMapping("/solicitudRadicacion")
    public String solicitudRadicacion() {
        return "solicitudRadicacion";
    }
}
