package sgpiv.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudOrganismoRequestDTO {

    @NotBlank(message = "El nombre del organismo es obligatorio")
    private String nombreOrganismo;

    @NotBlank(message = "El tipo de organismo es obligatorio")
    private String tipoOrganismo;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargoSolicitante;

    @NotBlank(message = "El motivo de acceso es obligatorio")
    private String motivoAcceso;

    // El archivo se maneja aparte con MultipartFile
}
