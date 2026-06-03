package sgpiv.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorTest {

    /***
     *
     *  IMPORTANTE: Para testear hay que poner en la consola el comando: mvn test
     *
     *  hay que hacerlo asi porque en spring 4 esta JUnit 6 perointelliJ todavia usa JUnit 5
     *  Asi que es mas rapido probar asi que intentar meter la dependencia de JUnit5
     */



    //Creo un validator para testear las notaciones de validcion @NotBlank, @NotEmpty, @Email, etc.
    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNombre(){
        var usuario = new Usuario("", "perez", "jp@gmail.com", 123456L, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        //aca el validator comprueba y atrapa todos los errores que hayan en donde pusimos notaciones
        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        //tomo el primer error de la lista
        ConstraintViolation<Proveedor> error = errores.iterator().next();

        //si hay un error entonces la lista no esta vacia, por lo que el test falla
        assertFalse(errores.isEmpty());
        //compruebo que error es y si es el error que estoy buscando que tire ese mensaje
        assertEquals("El nombre no puede estar vacio", error.getMessage());

    }

    @Test
    public void testApellido(){
        var usuario = new Usuario("juan", "", "jp@gmail.com", 123456L, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        ConstraintViolation<Proveedor> error = errores.iterator().next();

        assertFalse(errores.isEmpty());
        assertEquals("El apellido no puede estar vacio", error.getMessage());
    }

    @Test
    public void testEmail01(){
        var usuario = new Usuario("juan", "perez", "", 123456L, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        ConstraintViolation<Proveedor> error = errores.iterator().next();

        assertFalse(errores.isEmpty());
        assertEquals("El email no puede estar vacio", error.getMessage());
    }

    @Test
    public void testEmail02(){
        var usuario = new Usuario("juan", "perez", "jp.com", 123456L, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        ConstraintViolation<Proveedor> error = errores.iterator().next();

        assertFalse(errores.isEmpty());
        assertEquals("El email debe respetar el formato 'texto@dominio.com' ", error.getMessage());
    }

    @Test
    public void testTelefono01(){
        var usuario = new Usuario("juan", "perez", "jp@gmail.com", null, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        ConstraintViolation<Proveedor> error = errores.iterator().next();

        assertFalse(errores.isEmpty());
        assertEquals("El telefono no puede estar vacio", error.getMessage());
    }

    @Test
    public void testTelefono02(){
        var usuario = new Usuario("juan", "perez", "jp@gmail.com", 1L, "1234", "24-23556-3");
        Proveedor p = new Proveedor(usuario, "Gas", "nose");

        Set<ConstraintViolation<Proveedor>> errores = validator.validate(p);

        ConstraintViolation<Proveedor> error = errores.iterator().next();

        assertFalse(errores.isEmpty());
        assertEquals("El telefono debe tener al menos 5 digitos", error.getMessage());
    }



}