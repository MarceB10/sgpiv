package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sgpiv.dtos.response.OcupacionLoteResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.EstadoProyecto;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OcupacionLoteService {


    private final OcupacionLoteRepository ocupacionLoteRepository;
    private final RepresentanteRepository representanteRepository;
    private final LoteRepository loteRepository;
    private final EmpresaRepository empresaRepository;
    private final ProyectoRepository proyectoRepository;

    private final NotificacionService notificacionService;

    private final ProyectoService proyectoService;

    public static final String NOTIFICACION_LOTE_ADJUDICADO = "Se te ha adjudicado un Lote: \n" +
            "Ubicacion: ";


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

//    public void ocuparLote(Long idLote, Long idProyecto) {
//        ocuparLote(idLote, idProyecto, LocalDate.now());
//    }

//    public void ocuparLote(Long idLote, Long idProyecto, LocalDate fechaAdjudicacion) {
//        Lote lote = loteRepository
//                .findById(idLote)
//                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
//
//        Proyecto proyecto = proyectoService
//                .obtenerPorId(idProyecto);
//
//        lote.setEstadoLote(EstadoLote.EN_USO);
//        loteRepository.save(lote);
//        proyecto.getEmpresa().setEstadoEmpresa(EstadoEmpresa.RADICADA);
//        empresaRepository.save(proyecto.getEmpresa());
//        OcupacionLote ocupacionLote = new OcupacionLote(proyecto, lote, fechaAdjudicacion);
//        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);
//        ocupacionLoteRepository.save(ocupacionLote);
//
//        notificacionService.crearNotificacion(
//                "Se te ha adjudicado un Lote: \n" +
//                        "Ubicacion: " + lote.getUbicacion(),
//                proyecto.getRepresentanteEmpresa().getUsuario()
//        );
//    }

    public void ocuparLote(Long idLote, Long idProyecto, LocalDate fechaAdjudicacion) {
        Lote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        Proyecto proyecto = proyectoService.obtenerPorId(idProyecto);

        proyecto.getEmpresa().setEstadoEmpresa(EstadoEmpresa.RADICADA);
        empresaRepository.save(proyecto.getEmpresa());

        OcupacionLote ocupacion = new OcupacionLote();
        ocupacion.setLote(lote);
        ocupacion.setProyecto(proyecto);
        ocupacion.setFechaInicio(fechaAdjudicacion);

        ocupacion.setFechaFin(null);

        lote.setEstadoLote(EstadoLote.EN_USO);
        lote.setFechaAdjudicacion(fechaAdjudicacion);
        lote.setFechaUso(fechaAdjudicacion);

        proyecto.setEstadoProyecto(EstadoProyecto.ACTIVO);

        proyectoRepository.save(proyecto);
        ocupacionLoteRepository.save(ocupacion);
        loteRepository.save(lote);

        notificacionService.crearNotificacion(
                 NOTIFICACION_LOTE_ADJUDICADO +
                         lote.getUbicacion(),
                proyecto.getRepresentanteEmpresa().getUsuario()
        );
    }

    @Transactional
    public void desadjudicar(Long ocupacionId, String motivo) {

        // 1. Buscar la ocupacion
        OcupacionLote ocupacionActiva = ocupacionLoteRepository
                .findById(ocupacionId)
                .orElseThrow(() -> new RuntimeException("Ocupación no encontrada"));

        //1.5 traer la empresa
        Empresa empresa = ocupacionActiva
                .getProyecto()
                .getEmpresa();

        System.out.println("Id E: " + empresa.getId());
        System.out.println("Empresa: " + empresa.getRazonSocial());
        System.out.println("Estado: " + empresa.getEstadoEmpresa());

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


        // 5. Cerrar la ocupacion
        ocupacionActiva.setFechaFin(LocalDate.now());
        ocupacionActiva.setMotivoFinalizacion(motivo);
        ocupacionLoteRepository.save(ocupacionActiva);

        // 6. Liberar el lote
        Lote lote = ocupacionActiva.getLote();
        lote.habilitarDisponibilidad();
        loteRepository.save(lote);

        // new 7. Volver a un estado consistente la empresa
        empresa.setEstadoEmpresa(EstadoEmpresa.PENDIENTE_LOTE);
        empresaRepository.save(empresa);


//        // Suspender todos los proyectos de la empresa
//        empresa.getProyectos().forEach(proyecto -> {
//            proyecto.setEstadoProyecto(EstadoProyecto.INACTIVO);
//        });
//        empresaRepository.save(empresa);
//
//        // 7. Dar de baja la empresa
//        empresa.setEstadoEmpresa(EstadoEmpresa.BAJA);
//        empresaRepository.save(empresa);
//
//        // 8. Desactivar representante
//        representanteRepository.findByEmpresa(empresa)
//                .ifPresent(rep -> {
//                    rep.getUsuario().desactivar();
//                    representanteRepository.save(rep);
//                });

        notificacionService.crearNotificacion(
                "Se te ha desadjudicado el lote del parque\n" +
                        "Motivo: " + motivo + ".\n Lote" + lote.getUbicacion() + " liberado.",
                ocupacionActiva.getProyecto().getRepresentanteEmpresa().getUsuario());
    }

    public boolean existeOcupacion(Empresa empresa){

        OcupacionLote ocupacionActiva = ocupacionLoteRepository
                .findByProyecto_EmpresaAndFechaFinIsNull(empresa)
                .orElseThrow(() -> new RuntimeException(
                        "No hay ocupacion activa para esta empresa"));

        return (ocupacionActiva != null);
    }

    public OcupacionLoteResponseDTO obtenerOcupacionDeEmpresa(Empresa empresa){
        OcupacionLote ocupacion = ocupacionLoteRepository
                .findOcupacionActiva(empresa.getId())
                .orElseThrow(() -> new RuntimeException("no se encuentra una ocupacion activa"));

        return new OcupacionLoteResponseDTO(ocupacion);

    }

    public OcupacionLoteResponseDTO obtenerPorId(Long id) {
        return new OcupacionLoteResponseDTO(
                ocupacionLoteRepository.findById(id).orElseThrow()
        );
    }

    // Nuevo método opcional
    public Optional<OcupacionLoteResponseDTO> obtenerOcupacionDeEmpresaOpcional(Long empresaId) {
        return ocupacionLoteRepository
                .findOcupacionActiva(empresaId)
                .map(OcupacionLoteResponseDTO::new);
    }
}
