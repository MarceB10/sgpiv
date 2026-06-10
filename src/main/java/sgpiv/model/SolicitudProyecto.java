package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sgpiv.enums.EstadoSolicitudProyecto;
import sgpiv.enums.ServicioLote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudProyecto {

    @Id @GeneratedValue
    private Long id;

    // Relacion con la solicitud de radicacion
    @OneToOne
    @JoinColumn(name = "solicitud_radicacion_id")
    private SolicitudRadicacion solicitudRadicacion;

    // Datos del proyecto

    private String titulo;
    private String descripcion;
    private String objetivo;
    private String rubro;
    private BigDecimal inversionEstimada;
    private String actividadPrincipal;
    private String actividadSecundaria;
    private Integer personalAOcupar;
    private Integer tiempoDeRadicacion;
    private Double supCubiertaTrabajoM2;
    private Double supCubiertaDepositoM2;
    private Double supExpansionM2;
    private Boolean tienePlanos;

    //Residuos --- si genera residuos tienen que explicar cuales
    private boolean generaResiduos;
    private String descripcionResiduos;

    private String produccionEstimada;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "solicitud_proyecto_servicios",
            joinColumns = @JoinColumn(name = "solicitud_proyecto_id")
    )
    @Column(name = "servicio")
    private List<ServicioLote> serviciosRequeridos;

    // Tareas
    @OneToMany(mappedBy = "solicitudProyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TareaSolicitud> tareas = new ArrayList<>();

    // Estado
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudProyecto estado = EstadoSolicitudProyecto.PENDIENTE;

    private LocalDate fechaEnvio;
    private String motivoRechazo;

}
    




