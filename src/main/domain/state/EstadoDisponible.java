
package domain.state;

import domain.Mision;

/**
 * EstadoDisponible
 *
 * Representa el estado de una mision disponible para su ejecucion.
 * Permite completar la mision o registrar un fracaso, actualizando
 * su estado en consecuencia.
 */
public class EstadoDisponible implements EstadoMision {
    private static String nombre_ = "DISPONIBLE";

    /**
     * Crea una instancia de estado disponible.
     */
    public EstadoDisponible() {}

    /**
     * Comprueba si una mision en estado disponible puede ejecutarse.
     *
     * @return true, ya que la mision esta disponible.
     */
    public boolean esEjecutable() {
        return true;
    }

    /**
     * Completa la mision, cambiando su estado a completada.
     *
     * @param mision Mision que se desea completar.
     * @throws IllegalArgumentException Si la mision es nula.
     */
    public void alCompletar(Mision mision) {
        if (mision == null) throw new IllegalArgumentException("La misión no puede ser nula");
        mision.cambiarEstado(new EstadoCompletada());
    }

    /**
     * Registra el fracaso de la mision, cambiando su estado a fallida.
     *
     * @param mision Mision cuyo fracaso se desea registrar.
     * @throws IllegalArgumentException Si la mision es nula.
     */
    public void alFracasar(Mision mision) {
        if (mision == null) throw new IllegalArgumentException("La misión no puede ser nula");
        mision.cambiarEstado(new EstadoFallida());
    }

    /**
     * Obtiene el nombre del estado disponible.
     *
     * @return Nombre del estado, "DISPONIBLE".
     */
    public String getNombreEstado() {
        return nombre_;
    }
}
