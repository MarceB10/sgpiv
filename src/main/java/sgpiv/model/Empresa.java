package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoEmpresa;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
@Data // lombook, se encarga de todos los getters y setters, toString, equals y hashcode
@NoArgsConstructor // constructor vacio para JPA
@AllArgsConstructor // constructor con todos los atributos como parametros

public class Empresa {

    @Id // para la bd, la genera automaticamente autoincremental como primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La razon social no puede estar vacia") // veriefica que no sea null
    @Column(nullable = false)
    private String razonSocial;

    @NotBlank(message = "El CUIT no puede estar vacio")
    @Column(unique = true, nullable = false, length = 11)
    private String cuit;

    private String ingresoBrutos;

    @NotBlank(message = "El rubro no puede estar vacio")
    private String rubro;

    private String tipoIndustria;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "Ingrese correctamente el email") // verifica el email valido
    @Column(unique = true)
    private String email;

    private String direccion;

    @Enumerated(EnumType.STRING) // guarda el enum como string en la bd
    private EstadoEmpresa estadoEmpresa = (EstadoEmpresa.INTERESADA);

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL) // define la relacion con proyecto de 1 a mucho y lo
    private List<Proyecto> proyectos = new ArrayList<>();                           //mapea, seria que la relacion esta en la tabla proyecto
                                                                //y no en empresa, se borraria en cascada asi que hay que ver esto si esta bien o sacarle esta conf
}
