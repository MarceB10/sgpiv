package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Entity
@Table(name = "gerentes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "El CUIT no puede estar vacio")
//    private String cuit;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    private Verificator verificator = new Verificador();


    public Gerente(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit) {

        this.verificator.verificarTexto(cuit);
    }

}
