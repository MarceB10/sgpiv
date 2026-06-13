package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoSolicitudOrganismo;
import sgpiv.model.SolicitudOrganismoPublico;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudOrganismoResponseDTO {

    private Long id;
    private String nombreOrganismo;
    private String tipoOrganismo;
    private String cargoSolicitante;
    private String motivoAcceso;
    private EstadoSolicitudOrganismo estado;
    private LocalDate fechaEnvio;
    private String motivoRechazo;

    // Usuario
    private String nombreUsuario;
    private String apellidoUsuario;
    private String cuitUsuario;
    private String emailUsuario;

    //Archivo
    private String nombreArchivo;
    private String tipoArchivo;


    public SolicitudOrganismoResponseDTO(SolicitudOrganismoPublico s) {
        this.id               = s.getId();
        this.nombreOrganismo  = s.getNombreOrganismo();
        this.tipoOrganismo    = s.getTipoOrganismo();
        this.cargoSolicitante = s.getCargoSolicitante();
        this.motivoAcceso     = s.getMotivoAcceso();
        this.estado           = s.getEstado();
        this.fechaEnvio       = s.getFechaEnvio();
        this.motivoRechazo    = s.getMotivoRechazo();

        this.nombreArchivo    = s.getNombreArchivo();
        this.tipoArchivo      = s.getTipoArchivo();

        if (s.getUsuario() != null) {
            this.nombreUsuario   = s.getUsuario().getNombre();
            this.apellidoUsuario = s.getUsuario().getApellido();
            this.cuitUsuario     = s.getUsuario().getCuit();
            this.emailUsuario    = s.getUsuario().getEmail();
        }
    }
}
