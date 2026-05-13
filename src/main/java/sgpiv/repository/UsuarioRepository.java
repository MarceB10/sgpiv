package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.enums.NombreRol;
import sgpiv.model.Usuario;

import java.util.List;
import java.util.Optional;

/***
 * A TENER EN CUENTA:
 *
 * ---------- Sin @Query — el campo es "cuit" (español) y Spring lo encuentra bien ----
 * Optional<Usuario> findByCuit(String cuit);
 *
 * ----------Con @Query — el nombre completo puede ser en español----------------------
 *  @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
 *  List<Usuario> traerTodosConRoles(); //  perfectamente válido
 *
 *
 *  Por qué LEFT JOIN FETCH
 * Tu roles está definido como FetchType.EAGER,
 * así que JPA los carga siempre. Pero con EAGER en una lista de muchos usuarios,
 * JPA puede generar el problema N+1:
 * una query para traer los usuarios y luego una query por cada usuario para traer sus roles.
 *
 *
 */



public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCuit(String cuit);

    // Verificar si ya existe un email registrado
    boolean existsByEmail(String email);

    boolean existsByCuit(String cuit);

    // Traer todos los usuarios con sus roles cargados
    // Sin este @Query, al ser LAZY traería los roles en N consultas separadas
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
    List<Usuario> findAllConRoles();

    // Traer un usuario con sus roles por CUIT — evita el problema LAZY al buscar uno solo
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.cuit = :cuit")
    Optional<Usuario> findByCuitConRoles(@Param("cuit") String cuit);

    // Traer todos los usuarios que tengan un rol específico
    // Útil para que el Gerente filtre por ejemplo todos los ROL_NULO
    //El :nombreRol es un named parameter — un placeholder que se va a reemplazar con el valor real cuando se ejecute la query.
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :nombreRol")
    List<Usuario> findByRol(@Param("nombreRol") NombreRol nombreRol);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.email = :email ")
    Optional<Usuario> findByEmailConRoles(@Param("email") String email);

}
