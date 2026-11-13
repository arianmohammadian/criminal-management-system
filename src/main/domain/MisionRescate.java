
package domain;

import java.util.List;

import domain.enums.EstadoSecuaz;

/**
 * MisionRescate
 *
 * Representa una mision especial destinada a rescatar a un secuaz
 * encarcelado de la organizacion criminal.
 *
 * Hereda el comportamiento general de Mision, pero no dispone de
 * botin economico y, al completarse, libera al secuaz objetivo.
 */
public class MisionRescate extends Mision {
    private Secuaz secuazObjetivo;

    /**
     * Crea una nueva mision de rescate asociada a un secuaz encarcelado.
     * Inicialmente, la mision esta disponible y su botin es cero.
     *
     * @param id Identificador de la mision de rescate.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param requisitos Lista de requisitos de rol necesarios para la mision.
     * @param objetivo Secuaz encarcelado que se desea rescatar.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         la probabilidad esta fuera del intervalo [0, 100],
     *         los requisitos no son validos o el secuaz objetivo es nulo.
     * @throws IllegalStateException Si el secuaz objetivo no esta encarcelado.
     */
    public MisionRescate(String id, double probFracaso, List<RequisitoRol> requisitos, Secuaz objetivo) {
        super(id, 0.0, probFracaso, requisitos);
        if (objetivo == null) {
            throw new IllegalArgumentException("El secuaz objetivo no puede ser nulo");
        }
        if (objetivo.getEstado() != EstadoSecuaz.PRISIONERO) {
            throw new IllegalStateException("El secuaz objetivo debe estar en estado PRISIONERO.");
        }
        this.secuazObjetivo = objetivo;
    }

    /**
     * Obtiene el secuaz que se desea rescatar.
     *
     * @return Secuaz objetivo de la mision de rescate.
     */
    public Secuaz getSecuazObjetivo() {
        return secuazObjetivo;
    }

    /**
     * Impide asignar un botin economico a la mision de rescate.
     * Un valor de cero se acepta sin realizar modificaciones.
     *
     * @param botin Cantidad de botin que se intenta establecer.
     * @throws UnsupportedOperationException Si el botin es distinto de cero.
     */
    @Override
    public void setBotin(double botin) {
        if (botin == 0.0) return;
        throw new UnsupportedOperationException("Una misión de rescate no tiene botín económico");
    }

    /**
     * Completa la mision de rescate y libera al secuaz objetivo.
     *
     * Si la mision no es ejecutable, no realiza ninguna accion.
     * En caso contrario, delega la transicion al estado correspondiente
     * y rescata al secuaz objetivo si continua encarcelado.
     */
    @Override
    public void alCompletar() {
        if (!esEjecutable()) {
            return;
        }

        super.alCompletar();
        if (secuazObjetivo != null && secuazObjetivo.getEstado() == EstadoSecuaz.PRISIONERO) {
            secuazObjetivo.rescatarDePrision();
        }
    }
}
