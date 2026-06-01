package sgpiv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sgpiv.service.TareaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;

    @PostMapping("/{id}/toggle")
    public String toggleTarea(
            @PathVariable Long id,
            @RequestHeader("Referer") String referer) {

        tareaService.toggleCompleta(id);

        return "redirect:" + referer;
    }
}