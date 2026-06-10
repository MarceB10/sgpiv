package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardEmpresaDTO {

    private LoteResponseDTO loteAdjudicado;

    private BigDecimal inversionComprometida;
    private Integer empleoProyectado;

   private Integer tiempoRadicada;

   private Double metrosPorEmpleado;

   private BigDecimal inversionPorEmpleo;

   private BigDecimal valorLote;



}
