package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "organismos_publicos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrganismoPublico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreOrganismo;
    private String tipoOrganismo;
    private String cargoSolicitante;
    private boolean activo = true;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;


    public OrganismoPublico(String nombre, String apellido, String email, Long telefono, String contrasenia, String cuit){

    }

    public void consultarDatosDelParque(){

    }

}
