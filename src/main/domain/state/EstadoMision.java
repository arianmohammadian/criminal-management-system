
package domain.state;

import domain.Mision;

/**
 * EstadoMision
 *
 * Define las operaciones comunes de los estados de una mision.
 * Permite consultar su disponibilidad y gestionar las transiciones
 * producidas al completarse o fracasar.
 */
public interface EstadoMision {

    /**
     * Comprueba si una mision puede ejecutarse en este estado.
     *
     * @return true si la mision es ejecutable; false en caso contrario.
     */
    public boolean esEjecutable();

    /**
     * Gestiona el resultado de completar una mision en este estado.
     *
     * @param mision Mision sobre la que se aplica la operacion.
     */
    public void alCompletar(Mision mision);

    /**
     * Gestiona el resultado de fracasar una mision en este estado.
     *
     * @param mision Mision sobre la que se aplica la operacion.
     */
    public void alFracasar(Mision mision);

    /**
     * Obtiene el nombre del estado.
     *
     * @return Nombre del estado de la mision.
     */
    public String getNombreEstado();
}
