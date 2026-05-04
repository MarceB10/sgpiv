package sgpiv.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Data
public class Proveedor extends Usuario{

    @NotBlank(message = "El CUIT no puede estar vacio")
    private String cuit;

    @NotBlank(message = "Debe especificar el rubro")
    private String rubro;

    @NotBlank(message = "Debe completar la Categoria de provision")
    private String catgProvision;

    private Verificator verificator = new Verificador();

    public Proveedor(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit, String rubro, String catgProvision) {
        super(nombre, apellido, email, telefono, contrasenia);


        this.verificator.verificarTexto(cuit);
        this.verificator.verificarTexto(rubro);
        this.verificator.verificarTexto(catgProvision);


        this.cuit = cuit;
        this.rubro = rubro;
        this.catgProvision = catgProvision;
    }
}
