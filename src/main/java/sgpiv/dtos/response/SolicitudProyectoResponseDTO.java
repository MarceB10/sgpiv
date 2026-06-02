package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoSolicitudProyecto;
import sgpiv.enums.ServicioLote;
import sgpiv.model.SolicitudProyecto;
import sgpiv.model.TareaSolicitud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudProyectoResponseDTO {

    private Long id;
    private Long solicitudRadicacionId;

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
    private boolean generaResiduos;
    private String descripcionResiduos;
    private String produccionEstimada;
    private List<ServicioLote> serviciosRequeridos = new ArrayList<>();

    // Tareas
    private List<TareaSoliDTOResponse> tareas = new ArrayList<>();

    // Estado
    private EstadoSolicitudProyecto estado;
    private LocalDate fechaEnvio;
    private String motivoRechazo;

    // Usuario que presentó la solicitud
    private String nombreUsuario;
    private String apellidoUsuario;
    private String cuitUsuario;

    // Empresa de la solicitud de radicacion
    private String razonSocial;
    private String cuitEmpresa;

    public SolicitudProyectoResponseDTO(SolicitudProyecto sp) {
        this.id                    = sp.getId();
        this.solicitudRadicacionId = sp.getSolicitudRadicacion().getId();
        this.titulo                = sp.getTitulo();
        this.descripcion           = sp.getDescripcion();
        this.objetivo              = sp.getObjetivo();
        this.rubro                 = sp.getRubro();
        this.inversionEstimada     = sp.getInversionEstimada();
        this.actividadPrincipal    = sp.getActividadPrincipal();
        this.actividadSecundaria   = sp.getActividadSecundaria();
        this.personalAOcupar       = sp.getPersonalAOcupar();
        this.tiempoDeRadicacion    = sp.getTiempoDeRadicacion();
        this.supCubiertaTrabajoM2  = sp.getSupCubiertaTrabajoM2();
        this.supCubiertaDepositoM2 = sp.getSupCubiertaDepositoM2();
        this.supExpansionM2        = sp.getSupExpansionM2();
        this.tienePlanos           = sp.getTienePlanos();
        this.generaResiduos        = sp.isGeneraResiduos();
        this.descripcionResiduos   = sp.getDescripcionResiduos();
        this.produccionEstimada    = sp.getProduccionEstimada();
        this.serviciosRequeridos   = sp.getServiciosRequeridos();
        this.estado                = sp.getEstado();
        this.fechaEnvio            = sp.getFechaEnvio();
        this.motivoRechazo         = sp.getMotivoRechazo();

        // Datos del usuario via solicitud de radicacion
        if (sp.getSolicitudRadicacion().getUsuario() != null) {
            this.nombreUsuario   = sp.getSolicitudRadicacion().getUsuario().getNombre();
            this.apellidoUsuario = sp.getSolicitudRadicacion().getUsuario().getApellido();
            this.cuitUsuario     = sp.getSolicitudRadicacion().getUsuario().getCuit();
        }

        // Datos de empresa via solicitud de radicacion
        this.razonSocial = sp.getSolicitudRadicacion().getRazonSocial();
        this.cuitEmpresa = sp.getSolicitudRadicacion().getCuitEmpresa();

        // Tareas
        for (TareaSolicitud t : sp.getTareas()) {
            this.tareas.add(new TareaSoliDTOResponse(t));
        }
    }
}