package sgpiv.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorEmpresaRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El rubro no puede estar vacio")
    private String rubro;

    private String contacto;
}