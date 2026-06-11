package sgpiv.dtos.response;

import lombok.Data;
import sgpiv.model.Empresa;
import java.util.List;

@Data
public class EmpresaResponseDTO {
    private Long id;
    private String razonSocial;
    private String cuit;
    private String rubro;
    private String email;
    private String direccion;
    private String estadoEmpresa;
    private List<ProyectoResponseDTO> proyectos;

    //info del lote por posible baja con adjudicacion activa
    private boolean tieneLoteOcupado;
    private Long loteId;
    private String loteUbicacion;
    private Double loteSuperficie;

    public EmpresaResponseDTO(Empresa empresa) {
        this.id = empresa.getId();
        this.razonSocial = empresa.getRazonSocial();
        this.cuit = empresa.getCuit();
        this.rubro = empresa.getRubro();
        this.email = empresa.getEmail();
        this.direccion = empresa.getDireccion();
        this.estadoEmpresa = empresa.getEstadoEmpresa().name();
        this.proyectos = empresa.getProyectos()
                .stream()
                .map(ProyectoResponseDTO::new)
                .toList();
    }
}
