package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoSolicitud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SolicitudRadicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // -------------------
    //DATOS EMPRESA
    // -------------------
    private String razonSocial;
    private String cuitEmpresa;
    private String rubro;
    private String emailEmpresa;
    private String telefonoEmpresa;
    private String direccion;
    private String ingresoBrutos;
    private String descripcionBienServicio;
    private String tipoIndustria;
    //---------------
    //DATOS PROYECTO
    //-------------
    @NotBlank(message = "El tipo de empresa no puede estar vacio")
    private String tipoEmpresa;// Nueva o existente, hay que ver si es necesario un enum
    @NotBlank(message = "El objetivo del proyecto no puede estar vacio")
    private String objetivoProyecto;
    @NotBlank(message = "La actividad principal no puede estar vacia")
    private String actividadPrincipal;
    @NotNull(message = "Indicar la superficie necesaria en m2")
    private Double necesidadM2; // 1200, 1800, 2500, 3000, 5000, 6000
    @NotNull(message = "Indique si tiene planos")
    private Boolean tienePlanos;//ver el manejo de planos porque el capaz elos puede cargar, por ahora solo boolean
    //---------
    //eSTADO
    //-----------
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private LocalDate fechaEnvio;
    private String motivoRechazo; // razon por la que rechaza el gerente
    //---------
    // USUARIO ya no va representante por ahora xd
    //---------
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}