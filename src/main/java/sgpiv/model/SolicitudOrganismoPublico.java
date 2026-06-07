package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import sgpiv.enums.EstadoSolicitudOrganismo;

import java.time.LocalDate;

@Entity
@Table(name = "solicitudes_organismo_publico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudOrganismoPublico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del organismo
    @NotBlank
    private String nombreOrganismo;

    @NotBlank
    private String tipoOrganismo; // Municipal, Provincial, Nacional

    @NotBlank
    private String cargoSolicitante;

    @NotBlank
    private String motivoAcceso;

    // Informe adjunto (guardado como bytes en BD o como path en disco)
    private String nombreArchivo;
    private String tipoArchivo;

    @Lob
    private byte[] archivo;

    // Relacion con usuario
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Estado
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudOrganismo estado = EstadoSolicitudOrganismo.PENDIENTE;

    private LocalDate fechaEnvio;
    private String motivoRechazo;
}
