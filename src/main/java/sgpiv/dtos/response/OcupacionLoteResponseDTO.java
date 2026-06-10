package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.OcupacionLote;
import sgpiv.model.Proyecto;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionLoteResponseDTO {

    private Long id;

    // Lote
    private Long idLote;
    private String ubicacionLote;
    private Double superficieLote;

    // Empresa (via proyecto)
    private String razonSocialEmpresa;
    private String cuitEmpresa;

    // Representante (via proyecto -> usuario)
    private String nombreRepresentante;
    private String apellidoRepresentante;
    private String cuitRepresentante;

    // Proyecto
    private String tituloProyecto;  // por si lo necesitás mostrar

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    //ACTIVA
    private boolean activa;

    public OcupacionLoteResponseDTO(OcupacionLote ocupacion) {
        this.id               = ocupacion.getId();
        this.idLote           = ocupacion.getLote().getId();
        this.ubicacionLote    = ocupacion.getLote().getUbicacion();
        this.superficieLote   = ocupacion.getLote().getSuperficie();
        this.fechaInicio      = ocupacion.getFechaInicio();
        this.fechaFin         = ocupacion.getFechaFin();

        // via proyecto
        Proyecto proyecto = ocupacion.getProyecto();
        if (proyecto != null) {
            this.tituloProyecto = proyecto.getTitulo();
            this.razonSocialEmpresa = proyecto.getEmpresa().getRazonSocial();
            this.cuitEmpresa       = proyecto.getEmpresa().getCuit();

            // via proyecto -> usuario (representante)
            if (proyecto.getRepresentanteEmpresa() != null) {
                this.nombreRepresentante   = proyecto.getRepresentanteEmpresa().getUsuario().getNombre();
                this.apellidoRepresentante = proyecto.getRepresentanteEmpresa().getUsuario().getApellido();
                this.cuitRepresentante     = proyecto.getRepresentanteEmpresa().getUsuario().getCuit();
            }
        }

        this.activa = ocupacion.estaActiva();
    }
}
