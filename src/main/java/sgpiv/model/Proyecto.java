package sgpiv.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sgpiv.enums.EstadoProyecto;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Proyecto {

    @NotBlank(message = "El titulo del proyecto no puede estar vacio")
    private String titulo;

    @NotBlank(message = "La descripcion del proyecto no puede estar vacia")
    private String descripcion;

    private EstadoProyecto estadoProyecto;

    @NotNull(message = "La fecha de inicio del proyecto no puede estar vacia")
    private LocalDate fechaInicio;

    private LocalDate fechaFin; // sino no hay fecha fin entonces no termino

    @Min(value = 1, message = "minimo 1 persona debe trabajar en el proyecto")
    private int personalAOcupar;

    private List<Tarea> tareas;

    private Verificator verificator = new Verificador();

    public Proyecto(String titulo, String descripcion, LocalDate fechaInicio, int personalAOcupar){

        this.verificator.verificarTexto(titulo);
        this.verificator.verificarTexto(descripcion);
        this.verificator.verificarObjeto(fechaInicio);
        this.verificator.verificarNumeroInt(1, personalAOcupar);


        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.personalAOcupar = personalAOcupar;

        this.estadoProyecto = EstadoProyecto.INACTIVO;
        this.tareas = new ArrayList<>();
    }

    public double obtenerProgreso(){
        double progreso = 0;
        for(Tarea tarea: tareas){
           if(tarea.isCompleta()){
               progreso++;
           }
        }

        return progreso / this.tareas.size();
    }

    public void borrarTarea(Tarea tarea){
        this.tareas.remove(tarea);
    }

    public void agregarTarea(Tarea tarea){
        if (tarea == null){
            throw new RuntimeException("No se pudo agregar la Tarea");
        }
        this.tareas.add(tarea);
    }

    public List<Tarea> obtenerTareas(){
        return this.tareas;
    }

    public void iniciarProyecto(){
        this.estadoProyecto = EstadoProyecto.ACTIVO;
    }

    public void completar(LocalDate fechaFin){
        this.verificator.verificarObjeto(fechaFin);

        this.fechaFin = fechaFin;
        this.estadoProyecto = EstadoProyecto.COMPLETADO;
    }

    public void modificarTiulo(String titulo){
        this.verificator.verificarTexto(titulo);
        this.titulo = titulo;
    }

    public void modificarDescripcion(String descripcion){
        this.verificator.verificarTexto(descripcion);
        this.descripcion = descripcion;
    }

    public void modificarFechaInicio(LocalDate fechaInicio){
        this.verificator.verificarObjeto(fechaInicio);
        this.fechaInicio = fechaInicio;
    }



    public void modificarCantPersonal(int personalAOcupar){
        this.verificator.verificarObjeto(personalAOcupar);
        this.personalAOcupar = personalAOcupar;
    }


}
