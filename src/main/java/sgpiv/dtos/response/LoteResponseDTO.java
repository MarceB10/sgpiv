package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoLote;
import sgpiv.enums.ServicioLote;
import sgpiv.model.Lote;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteResponseDTO {

    private Long id;
    private Double superficie;
    private String ubicacion;
    private Float precio;
    private LocalDate fechaUso;
    private LocalDate fechaAdjudicacion;
    private String restricciones;
    private EstadoLote estadoLote;
    private Set<ServicioLote> servicios;

    public LoteResponseDTO(Lote lote) {
        this.id               = lote.getId();
        this.superficie       = lote.getSuperficie();
        this.ubicacion        = lote.getUbicacion();
        this.precio           = lote.getPrecio();
        this.fechaUso         = lote.getFechaUso();
        this.fechaAdjudicacion = lote.getFechaAdjudicacion();
        this.restricciones    = lote.getRestricciones();
        this.estadoLote       = lote.getEstadoLote();
        this.servicios        = lote.getServicios();
    }

}
