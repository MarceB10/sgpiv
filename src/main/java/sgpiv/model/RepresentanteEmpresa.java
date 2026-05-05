package sgpiv.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepresentanteEmpresa {

    @NotBlank(message = "El CUIT no puede estar vacio")
    private String cuit;

    @NotNull(message = "El representante debe estar asociado a una empresa")
    private Empresa empresa;




}
