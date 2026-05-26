package sgpiv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import sgpiv.enums.EstadoLote;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ingrese la superficie")
    private Float superficie;

    @NotBlank(message = "Ingrese la ubicacion")
    private String ubicacion;

    @NotNull(message = "El precio no puede estar vacio")
    private Float precio;

    private LocalDate fechaUso;

    private LocalDate fechaAdjudicacion;

    private String restricciones;
    

    @Enumerated(EnumType.STRING)
    private EstadoLote estadoLote= EstadoLote.DISPONIBLE;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
    private List<OcupacionLote> ocupaciones;

    public Lote(Float superficie,
                String ubicacion,
                Float precio,
                String restricciones
                ){
        this.superficie = superficie;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.restricciones = restricciones;
    }


    public boolean estaDisponible() {
        return this.estadoLote == EstadoLote.DISPONIBLE;
    }

    public void habilitarDisponibilidad(){
        this.estadoLote = EstadoLote.DISPONIBLE;
    }

    public void deshabilitarDisponibilidad(){
        this.estadoLote = EstadoLote.EN_USO;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public void setSuperficie(float superficie) {
        this.superficie = superficie;
    }
}
