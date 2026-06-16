package sgpiv.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.dtos.RolDTO;
import sgpiv.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String cuit;
    private String email;
    private Long telefono;
    private boolean activo;
    private List<RolDTO> roles;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.cuit = usuario.getCuit();
        this.email = usuario.getEmail();
        this.telefono = usuario.getTelefono();
        this.activo = usuario.isActivo();
        this.roles = usuario.getRoles().stream()
                .map(RolDTO::new)
                .collect(Collectors.toList());
    }

}
