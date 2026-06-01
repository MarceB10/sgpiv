package sgpiv.dtos.response;

import lombok.Data;
import sgpiv.model.Tarea;

@Data
public class TareaResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private boolean completa;

    public TareaResponseDTO(Tarea tarea){
        this.id = tarea.getId();
        this.titulo = tarea.getTitulo();
        this.descripcion = tarea.getDescripcion();
        this.completa = tarea.isCompleta();
    }
}