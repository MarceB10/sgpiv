package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores_empresa")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProveedorEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "EL nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El rubro no puede estar vacio")
    private String rubro;

    private String contacto;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

}
