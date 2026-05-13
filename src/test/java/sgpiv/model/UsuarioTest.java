package sgpiv.model;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    //probando @NotBlank y @Email
    private Validator validator;

    @BeforeEach
    void setUp(){
        //significa que antes de cada test se ejecute lo siguiente
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator(); //motor de validacion para revisar las anotaciones

    }

    @Test
    void usuarioValidoNoDebeTenerErrores(){
        Usuario usuario = new Usuario(
                "Juan",
                "Perez",
                "juan@mail.com",
                2000000002L,
                "1234",
                "23-456234-9"
        );

        Set<ConstraintViolation<Usuario>> errores = validator.validate(usuario);

        for (ConstraintViolation<Usuario> error : errores) {
            System.out.println(
                    "Campo: " + error.getPropertyPath()
                            + " | Mensaje: " + error.getMessage()
            );
        }

        assertTrue(errores.isEmpty());
    }

    @Test
    void usuarioCamposVacios(){
        Usuario usuario = new Usuario(
                "",
                "Perez",
                "2000000002",
                "juan@mail.com",
                202021L,
                "1234",
                "23-456234-9"
        );

        //Set<ConstraintViolation<Usuario>> errores = validator.validate(usuario);

        /*for (ConstraintViolation<Usuario> error : errores) {
            System.out.println(
                    "Campo: " + error.getPropertyPath()
                            + " | Mensaje: " + error.getMessage()
            );
        }*/

        System.out.println(usuario);
        assertEquals("", usuario.miNombre());
    }






}