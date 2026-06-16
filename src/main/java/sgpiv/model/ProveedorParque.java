package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores_parque")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProveedorParque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proveedor no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El tipo de servicio no uede estar vacio")
    private String tipoServicio; //Agua, luz, gas

    private String contacto;

    private boolean compartido; // true = recurso compartido entre empresas

}




