package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private SolicitudRadicacion solicitud;


    public TareaSolicitud(String titulo, String descripcion, SolicitudRadicacion solicitud){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitud = solicitud;
    }

}
