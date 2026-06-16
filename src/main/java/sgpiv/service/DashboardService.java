package sgpiv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sgpiv.dtos.response.DashboardGeneralDTO;
import sgpiv.dtos.response.DashboardEmpresaDTO;
import sgpiv.dtos.response.LoteResponseDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.*;
import sgpiv.model.*;
import sgpiv.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SolicitudRadicacionRepository solicitudRadicacionRepository;
    private final EmpresaRepository empresaRepository;
    private final ProyectoRepository proyectoRepository;
    private final LoteRepository loteRepository;
    private final OcupacionLoteRepository ocupacionLoteRepository;
    private final RepresentanteRepository representanteRepository;

    public DashboardGeneralDTO obtenerMetricasGenerales() {

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
                        .filter(p -> p.getEmpresa().getEstadoEmpresa() != EstadoEmpresa.BAJA)
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

        BigDecimal inversionPorEmpleo = inversionProyectada.divide(
                BigDecimal.valueOf(empleoProyectado),
                2,
                RoundingMode.HALF_UP
        );

        DashboardGeneralDTO dashboard = new DashboardGeneralDTO();

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
        dashboard.setInversionPorEmpleo(inversionPorEmpleo);

        return dashboard;
    }

    public DashboardEmpresaDTO obtenerMetricasDeMiEmpresa(UsuarioResponseDTO usuario){

        RepresentanteEmpresa representante = representanteRepository
                .findByUsuario_Cuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

        Proyecto proyecto = proyectoRepository
                .findByEmpresa_Id(representante.getEmpresa().getId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));


        LoteResponseDTO loteAdjudicado = obtenerLoteAdjudicado(usuario);

        BigDecimal inversionComprometida = proyecto.getInversionEstimada();
        Integer empleoProyectado = proyecto.getPersonalAOcupar();

        DashboardEmpresaDTO dashboardRepresentante = new DashboardEmpresaDTO();

        dashboardRepresentante.setLoteAdjudicado(loteAdjudicado);
        dashboardRepresentante.setInversionComprometida(inversionComprometida);
        dashboardRepresentante.setEmpleoProyectado(empleoProyectado);
        dashboardRepresentante.setInversionPorEmpleo(
                obtenerInversionPorEmpleo(proyecto)
        );

        if (loteAdjudicado != null) {

            OcupacionLote ocupacionLote =
                    ocupacionLoteRepository
                            .findOcupacionActiva(representante.getEmpresa().getId())
                            .orElseThrow();

            Lote lote =
                    loteRepository
                            .findById(loteAdjudicado.getId())
                            .orElseThrow();

            dashboardRepresentante.setTiempoRadicada(
                    tiempoRadicada(ocupacionLote)
            );

            dashboardRepresentante.setMetrosPorEmpleado(
                    obtenerMetrosPorEmpleado(proyecto, lote)
            );


            dashboardRepresentante.setValorLote(
                    BigDecimal.valueOf(lote.getPrecio())
            );
        }


        return dashboardRepresentante;

    }


    public LoteResponseDTO obtenerLoteAdjudicado(UsuarioResponseDTO usuario){


        RepresentanteEmpresa representante = representanteRepository
                .findByUsuario_Cuit(usuario.getCuit())
                .orElseThrow(() -> new RuntimeException("Usuario No Encontrado"));

        log.info("=== DEBUG ===");
        log.info("CUIT usuario: {}", usuario.getCuit());
        log.info("Representante ID: {}", representante.getId());
        log.info("Empresa ID: {}", representante.getEmpresa().getId());


        Optional<OcupacionLote> ocupacionLote = ocupacionLoteRepository
                .findOcupacionActiva(representante.getEmpresa().getId());

        log.info("Ocupacion encontrada: " + ocupacionLote.isPresent());

        if (ocupacionLote.isEmpty()){
            return null;
        }

        Lote lote = ocupacionLote.get().getLote();

        return new LoteResponseDTO(lote, ocupacionLote.get().getFechaInicio());
    }

    public int obtenerEmpleoProyectado() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getPersonalAOcupar() != null && p.getEmpresa().getEstadoEmpresa() != EstadoEmpresa.BAJA)
                .mapToInt(Proyecto::getPersonalAOcupar)
                .sum();
    }

    public Map<String, Long> obtenerServiciosDemandados() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getEmpresa().getEstadoEmpresa() != EstadoEmpresa.BAJA)
                .flatMap(p -> p.getServiciosRequeridos().stream())
                .collect(Collectors.groupingBy(
                        ServicioLote::name,
                        Collectors.counting()
                ));
    }


    public BigDecimal obtenerInversionTotal() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getEmpresa().getEstadoEmpresa() != EstadoEmpresa.BAJA)
                .map(Proyecto::getInversionEstimada)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> obtenerInversionPorRubro() {

        return proyectoRepository.findAll()
                .stream()
                .filter(p -> p.getEmpresa().getEstadoEmpresa() != EstadoEmpresa.BAJA)
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


    public Integer tiempoRadicada(OcupacionLote ocupacionLote){
        if (ocupacionLote == null){
            throw new RuntimeException("no existe un loteadjudicado a tu empresa");
        }

        return Math.toIntExact(ChronoUnit.DAYS.between(
                ocupacionLote.getFechaInicio(),
                LocalDate.now()
        ));
    }

    public Double obtenerMetrosPorEmpleado(
            Proyecto proyecto,
            Lote lote){

        if(proyecto.getPersonalAOcupar() == null
                || proyecto.getPersonalAOcupar() == 0
                || lote == null
                || lote.getSuperficie() == null){
            return null;
        }

        return lote.getSuperficie()
                / proyecto.getPersonalAOcupar();
    }

    public BigDecimal obtenerInversionPorEmpleo(Proyecto proyecto){

        if(proyecto.getPersonalAOcupar() == null
                || proyecto.getPersonalAOcupar() == 0){
            return BigDecimal.ZERO;
        }

        return proyecto.getInversionEstimada()
                .divide(
                        BigDecimal.valueOf(
                                proyecto.getPersonalAOcupar()
                        ),
                        2,
                        RoundingMode.HALF_UP
                );
    }

}