package sgpiv.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.model.Empresa;
import sgpiv.service.EmpresaService;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor

public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public Empresa registrar(@RequestBody @Valid Empresa empresa){
        return empresaService.registrar(empresa);
    }

    @GetMapping
    public List<Empresa> listarTodas(){
        return empresaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Empresa buscarPorId(@PathVariable Long id){
        return empresaService.buscarPorId(id);
    }

    @GetMapping("/estado/{estado}")
    public List<Empresa> listarPorEstado(@PathVariable EstadoEmpresa estado){
        return empresaService.listarPorEstado(estado);
    }

    @PutMapping("/{id}/radicar")
    public Empresa radicar(@PathVariable Long id){
        return empresaService.radicar(id);
    }

    @PutMapping("/{id}/baja")
    public Empresa darDeBaja(@PathVariable Long id){
        return empresaService.darDeBaja(id);
    }

}
