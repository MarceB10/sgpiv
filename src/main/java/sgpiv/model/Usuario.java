package sgpiv.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    //esto se usaria para hacer un borrado logico de ser necesario
    private boolean activo = true;

    public Usuario(String nombre, String apellido, String email, Long telefono, String contrasenia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
    }


    public void modificarContrasenia(String contraseniaActual, String nuevaContrasenia){
        if (!this.contrasenia.equals(contraseniaActual)){
            throw new RuntimeException("La contrasenia actual es incorrecta");
        }
        this.contrasenia = nuevaContrasenia;
    }


   public void modificarNombre(String nuevoNombre){
        this.nombre = nuevoNombre;
   }

   public void modificarApellido(String nuevoApellido){
        this.apellido = nuevoApellido;
   }

   public void modificarTelefono(Long nuevoTelefono){
        this.telefono = nuevoTelefono;
   }

   public void modificarEmail(String nuevoEmail){
        this.email = nuevoEmail;
   }



}
