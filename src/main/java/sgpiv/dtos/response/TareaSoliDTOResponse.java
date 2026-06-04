package sgpiv.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.TareaSolicitud;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TareaSoliDTOResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private Long idSolicitudProyecto;

    public TareaSoliDTOResponse(TareaSolicitud tarea){
        this.id = tarea.getId();
        this.titulo = tarea.getTitulo();
        this.descripcion = tarea.getDescripcion();
        this.idSolicitudProyecto = tarea.getSolicitudProyecto().getId();
    }

}
