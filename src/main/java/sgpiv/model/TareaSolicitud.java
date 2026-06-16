package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tareaSolicitud")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TareaSolicitud {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "solicitud_proyecto_id")
    private SolicitudProyecto solicitudProyecto;


    public TareaSolicitud(String titulo, String descripcion, SolicitudProyecto solicitud){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitudProyecto = solicitud;
    }

}
