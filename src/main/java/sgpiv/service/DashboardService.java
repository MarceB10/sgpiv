package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.DashboardDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.ServicioLote;
import sgpiv.model.Proyecto;
import sgpiv.repository.EmpresaRepository;
import sgpiv.repository.LoteRepository;
import sgpiv.repository.ProyectoRepository;
import sgpiv.repository.SolicitudRadicacionRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final EmpresaRepository empresaRepository;
    private final ProyectoRepository proyectoRepository;
    private final LoteRepository loteRepository;

    public DashboardDTO obtenerMetricas() {

        long totalEmpresas =
                empresaRepository.count();

        long totalProyectos =
                proyectoRepository.count();

        long disponibles =
                loteRepository
                        .findDisponibles()
                        .size();

        long ocupados =
                loteRepository
                        .findByEstadoLote(EstadoLote.EN_USO)
                        .size();

        long totalLotes = disponibles + ocupados;

        double porcentaje = totalLotes == 0
                ? 0
                : (ocupados * 100.0 / totalLotes);


        long pendientes =

                solicitudRadicacionRepository.findByEstado(EstadoSolicitud.PENDIENTE).size()
                        +
                        solicitudRadicacionRepository.findByEstado(EstadoSolicitud.PENDIENTE_PROYECTO).size();

        long pendientesLote =
                empresaRepository
                        .findByEstadoEmpresa(EstadoEmpresa.PENDIENTE_LOTE)
                        .size();

        long radicadas =
                empresaRepository
                        .findByEstadoEmpresa(EstadoEmpresa.RADICADA)
                        .size();

        Map<String, Long> rubros =
                proyectoRepository.findAll()
                        .stream()
                        .filter(p -> p.getRubro() != null)
                        .collect(
                                Collectors.groupingBy(
                                        p -> p.getRubro().trim().toUpperCase(),
                                        Collectors.counting()
                                )
                        );

        Integer empleoProyectado = obtenerEmpleoProyectado();

        Map<String, Long> servicios = obtenerServiciosDemandados();

        BigDecimal inversionProyectada = obtenerInversionTotal();

        return new DashboardDTO(
                totalEmpresas,
                totalProyectos,
                disponibles,
                ocupados,
                porcentaje,
                pendientes,
                pendientesLote,
                radicadas,
                rubros,
                empleoProyectado,
                servicios,
                inversionProyectada
        );
    }

    public int obtenerEmpleoProyectado() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getPersonalAOcupar() != null)
                .mapToInt(Proyecto::getPersonalAOcupar)
                .sum();
    }

    public Map<String, Long> obtenerServiciosDemandados() {

        return proyectoRepository.findAll()
                .stream()
                .flatMap(p -> p.getServiciosRequeridos().stream())
                .collect(Collectors.groupingBy(
                        ServicioLote::name,
                        Collectors.counting()
                ));
    }


    public BigDecimal obtenerInversionTotal() {

        return proyectoRepository.findAll()
                .stream()
                .map(Proyecto::getInversionEstimada)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
