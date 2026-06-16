//package sgpiv.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import sgpiv.dtos.request.EmpresaRequestDTO;
//import sgpiv.dtos.response.EmpresaResponseDTO;
//import sgpiv.enums.EstadoEmpresa;
//import sgpiv.service.EmpresaService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/empresas")
//@RequiredArgsConstructor
//
//public class EmpresaRestController {
//
//    private final EmpresaService empresaService;
//
////    @PostMapping
////    public EmpresaResponseDTO registrar(
////            @RequestBody @Valid EmpresaRequestDTO dto) {
////
////        return empresaService.registrar(dto);
////    }
//
//    @GetMapping
//    public List<EmpresaResponseDTO> listarTodas() {
//        return empresaService.listarTodas();
//    }
//
//    @GetMapping("/{id}")
//    public EmpresaResponseDTO buscarPorId(
//            @PathVariable Long id) {
//
//        return empresaService.buscarPorId(id);
//    }
//    @GetMapping("/estado/{estado}")
//    public List<EmpresaResponseDTO> listarPorEstado(
//            @PathVariable EstadoEmpresa estado) {
//
//        return empresaService.listarPorEstado(estado);
//    }
//
//    @PutMapping("/{id}/radicar")
//    public EmpresaResponseDTO radicar(
//            @PathVariable Long id) {
//
//        return empresaService.radicar(id);
//    }
////    @PutMapping("/{id}/baja")
////    public EmpresaResponseDTO baja(@PathVariable Long id) {
////
////        return empresaService.darDeBaja(id);
////    }
//}
