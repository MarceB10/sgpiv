package sgpiv.dtos.response;

import lombok.Data;
import sgpiv.model.Proyecto;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProyectoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String estadoProyecto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long personalAOcupar;
    private Double progreso;

    private List<TareaResponseDTO> tareas;

    public ProyectoResponseDTO(Proyecto proyecto){
        this.id = proyecto.getId();
        this.titulo = proyecto.getTitulo();
        this.descripcion = proyecto.getDescripcion();
        this.estadoProyecto = proyecto.getEstadoProyecto().name();
        this.fechaInicio = proyecto.getFechaInicio();
        this.fechaFin = proyecto.getFechaFin();
        this.personalAOcupar = proyecto.getPersonalAOcupar();
        this.progreso = proyecto.obtenerProgreso() * 100;
        this.tareas = proyecto.getTareas()
                        .stream()
                        .map(TareaResponseDTO::new)
                        .toList();
    }
}