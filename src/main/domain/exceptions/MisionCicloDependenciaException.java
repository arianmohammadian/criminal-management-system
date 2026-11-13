package domain.exceptions;

/**
 * MisionCicloDependenciaException
 *
 * Excepcion que se produce al intentar añadir una dependencia
 * entre misiones que genera un ciclo en el grafo.
 *
 * @author Arian Mohammadian
 */
public class MisionCicloDependenciaException extends MisionException {

    /**
     * Crea una excepcion indicando que la dependencia genera un ciclo.
     *
     * @param id Identificador de la mision que genera el ciclo.
     */
    public MisionCicloDependenciaException(String id) {
        super("La mision con identificador " + id + " genera un ciclo, imposible añadir la dependencia");
    }
}
