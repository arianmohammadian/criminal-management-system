package domain.exceptions;

/**
 * MisionNoEncontradaException
 *
 * Excepcion que se produce cuando se intenta consultar o realizar
 * una operacion sobre una mision que no existe en el sistema.
 *
 * @author Arian Mohammadian
 */
public class MisionNoEncontradaException extends MisionException {

    /**
     * Crea una excepcion indicando que la mision solicitada no existe.
     *
     * @param id Identificador de la mision que no se ha encontrado.
     */
    public MisionNoEncontradaException(String id) {
        super("La mision identificada con " + id + " no existe");
    }
}
