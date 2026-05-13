package sgpiv.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.NombreRol;

import java.time.LocalDate;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private NombreRol nombre;


    public Rol (NombreRol rol){
        this.nombre = rol;
    }

}
