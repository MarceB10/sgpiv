package sgpiv.model.verificator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Verificador implements Verificator{

    private final Long TELEFONO_MINIMO_A_INGRESAR = 10000L;
    private final String MSG_TEXTO_EN_BLANCO = "El texto No puede estar en blanco";
    private final String MSG_TELEFONO_INCORRECTO = "El numero de telefono es incorrecto";


    @Override
    public void verificarTexto(String txt) {
        if (txt.isBlank() || txt.isEmpty()){
                throw new RuntimeException(MSG_TEXTO_EN_BLANCO);
        }
    }

    @Override
    public void verificarObjeto(Object obj) {
        if (obj == null){
            throw new RuntimeException("Este objeto: ["+ obj.toString() +"]  No puede estar vacio");
        }
    }

    @Override
    public void verificarNumeroInt(int numeroMinimo, int numeroAVerificar) {
        if (numeroAVerificar < numeroMinimo){
            throw new RuntimeException("El numero minimo a ingresar es [" + numeroMinimo + "]");
        }
    }

    @Override
    public void verificarNumeroTelefono(Long telefono) {
        if (telefono < TELEFONO_MINIMO_A_INGRESAR){
            throw new RuntimeException(MSG_TELEFONO_INCORRECTO);
        }
    }
}
