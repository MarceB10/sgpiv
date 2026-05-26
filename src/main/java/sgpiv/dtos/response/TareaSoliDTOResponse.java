package sgpiv.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.Tarea;
import sgpiv.model.TareaSolicitud;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TareaSoliDTOResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private Long idSolicitud;

    public TareaSoliDTOResponse(TareaSolicitud tarea){
        this.id = tarea.getId();
        this.titulo = tarea.getTitulo();
        this.descripcion = tarea.getDescripcion();
        this.idSolicitud = tarea.getSolicitud().getId();
    }

}
