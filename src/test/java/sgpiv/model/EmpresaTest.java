package sgpiv.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import sgpiv.enums.EstadoEmpresa;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    // tests de lógica de negocio

    @Test
    public void testEstadoPorDefectoEsInteresada() {
        Empresa empresa = new Empresa();
        assertEquals(EstadoEmpresa.INTERESADA, empresa.getEstadoEmpresa());
    }

    //faltan hacer mas cuando este todo

    // test validacion

    @Test
    public void testRazonSocialVacia() {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("");
        empresa.setCuit("20-12345678-9");
        empresa.setRubro("Tecnologia");
        empresa.setEmail("empresa@gmail.com");
        Set<ConstraintViolation<Empresa>> errores = validator.validate(empresa);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("La razon social no puede estar vacia")));
    }

    @Test
    public void testCuitVacio() {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("Empresa SA");
        empresa.setCuit("");
        empresa.setRubro("Tecnologia");
        empresa.setEmail("empresa@gmail.com");
        Set<ConstraintViolation<Empresa>> errores = validator.validate(empresa);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("El CUIT no puede estar vacio")));
    }

    @Test
    public void testRubroVacio() {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("Empresa SA");
        empresa.setCuit("20-12345678-9");
        empresa.setRubro("");
        empresa.setEmail("empresa@gmail.com");
        Set<ConstraintViolation<Empresa>> errores = validator.validate(empresa);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("El rubro no puede estar vacio")));
    }

    @Test
    public void testEmailVacio() {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("Empresa SA");
        empresa.setCuit("20-12345678-9");
        empresa.setRubro("Tecnologia");
        empresa.setEmail("");
        Set<ConstraintViolation<Empresa>> errores = validator.validate(empresa);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("El email no puede estar vacio")));
    }

    @Test
    public void testEmailInvalido() {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("Empresa SA");
        empresa.setCuit("20-12345678-9");
        empresa.setRubro("Tecnologia");
        empresa.setEmail("emailinvalido");
        Set<ConstraintViolation<Empresa>> errores = validator.validate(empresa);
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage()
                        .equals("Ingrese correctamente el email")));
    }

}