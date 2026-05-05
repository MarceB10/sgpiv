package sgpiv.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Data
public class Gerente extends Usuario {

    @NotBlank(message = "El CUIT no puede estar vacio")
    private String cuit;

    private Verificator verificator = new Verificador();


    public Gerente(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit) {
        super(nombre, apellido, email, telefono, contrasenia);

        this.verificator.verificarTexto(cuit);

        this.cuit = cuit;
    }

}
