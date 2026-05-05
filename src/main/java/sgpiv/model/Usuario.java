package sgpiv.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgpiv.model.verificator.Verificador;
import sgpiv.model.verificator.Verificator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario {


    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacio")
    private String apellido;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe respetar el formato 'texto@dominio.com' ")
    private String email;


    @NotNull(message = "El telefono no puede estar vacio")
    @Min(value = 10000, message = "El telefono debe tener al menos 5 digitos")
    private Long telefono;

    @NotBlank(message = "La contraseña no puede estar vacia")
    private String contrasenia;

    private Verificator verificator = new Verificador();

    //esto se usaria para hacer un borrado logico de ser necesario
    private boolean activo = true;

    public Usuario(String nombre, String apellido, String email, Long telefono, String contrasenia) {

        this.verificator.verificarTexto(nombre);
        this.verificator.verificarTexto(apellido);
        this.verificator.verificarTexto(email);
        this.verificator.verificarNumeroTelefono(telefono);
        this.verificator.verificarTexto(contrasenia);


        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
    }


    private boolean contraseniaCorrecta(String contrasenia){
        return this.contrasenia.equals(contrasenia);
    }

    public void modificarContrasenia(String contraseniaActual, String nuevaContrasenia){
        verificarContrasenia(contraseniaActual, nuevaContrasenia);
        this.contrasenia = nuevaContrasenia;
    }


   public void modificarNombre(String nuevoNombre){
        this.nombre = nuevoNombre;
   }

   public void modificarApellido(String nuevoApellido){

        this.apellido = nuevoApellido;
   }

   public void modificarTelefono(Long nuevoTelefono){
        this.verificator.verificarNumeroTelefono(nuevoTelefono);
        this.telefono = nuevoTelefono;
   }

   public void modificarEmail(String nuevoEmail){
        this.verificator.verificarTexto(nuevoEmail);
        this.email = nuevoEmail;
   }

   private void verificarContrasenia(String contraseniaActual, String nuevaContrasenia){
       this.verificator.verificarTexto(contrasenia);

       if (!contraseniaCorrecta(contraseniaActual)){
           throw new RuntimeException("La contrasenia actual es incorrecta");
       }
   }


}
