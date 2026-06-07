package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardGeneralDTO {

    //DATOS DE GERENTE, ORG PUBLICO----------------
    private long totalEmpresas;
    private long totalProyectos;

    private long lotesDisponibles;
    private long lotesOcupados;

    private double porcentajeOcupacion;

    private long empresasPendientes;
    private long empresasPendientesLote;
    private long empresasRadicadas;

    private Map<String, Long> empresasPorRubro;

    private Integer empleoProyectado;

    private Map<String, Long> serviciosDemandados;

    private BigDecimal inversionTotalEstimada;

    private Map<String, BigDecimal> inversionPorRubro;

    // map de radicaciones por mes, dentro de un map con radicaciones anuales
    private Map<Integer, long[]> radicacionesPorAnio;


}
