package sgpiv.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import sgpiv.enums.EstadoLote;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoteTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    //tests logica de negocio

    @Test
    public void testEstaDisponibleCuandoEstadoEsDisponible(){
        Lote lote = new Lote();
        lote.setEstadoLote(EstadoLote.DISPONIBLE);
        assertTrue(lote.estaDisponible());
    }

    @Test
    public void testEstaDisponibleCuandoEstadoEsAsignado(){
        Lote lote = new Lote();
        lote.setEstadoLote(EstadoLote.ASIGNADO);
        assertFalse(lote.estaDisponible());
    }

    @Test
    public void testEstaDisponibleCuandoEstadoEsEnUso(){
        Lote lote = new Lote();
        lote.setEstadoLote(EstadoLote.EN_USO);
        assertFalse(lote.estaDisponible());
    }

    @Test
    public void testEstadoPorDefectoEsDisponible(){
        Lote lote = new Lote();
        assertEquals(EstadoLote.DISPONIBLE, lote.getEstadoLote());
    }

    // tests validacion

    @Test
    public void testSuperficieNula() {
        Lote lote = new Lote();
        lote.setUbicacion("Zona A");
        lote.setPrecio(1500f);
        Set<ConstraintViolation<Lote>> errores = validator.validate(lote);
        assertTrue(errores.stream().anyMatch(e -> e.getMessage().equals("Ingrese la superficie")));
    }

    @Test
    public void testUbicacionVacia() {
        Lote lote = new Lote();
        lote.setSuperficie(200f);
        lote.setPrecio(1500f);
        lote.setUbicacion("");
        Set<ConstraintViolation<Lote>> errores = validator.validate(lote);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("Ingrese la ubicacion")));
    }

    @Test
    public void testPrecioNulo() {
        Lote lote = new Lote();
        lote.setSuperficie(200f);
        lote.setUbicacion("Zona A");
        Set<ConstraintViolation<Lote>> errores = validator.validate(lote);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("El precio no puede estar vacio")));
    }

}