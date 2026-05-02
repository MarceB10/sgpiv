package sgpiv.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Gerente extends Usuario {

    @NotBlank(message = "El CUIT no puede estar vacio")
    private String cuit;


    public Gerente(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit) {
        super(nombre, apellido, email, telefono, contrasenia);
        this.cuit = cuit;
    }

}
