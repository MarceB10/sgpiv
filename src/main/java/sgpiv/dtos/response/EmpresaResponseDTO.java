package sgpiv.dtos.response;

import lombok.Data;
import sgpiv.model.Empresa;

@Data
public class EmpresaResponseDTO {
    private Long id;
    private String razonSocial;
    private String cuit;
    private String rubro;
    private String email;
    private String direccion;
    private String estadoEmpresa;

    public EmpresaResponseDTO(Empresa empresa) {
        this.id = empresa.getId();
        this.razonSocial = empresa.getRazonSocial();
        this.cuit = empresa.getCuit();
        this.rubro = empresa.getRubro();
        this.email = empresa.getEmail();
        this.direccion = empresa.getDireccion();
        this.estadoEmpresa = empresa.getEstadoEmpresa().name();
    }
}
