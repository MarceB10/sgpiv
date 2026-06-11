package sgpiv.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorParqueRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El tipo de servicio no puede estar vacio")
    private String tipoServicio;

    private String contacto;

    private boolean compartido;
}