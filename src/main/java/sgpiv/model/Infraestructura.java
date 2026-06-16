package sgpiv.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoInfraestructura;
import sgpiv.enums.TipoInfraestructura;

import java.time.LocalDate;

@Entity
@Table(name = "infraestructuras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Infraestructura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoInfraestructura tipo;

    @Enumerated(EnumType.STRING)
    private EstadoInfraestructura estado;

    private String sector;

    private String ubicacion;

    @Column(length = 1000)
    private String descripcion;

    private LocalDate ultimoMantenimiento;

    private LocalDate proximoMantenimiento;

    public Infraestructura(String nombre,
                           TipoInfraestructura tipo,
                           EstadoInfraestructura estado,
                           String sector,
                           String ubicacion,
                           String descripcion,
                           LocalDate ultimoMantenimiento,
                           LocalDate proximoMantenimiento) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.sector = sector;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.ultimoMantenimiento = ultimoMantenimiento;
        this.proximoMantenimiento = proximoMantenimiento;
    }
}