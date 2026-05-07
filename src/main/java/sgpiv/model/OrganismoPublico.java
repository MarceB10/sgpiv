package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Entity
@Table(name = "organismos_publicos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrganismoPublico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "El CUIT no puede estar vacio")
//    private String cuit;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    private Verificator verificator = new Verificador();

    public OrganismoPublico(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit){
        this.verificator.verificarTexto(cuit);

    }

    public void consultarDatosDelParque(){

    }

}
