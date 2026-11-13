
package domain.state;

import domain.Mision;

/**
 * EstadoCompletada
 *
 * Representa el estado de una mision que ha sido completada.
 * La mision deja de ser ejecutable y las operaciones de completar
 * o fracasar no producen cambios en su estado.
 */
public class EstadoCompletada implements EstadoMision {
    private static final String nombre_ = "COMPLETADA";

    /**
     * Crea una instancia de estado completada.
     */
    public EstadoCompletada() {}

    /**
     * Comprueba si una mision completada puede ejecutarse.
     *
     * @return false, ya que la mision esta completada.
     */
    public boolean esEjecutable() {
        return false;
    }

    /**
     * Gestiona un intento de completar una mision ya completada.
     * No realiza ninguna modificacion.
     *
     * @param mision Mision sobre la que se solicita la operacion.
     */
    public void alCompletar(Mision mision) {
        return;
    }

    /**
     * Gestiona un intento de registrar un fracaso en una mision
     * completada. No realiza ninguna modificacion.
     *
     * @param mision Mision sobre la que se solicita la operacion.
     */
    public void alFracasar(Mision mision) {
        return;
    }

    /**
     * Obtiene el nombre del estado completada.
     *
     * @return Nombre del estado, "COMPLETADA".
     */
    public String getNombreEstado() {
        return nombre_;
    }
}
