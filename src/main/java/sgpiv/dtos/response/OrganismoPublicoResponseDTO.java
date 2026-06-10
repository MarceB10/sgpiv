package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.OrganismoPublico;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganismoPublicoResponseDTO {

    private Long id;
    private String nombreOrganismo;
    private String tipoOrganismo;
    private String cargoSolicitante;
    private boolean activo;

    // Datos del usuario
    private String nombreUsuario;
    private String apellidoUsuario;
    private String cuitUsuario;
    private String emailUsuario;
    private Long telefonoUsuario;

    public OrganismoPublicoResponseDTO(OrganismoPublico o) {
        this.id               = o.getId();
        this.nombreOrganismo  = o.getNombreOrganismo();
        this.tipoOrganismo    = o.getTipoOrganismo();
        this.cargoSolicitante = o.getCargoSolicitante();
        this.activo           = o.isActivo();

        if (o.getUsuario() != null) {
            this.nombreUsuario   = o.getUsuario().getNombre();
            this.apellidoUsuario = o.getUsuario().getApellido();
            this.cuitUsuario     = o.getUsuario().getCuit();
            this.emailUsuario    = o.getUsuario().getEmail();
            this.telefonoUsuario = o.getUsuario().getTelefono();
        }
    }
}
