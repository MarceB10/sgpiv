package sgpiv.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudRequestDTO {

    // -------------------
    // DATOS EMPRESA
    // -------------------

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    private String cuitEmpresa;

    @NotBlank(message = "El rubro es obligatorio")
    private String rubro;

    @NotBlank(message = "El email es obligatorio")
    private String emailEmpresa;

    private String telefonoEmpresa;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String ingresoBrutos;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcionBienServicio;

    private String tipoIndustria;

    //
    // DATOS PROYECOT
    //

    @NotBlank(message = "El tipo de empresa no puede estar vacio")
    private String tipoEmpresa;

    @NotBlank(message = "El objetivo del proyecto no puede estar vacio")
    private String objetivoProyecto;

    @NotBlank(message = "La actividad principal no puede estar vacia")
    private String actividadPrincipal;

    private String actividadSecundaria;

    @NotNull(message = "Indicar la superficie necesaria en m2")
    private Double necesidadM2;

    @NotNull(message = "La superficie de trabajo no puede estar vacia")
    private Double supCubiertaTrabajoM2;

    @NotNull(message = "La superficie de deposito no puede estar vacia")
    private Double supCubiertaDepositoM2;

    private Double supExpansionM2;

    @NotNull
    private List<TareaSoliDTORequest> tareas;

    @NotNull(message = "Indique si tiene planos")
    private Boolean tienePlanos;

    @NotNull(message = "Indique el personal a ocupar")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    private Long personalAOcupar;

    @NotNull(message = "Indicar el tiempo de radicacion")
    private Integer tiempoDeRadicacion;


}