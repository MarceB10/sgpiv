package sgpiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sgpiv.dtos.request.LoginDTO;
import sgpiv.dtos.request.UsuarioRequestDTO;
import sgpiv.dtos.response.UsuarioResponseDTO;
import sgpiv.enums.NombreRol;
import sgpiv.model.Gerente;
import sgpiv.model.Rol;
import sgpiv.model.Usuario;
import sgpiv.repository.GerenteRepository;
import sgpiv.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String USUARIO_NO_ENCONTRADO = "El Usuario No se ha Encontrado";
    private static final String CONTRASENIA_INCORRECTA = "La contraseña No es correcta";
    private static final String USUARIO_INACTIVO = "El Usuario esta dado de baja";
    private static final String EMAIL_NO_EXISTE = "El email No Existe";


    private final UsuarioRepository usuarioRepository;
    private final RepresentanteService representanteService;
    private final GerenteRepository gerenteRepository;
    private final ProveedorService proveedorService;
    private final OrgPublicoService orgPublicoService;


    public List<UsuarioResponseDTO> usuariosSinRol(){
        List<Usuario> usuarios = usuarioRepository.findByRol(NombreRol.ROL_NULO);
        List<UsuarioResponseDTO> usuarioDTOS = new ArrayList<>();
        for (Usuario u: usuarios){
            usuarioDTOS.add( new UsuarioResponseDTO(u) );
        }

        return usuarioDTOS;
    }

    public UsuarioResponseDTO iniciarSesion(LoginDTO loginDTO){
        Usuario usuario = usuarioRepository.findByEmailConRoles(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException(EMAIL_NO_EXISTE));

        if (!usuario.contraseniaCorrecta(loginDTO.getContrasenia())){
            throw new RuntimeException(CONTRASENIA_INCORRECTA);
        }
        if(!usuario.isActivo()){
            throw new RuntimeException(USUARIO_INACTIVO);
        }

        return new UsuarioResponseDTO(usuario);

    }


    public void registrarse(UsuarioRequestDTO usuarioRequestDTO){

        Usuario nuevoUsuario = new Usuario(
                usuarioRequestDTO.getNombre(),
                usuarioRequestDTO.getApellido(),
                usuarioRequestDTO.getEmail(),
                usuarioRequestDTO.getTelefono(),
                usuarioRequestDTO.getContrasenia(),
                usuarioRequestDTO.getCuit() );
        Rol rolDeInicio = new Rol(NombreRol.ROL_NULO);
        nuevoUsuario.agregarRol(rolDeInicio);
        usuarioRepository.save(nuevoUsuario);
    }

    public UsuarioResponseDTO buscarPorCuit(String cuit){
        Usuario usuario = usuarioRepository.findByCuitConRoles(cuit)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));

        return new UsuarioResponseDTO(usuario);
    }



    public void AsignarRol(Usuario usuario, NombreRol rol){
        Rol nuevoRol = new Rol(rol);

        usuario.agregarRol(nuevoRol);

        if (rol == NombreRol.ROL_GERENTE){ //uso repository para evitar dependencias circulares
            Gerente gerente = new Gerente(usuario);
            gerenteRepository.save(gerente);
        }
        if (rol == NombreRol.ROL_REPRESENTANTE_EMPRESA){
            representanteService.crearPerfil(usuario);
        }
        if (rol == NombreRol.ROL_PROVEEDOR){
            proveedorService.crearPerfil();
        }
        if (rol == NombreRol.ROL_REPRESENTANTE_EMPRESA){
            orgPublicoService.crearPerfil();
        }

        usuarioRepository.save(usuario);
    }

    public void darDeBajaUsuario(String cuit){
        Usuario usuario = usuarioRepository.findByCuit(cuit)
                .orElseThrow(() -> new RuntimeException(USUARIO_NO_ENCONTRADO));
        usuario.desactivar();
        usuarioRepository.save(usuario);
    }


}
