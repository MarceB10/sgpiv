package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.DashboardDTO;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.EstadoEmpresa;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.enums.ServicioLote;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final EmpresaRepository empresaRepository;
    private final ProyectoRepository proyectoRepository;
    private final LoteRepository loteRepository;
    private final OcupacionLoteRepository ocupacionLoteRepository;
    private final RepresentanteRepository representanteRepository;

    public DashboardDTO obtenerMetricasGenerales() {

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

        Map<String, BigDecimal> inversionPorRubro = obtenerInversionPorRubro();

        Map<Integer, long[]> radicacionesPorAnio = obtenerRadicacionesPorAnio();

        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setTotalEmpresas(totalEmpresas);
        dashboard.setTotalProyectos(totalProyectos);
        dashboard.setLotesDisponibles(disponibles);
        dashboard.setLotesOcupados(ocupados);
        dashboard.setPorcentajeOcupacion(porcentaje);
        dashboard.setEmpresasPendientes(pendientes);
        dashboard.setEmpresasPendientesLote(pendientesLote);
        dashboard.setEmpresasRadicadas(radicadas);
        dashboard.setEmpresasPorRubro(rubros);
        dashboard.setEmpleoProyectado(empleoProyectado);
        dashboard.setServiciosDemandados(servicios);
        dashboard.setInversionTotalEstimada(inversionProyectada);
        dashboard.setInversionPorRubro(inversionPorRubro);
        dashboard.setRadicacionesPorAnio(radicacionesPorAnio);

        return dashboard;
    }

    public DashboardDTO obtenerMetricasDeMiEmpresa(UsuarioResponseDTO usuario){

        LoteResponseDTO loteAdjudicado = obtenerLoteAdjudicado(usuario);

        DashboardDTO dashboardRepresentante = new DashboardDTO();

        dashboardRepresentante.setLoteAdjudicado(loteAdjudicado);

        return dashboardRepresentante;

    }


    public LoteResponseDTO obtenerLoteAdjudicado(UsuarioResponseDTO usuario){
        RepresentanteEmpresa representante = representanteRepository
                .findByUsuario_Cuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario No Encontrado"));

        Proyecto proyecto = proyectoRepository
                .findByEmpresaIdAndRepresentanteId(representante.getEmpresa().getId(), representante.getId())
                .orElseThrow(() -> new RuntimeException("Proyecto No Encontrado"));

        Optional<OcupacionLote> ocupacionLote = ocupacionLoteRepository
                .findByProyecto(proyecto);

        if (ocupacionLote.isEmpty()){
            return null;
        }

        Lote lote = ocupacionLote.get().getLote();

        return new LoteResponseDTO(lote);
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

    public Map<String, BigDecimal> obtenerInversionPorRubro() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getRubro() != null)
                .collect(Collectors.groupingBy(

                        p -> p.getRubro().trim().toUpperCase(),

                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> p.getInversionEstimada() != null
                                        ? p.getInversionEstimada()
                                        : BigDecimal.ZERO,
                                BigDecimal::add
                        )
                ));
    }


    public Map<Integer, long[]> obtenerRadicacionesPorAnio() {

        Map<Integer, long[]> resultado = new TreeMap<>();

        for (OcupacionLote ocupacion : ocupacionLoteRepository.findAll()) {

            int anio = ocupacion.getFechaInicio().getYear();

            int mes = ocupacion.getFechaInicio().getMonthValue() - 1;

            resultado.putIfAbsent(anio, new long[12]);

            resultado.get(anio)[mes]++;
        }

        return resultado;
    }


}
