package sgpiv.model.verificator;

public interface Verificator {

    void verificarTexto(String txt);

    void verificarObjeto(Object obj);

    void verificarNumeroInt(int numeroMinimo, int numeroAVerificar);

    void verificarNumeroTelefono(Long telefono);
}
