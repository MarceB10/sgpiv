package sgpiv.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmpresaRequestDTO {


    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    private String cuit;

    public Long Telefono;

    private String ingresoBrutos;

    public String descripcionBienServicio;

    @NotBlank(message = "El rubro es obligatorio")
    private String rubro;

    private String tipoIndustria;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    private String direccion;

}
