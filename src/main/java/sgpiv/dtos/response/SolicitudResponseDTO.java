package sgpiv.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.EstadoSolicitud;
import sgpiv.model.SolicitudRadicacion;
import sgpiv.model.Tarea;
import sgpiv.model.TareaSolicitud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDTO {
    private Long id;

    // DATOS EMPRESA
    private String razonSocial;
    private String cuitEmpresa;
    private String rubro;
    private String tipoIndustria;
    private String emailEmpresa;
    private String telefonoEmpresa;
    private String direccion;
    private String ingresoBrutos;
    private String descripcionBienServicio;

    // DATOS PROYECTO
    private String tipoEmpresa;
    private String objetivoProyecto;
    private String actividadPrincipal;
    private Double necesidadM2;
    private Boolean tienePlanos;

    // ESTADO
    private EstadoSolicitud estado;
    private LocalDate fechaEnvio;
    private String motivoRechazo;

    // USUARIO
    private String nombreUsuario;
    private String apellidoUsuario;
    private String cuitUsuario;

    public SolicitudResponseDTO(SolicitudRadicacion solicitud) {
        this.id                      = solicitud.getId();
        this.razonSocial             = solicitud.getRazonSocial();
        this.cuitEmpresa             = solicitud.getCuitEmpresa();
        this.rubro                   = solicitud.getRubro();
        this.tipoIndustria           = solicitud.getTipoIndustria();
        this.emailEmpresa            = solicitud.getEmailEmpresa();
        this.telefonoEmpresa         = solicitud.getTelefonoEmpresa();
        this.direccion               = solicitud.getDireccion();
        this.ingresoBrutos           = solicitud.getIngresoBrutos();
        this.descripcionBienServicio = solicitud.getDescripcionBienServicio();
        this.tipoEmpresa             = solicitud.getTipoEmpresa();
        this.objetivoProyecto        = solicitud.getObjetivoProyecto();
        this.actividadPrincipal      = solicitud.getActividadPrincipal();
        this.necesidadM2             = solicitud.getNecesidadM2();
        this.tienePlanos             = solicitud.getTienePlanos();
        this.estado                  = solicitud.getEstado();
        this.fechaEnvio              = solicitud.getFechaEnvio();
        this.motivoRechazo           = solicitud.getMotivoRechazo();

        if (solicitud.getUsuario() != null) {
            this.nombreUsuario   = solicitud.getUsuario().getNombre();
            this.apellidoUsuario = solicitud.getUsuario().getApellido();
            this.cuitUsuario     = solicitud.getUsuario().getCuit();
        }

    }

}
