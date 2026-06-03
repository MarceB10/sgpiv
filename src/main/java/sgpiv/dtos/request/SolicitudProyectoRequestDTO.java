package sgpiv.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.ServicioLote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudProyectoRequestDTO {

    // Referencia a la solicitud de radicacion
    @NotNull(message = "La solicitud de radicación es obligatoria")
    private Long solicitudRadicacionId;  //

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivo;

    @NotBlank(message = "El rubro es obligatorio")
    private String rubro;

    @NotNull(message = "La inversión estimada es obligatoria")
    private BigDecimal inversionEstimada;

    @NotBlank(message = "La actividad principal es obligatoria")
    private String actividadPrincipal;

    private String actividadSecundaria;

    @NotNull(message = "El personal a ocupar es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    private Integer personalAOcupar;

    @NotNull(message = "El tiempo de radicación es obligatorio")
    private Integer tiempoDeRadicacion;

    @NotNull(message = "La superficie de trabajo es obligatoria")
    private Double supCubiertaTrabajoM2;

    @NotNull(message = "La superficie de depósito es obligatoria")
    private Double supCubiertaDepositoM2;

    private Double supExpansionM2;

    @NotNull(message = "Indique si tiene planos")
    private Boolean tienePlanos;

    @NotNull(message = "Indique si genera residuos")
    private Boolean generaResiduos;

    // Solo obligatorio si generaResiduos es true, validarlo en el service
    private String descripcionResiduos;

    private String produccionEstimada;

    private List<ServicioLote> serviciosRequeridos = new ArrayList<>();

    @NotNull(message = "Debe agregar al menos una tarea")
    @Size(min = 1, message = "Debe agregar al menos una tarea")
    private List<TareaSoliDTORequest> tareas = new ArrayList<>();
}
