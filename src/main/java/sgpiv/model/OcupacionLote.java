package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OcupacionLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ingrese la fecha de inicio")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String motivoFinalizacion;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    public OcupacionLote(Empresa empresa, Lote lote, LocalDate fechaInicio){
        this.empresa = empresa;
        this.lote = lote;
        this.fechaInicio = fechaInicio;
    }

    public boolean estaActiva() {
        return this.fechaFin == null;
    }


}
