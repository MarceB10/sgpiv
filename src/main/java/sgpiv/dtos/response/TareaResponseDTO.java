package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.Tarea;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private boolean completada;
    private Long proyectoId;

    public TareaResponseDTO(Tarea tarea) {
        this.id          = tarea.getId();
        this.titulo      = tarea.getTitulo();
        this.descripcion = tarea.getDescripcion();
        this.completada  = tarea.isCompleta();
        this.proyectoId  = tarea.getProyecto() != null ? tarea.getProyecto().getId() : null;
    }
}
