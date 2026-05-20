package sgpiv.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "representantes_empresa")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RepresentanteEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "El CUIT no puede estar vacio")
//    private String cuit;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    //@NotNull(message = "El representante debe estar asociado a una empresa")
    private Empresa empresa;

    public RepresentanteEmpresa(Usuario usuario){
        this.usuario = usuario;
    }

    public void asignarEmpresa(Empresa empresa){
        this.empresa = empresa;
    }

    public Empresa miEmpresaEs(){
        return this.empresa;
    }

}
