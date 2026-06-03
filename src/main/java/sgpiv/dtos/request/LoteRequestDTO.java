package sgpiv.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import sgpiv.enums.ServicioLote;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteRequestDTO {

    @NotNull(message = "Ingrese la superficie")
    @Positive(message = "La superficie debe ser mayor a 0")
    private Double superficie;

    @NotBlank(message = "Ingrese la ubicacion")
    private String ubicacion;

    @NotNull(message = "El precio no puede estar vacio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Float precio;

    private LocalDate fechaUso;

    private LocalDate fechaAdjudicacion;

    private String restricciones;

    private Set<ServicioLote> servicios =
            new HashSet<>();


}
