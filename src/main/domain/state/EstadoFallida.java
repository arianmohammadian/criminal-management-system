
package domain.state;

import domain.Mision;

/**
 * EstadoFallida
 *
 * Representa el estado de una mision que ha fracasado.
 * Permite volver a ejecutar la mision, pudiendo completarse
 * posteriormente o permanecer en estado fallida.
 */
public class EstadoFallida implements EstadoMision {
    private static String nombre_ = "FALLIDA";

    /**
     * Crea una instancia de estado fallida.
     */
    public EstadoFallida() {}

    /**
     * Comprueba si una mision fallida puede volver a ejecutarse.
     *
     * @return true, ya que la mision permite nuevos intentos.
     */
    public boolean esEjecutable() {
        return true;
    }

    /**
     * Completa una mision anteriormente fallida, cambiando
     * su estado a completada.
     *
     * @param mision Mision que se desea completar.
     * @throws IllegalArgumentException Si la mision es nula.
     */
    public void alCompletar(Mision mision) {
        if (mision == null) throw new IllegalArgumentException("La misión no puede ser nula");
        mision.cambiarEstado(new EstadoCompletada());
    }

    /**
     * Registra un nuevo fracaso, manteniendo la mision en estado fallida.
     *
     * @param mision Mision cuyo fracaso se desea registrar.
     * @throws IllegalArgumentException Si la mision es nula.
     */
    public void alFracasar(Mision mision) {
        if (mision == null) throw new IllegalArgumentException("La misión no puede ser nula");
        mision.cambiarEstado(new EstadoFallida());
    }

    /**
     * Obtiene el nombre del estado fallida.
     *
     * @return Nombre del estado, "FALLIDA".
     */
    public String getNombreEstado() {
        return nombre_;
    }
}
