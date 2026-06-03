package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    @Valid
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;


    public Gerente(Usuario usuario) {
        this.usuario = usuario;
    }

}
