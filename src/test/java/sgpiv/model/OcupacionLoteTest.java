package sgpiv.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OcupacionLoteTest {

    @Test
    public void testEstaActivaCuandoFechaFinEsNula(){
        OcupacionLote ocupacion = new OcupacionLote();
        ocupacion.setFechaInicio(LocalDate.now());
        assertTrue(ocupacion.estaActiva());
    }

    @Test
    public void testEstaActivaCuandoFechaFinEstaDefinida(){
        OcupacionLote ocupacion = new OcupacionLote();
        ocupacion.setFechaInicio(LocalDate.now().minusMonths(6));
        ocupacion.setFechaFin(LocalDate.now());
        assertFalse(ocupacion.estaActiva());
    }

    @Test
    public void testFechaFinEsNulaPorDefecto() {
        OcupacionLote ocupacion = new OcupacionLote();
        assertNull(ocupacion.getFechaFin());
    }

    @Test
    public void testMotivoFinalizacionEsNuloPorDefecto() {
        OcupacionLote ocupacion = new OcupacionLote();
        assertNull(ocupacion.getMotivoFinalizacion());
    }

}