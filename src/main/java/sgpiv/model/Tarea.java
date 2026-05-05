package sgpiv.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Data
public class Tarea {

    @NotBlank(message = "El titulo de la tarea no puede estar vacio")
    private String titulo;

    @NotBlank(message = "La descripcion de la tarea no puede estar vacia")
    private String descripcion;

    private boolean completa;
    private Verificator verificator = new Verificador();

    public Tarea(String titulo, String descripcion){
        this.verificator.verificarTexto(titulo);
        this.verificator.verificarTexto(descripcion);

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completa = false;
    }

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
