package domain.exceptions;

/**
 * MisionYaExistenteException
 *
 * Excepcion que se produce al intentar registrar una mision
 * cuyo identificador ya existe en el sistema.
 *
 * @author Arian Mohammadian
 */
public class MisionYaExistenteException extends MisionException {

    /**
     * Crea una excepcion indicando que ya existe una mision
     * con el identificador especificado.
     *
     * @param id Identificador de la mision duplicada.
     */
    public MisionYaExistenteException(String id) {
        super("La mision identificada con " + id + " ya existe");
    }
}
