package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Entity
@Table(name = "proveedores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "El CUIT no puede estar vacio")
//    private String cuit;

    @NotBlank(message = "Debe especificar el rubro")
    private String rubro;

    @NotBlank(message = "Debe completar la Categoria de provision")
    private String categoriaProvision;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

}
