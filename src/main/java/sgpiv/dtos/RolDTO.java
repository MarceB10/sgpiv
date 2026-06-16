package sgpiv.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.enums.NombreRol;
import sgpiv.model.Rol;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {

    @NotNull(message = "El rol es obligatorio")
    private NombreRol nombre;

    public RolDTO(Rol rol) {
        this.nombre = rol.getNombre();
    }
}

