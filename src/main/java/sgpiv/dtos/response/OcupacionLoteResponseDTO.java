package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.OcupacionLote;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionLoteResponseDTO {

    private Long id;
    private Long idLote;
    private String ubicacionLote;
    private Float superficieLote;
    private Float precioLote;
    private String razonSocialEmpresa;
    private String cuitEmpresa;
    private String nombreRepresentante;
    private String apellidoRepresentante;
    private String cuitRepresentante;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;

    public OcupacionLoteResponseDTO(OcupacionLote ocupacion, String nombreRep,
                                    String apellidoRep, String cuitRep) {
        this.id                   = ocupacion.getId();
        this.idLote               = ocupacion.getLote().getId();
        this.ubicacionLote        = ocupacion.getLote().getUbicacion();
        this.superficieLote       = ocupacion.getLote().getSuperficie();
        this.precioLote           = ocupacion.getLote().getPrecio();
        this.razonSocialEmpresa   = ocupacion.getEmpresa().getRazonSocial();
        this.cuitEmpresa          = ocupacion.getEmpresa().getCuit();
        this.nombreRepresentante  = nombreRep;
        this.apellidoRepresentante = apellidoRep;
        this.cuitRepresentante    = cuitRep;
        this.fechaInicio          = ocupacion.getFechaInicio();
        this.fechaFin             = ocupacion.getFechaFin();
        this.activa               = ocupacion.estaActiva();
    }
}
