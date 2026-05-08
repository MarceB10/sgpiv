package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tareas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo de la tarea no puede estar vacio")
    private String titulo;

    @NotBlank(message = "La descripcion de la tarea no puede estar vacia")
    private String descripcion;

    private boolean completa;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    public void completarTarea(){
        this.completa = true;
    }

    public void reiniciarTarea(){
        this.completa = false;
    }

    public boolean isCompleta() {
        return completa;
    }




}
