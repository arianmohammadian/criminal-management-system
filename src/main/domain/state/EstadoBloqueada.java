
package domain.state;

import domain.Mision;

/**
 * EstadoBloqueada
 *
 * Representa el estado de una mision que no puede ejecutarse
 * mientras permanezca bloqueada.
 *
 * Las operaciones de completar y fracasar no modifican su estado.
 */
public class EstadoBloqueada implements EstadoMision {
    private static final String nombre_ = "BLOQUEADA";

    /**
     * Crea una instancia de estado bloqueada.
     */
    public EstadoBloqueada() {}

    /**
     * Comprueba si una mision bloqueada puede ejecutarse.
     *
     * @return false, ya que la mision esta bloqueada.
     */
    public boolean esEjecutable() {
        return false;
    }

    /**
     * Gestiona un intento de completar una mision bloqueada.
     * No realiza ninguna modificacion.
     *
     * @param mision Mision sobre la que se solicita la operacion.
     */
    public void alCompletar(Mision mision) {
        return;
    }

    /**
     * Gestiona un intento de registrar un fracaso en una mision
     * bloqueada. No realiza ninguna modificacion.
     *
     * @param mision Mision sobre la que se solicita la operacion.
     */
    public void alFracasar(Mision mision) {
        return;
    }

    /**
     * Obtiene el nombre del estado bloqueada.
     *
     * @return Nombre del estado, "BLOQUEADA".
     */
    public String getNombreEstado() {
        return nombre_;
    }
}
