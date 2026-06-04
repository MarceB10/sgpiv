package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoProyecto;
import sgpiv.enums.ServicioLote;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo del proyecto no puede estar vacio")
    private String titulo;

    @NotBlank(message = "La descripcion del proyecto no puede estar vacia")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoProyecto estadoProyecto;

    private String objetivo;
    private String rubro;
    private String actividadPrincipal;
    private String actividadSecundaria;
    private BigDecimal inversionEstimada;
    private String produccionEstimada;

    private Double supCubiertaTrabajoM2;
    private Double supCubiertaDepositoM2;
    private Double supExpansionM2;

    private Boolean tienePlanos;

    // Residuos
    private boolean generaResiduos;
    private String descripcionResiduos;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ServicioLote> serviciosRequeridos = new ArrayList<>();

    @NotNull(message = "La fecha de inicio del proyecto no puede estar vacia")
    private LocalDate fechaInicio;

    private Integer tiempoDeRadicacion;

    private LocalDate fechaFin; // sino no hay fecha fin entonces no termino

    @Min(value = 1, message = "minimo 1 persona debe trabajar en el proyecto")
    private Integer personalAOcupar;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tarea> tareas = new ArrayList<>();

    @NotNull
    private Double necesidadM2;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "representante_id")
    private RepresentanteEmpresa representanteEmpresa;

    public Proyecto(String titulo, String descripcion, LocalDate fechaInicio, Integer personalAOcupar){

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.personalAOcupar = personalAOcupar;

        this.estadoProyecto = EstadoProyecto.INACTIVO;
        this.tareas = new ArrayList<>();
    }

    public double obtenerProgreso(){
        if (this.tareas == null || this.tareas.isEmpty()) return 0; // ← primero
        double progreso = 0;
        for(Tarea tarea: tareas){
            if(tarea.isCompleta()){
                progreso++;
            }
        }
        return 100 * (progreso / this.tareas.size());
    }

    public long cantTareasCompletadas(){

        return this.tareas.stream().filter(Tarea ::isCompleta).count();
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


    public void agregarServiciosRequeridos(List<ServicioLote> serviciosRequeridos){
        this.serviciosRequeridos.addAll(serviciosRequeridos);
    }


    public void iniciarProyecto(){
        this.estadoProyecto = EstadoProyecto.ACTIVO;
    }

    public void completar(LocalDate fechaFin){
        this.fechaFin = fechaFin;
        this.estadoProyecto = EstadoProyecto.COMPLETADO;
    }


    public void modificarTiulo(String titulo){
        this.titulo = titulo;
    }

    public void modificarDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public void modificarFechaInicio(LocalDate fechaInicio){
        this.fechaInicio = fechaInicio;
    }



    public void modificarCantPersonal(Integer personalAOcupar){
        this.personalAOcupar = personalAOcupar;
    }


    public void agregarTareas(List<Tarea> tareasProyecto) {
        this.tareas.addAll(tareasProyecto);
    }


}
