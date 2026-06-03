package sgpiv.dtos.response;

import sgpiv.enums.ServicioLote;
import sgpiv.model.Proyecto;
import sgpiv.model.Tarea;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProyectoResponseDTO {
    private Long id;
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
    private List<ServicioLote> serviciosRequeridos;

    private String razonSocialEmpresa;
    private String cuitEmpresa;

    private List<TareaResponseDTO> tareas = new ArrayList<>();
    private long tareasCompletadas;
    private double porcentajeCompletado;
    private Double necesidadM2;

    private EstadoProyecto estado;

    private String nombreRepresentante;
    private String apellidoRepresentante;
    private String cuitRepresentante;
    private double progreso;

    public ProyectoResponseDTO(Proyecto p) {
        this.id                  = p.getId();
        this.titulo              = p.getTitulo();
        this.descripcion         = p.getDescripcion();
        this.objetivo            = p.getObjetivo();
        this.rubro               = p.getRubro();
        this.inversionEstimada   = p.getInversionEstimada();
        this.actividadPrincipal  = p.getActividadPrincipal();
        this.actividadSecundaria = p.getActividadSecundaria();
        this.personalAOcupar     = p.getPersonalAOcupar();
        this.tiempoDeRadicacion  = p.getTiempoDeRadicacion();
        this.supCubiertaTrabajoM2  = p.getSupCubiertaTrabajoM2();
        this.supCubiertaDepositoM2 = p.getSupCubiertaDepositoM2();
        this.supExpansionM2      = p.getSupExpansionM2();
        this.tienePlanos         = p.getTienePlanos();
        this.generaResiduos      = p.isGeneraResiduos();
        this.descripcionResiduos = p.getDescripcionResiduos();
        this.produccionEstimada  = p.getProduccionEstimada();
        this.serviciosRequeridos = p.getServiciosRequeridos();
        this.necesidadM2         = p.getNecesidadM2();

        this.estado              = p.getEstadoProyecto();


        if (p.getEmpresa() != null) {
            this.razonSocialEmpresa = p.getEmpresa().getRazonSocial();
            this.cuitEmpresa        = p.getEmpresa().getCuit();
        }

        if (p.getRepresentanteEmpresa() != null) {
            this.nombreRepresentante   = p.getRepresentanteEmpresa().getUsuario().getNombre();
            this.apellidoRepresentante = p.getRepresentanteEmpresa().getUsuario().getApellido();
            this.cuitRepresentante     = p.getRepresentanteEmpresa().getUsuario().getCuit();
        }

        for (Tarea t : p.getTareas()) {
            this.tareas.add(new TareaResponseDTO(t));
        }

        this.tareasCompletadas    = p.cantTareasCompletadas();
        this.progreso = p.obtenerProgreso();
    }
}

