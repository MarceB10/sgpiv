package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.response.OcupacionLoteResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.EstadoProyecto;
import sgpiv.model.*;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.LoteRepository;
import sgpiv.repository.OcupacionLoteRepository;
import sgpiv.repository.RepresentanteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcupacionLoteService {

    private final OcupacionLoteRepository ocupacionLoteRepository;
    private final RepresentanteRepository representanteRepository;
    private final LoteRepository loteRepository;
    private final EmpresaRepository empresaRepository;

    private final NotificacionService notificacionService;

    private final ProyectoService proyectoService;


    public List<OcupacionLoteResponseDTO> obtenerTodas() {
        List<OcupacionLote> ocupaciones = ocupacionLoteRepository.findAll();
        List<OcupacionLoteResponseDTO> resultado = new ArrayList<>();

        for (OcupacionLote ocupacion : ocupaciones) {
//            RepresentanteEmpresa rep = representanteRepository
//                    .findByEmpresa(ocupacion.getProyecto().getEmpresa())
//                    .orElse(null);
//
//            String nombre   = rep != null ? rep.getUsuario().getNombre()   : "-";
//            String apellido = rep != null ? rep.getUsuario().getApellido() : "-";
//            String cuit     = rep != null ? rep.getUsuario().getCuit()     : "-";

            resultado.add(new OcupacionLoteResponseDTO(ocupacion));
        }

        return resultado;
    }

    public void ocuparLote(Long idLote, Long idProyecto) {
        ocuparLote(idLote, idProyecto, LocalDate.now());
    }

    public void ocuparLote(Long idLote, Long idProyecto, LocalDate fechaAdjudicacion) {
        Lote lote = loteRepository
                .findById(idLote)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        Proyecto proyecto = proyectoService
                .obtenerPorId(idProyecto);

        lote.setEstadoLote(EstadoLote.EN_USO);
        loteRepository.save(lote);
        proyecto.getEmpresa().setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(proyecto.getEmpresa());
        OcupacionLote ocupacionLote = new OcupacionLote(proyecto, lote, fechaAdjudicacion);
        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);
        ocupacionLoteRepository.save(ocupacionLote);

        notificacionService.crearNotificacion(
                "Se te ha adjudicado un Lote: \n" +
                        "Ubicacion: " + lote.getUbicacion(),
                proyecto.getRepresentanteEmpresa().getUsuario()
        );
    }

    @Transactional
    public void desadjudicar(Long empresaId, String motivo) {

        // 1. Buscar la empresa
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        // 2. Verificar estado
        if (empresa.getEstadoEmpresa() != EstadoEmpresa.RADICADA &&
                empresa.getEstadoEmpresa() != EstadoEmpresa.ADJUDICADA) {
            throw new RuntimeException("La empresa no está radicada en el parque");
        }

//        Para implementar mas adelante
//        // 3. Verificar que no tenga proyectos activos
//        boolean tieneProyectosActivos = empresa.getProyectos().stream()
//                .anyMatch(p -> p.getEstadoProyecto() == EstadoProyecto.ACTIVO);
//
//        if (tieneProyectosActivos) {
//            throw new RuntimeException(
//                    "La empresa tiene proyectos activos. Finalizalos antes de desadjudicar");
//        }

        // 4. Buscar la ocupacion activa
        OcupacionLote ocupacionActiva = ocupacionLoteRepository
                .findByProyecto_EmpresaAndFechaFinIsNull(empresa)
                .orElseThrow(() -> new RuntimeException(
                        "No hay ocupacion activa para esta empresa"));

        // 5. Cerrar la ocupacion
        ocupacionActiva.setFechaFin(LocalDate.now());
        ocupacionActiva.setMotivoFinalizacion(motivo);
        ocupacionLoteRepository.save(ocupacionActiva);

        // 6. Liberar el lote
        Lote lote = ocupacionActiva.getLote();
        lote.habilitarDisponibilidad();
        loteRepository.save(lote);

        // Suspender todos los proyectos de la empresa
        empresa.getProyectos().forEach(proyecto -> {
            proyecto.setEstadoProyecto(EstadoProyecto.INACTIVO);
        });
        empresaRepository.save(empresa);

        // 7. Dar de baja la empresa
        empresa.setEstadoEmpresa(EstadoEmpresa.BAJA);
        empresaRepository.save(empresa);

        // 8. Desactivar representante
        representanteRepository.findByEmpresa(empresa)
                .ifPresent(rep -> {
                    rep.getUsuario().desactivar();
                    representanteRepository.save(rep);
                });
    }

}
