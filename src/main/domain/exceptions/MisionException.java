package domain.exceptions;

/**
 * MisionException
 *
 * Excepcion general para representar errores relacionados con
 * la gestion de misiones de la organizacion.
 *
 * Actua como clase base de las excepciones especificas de misiones.
 *
 * @author Arian Mohammadian
 */
public class MisionException extends Exception {

    /**
     * Crea una excepcion de mision sin mensaje descriptivo.
     */
    public MisionException() {
        super();
    }

    /**
     * Crea una excepcion de mision con un mensaje descriptivo.
     *
     * @param msg Mensaje que describe el error producido.
     */
    public MisionException(String msg) {
        super(msg);
    }
}
