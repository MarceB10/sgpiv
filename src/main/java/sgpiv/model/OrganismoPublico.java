package sgpiv.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Data
public class OrganismoPublico extends Usuario {

    @NotBlank(message = "El CUIT no puede estar vacio")
    private String cuit;

    private Verificator verificator = new Verificador();

    public OrganismoPublico(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit){
        super(nombre, apellido, email, telefono, contrasenia);

        this.verificator.verificarTexto(cuit);

        this.cuit = cuit;
    }

    public void consultarDatosDelParque(){

    }

}
