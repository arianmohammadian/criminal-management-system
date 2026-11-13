package domain;

import domain.enums.TipoRol;
import domain.enums.EstadoSecuaz;

/**
 * Secuaz
 *
 * Representa un secuaz de la organizacion criminal. Gestiona su rol principal,
 * estado de disponibilidad y cantidad acumulada de botin.
 *
 * @author Arian Mohammadian
 */
public class Secuaz {
    private final String id_;
    private TipoRol rolBase_;
    private double cantidadAcumulada_;
    private EstadoSecuaz estado_;

    // CONSTRUCTOR

    /**
     * Crea un nuevo secuaz con el identificador y rol base indicados.
     * Inicialmente, el secuaz esta libre y no tiene botin acumulado.
     *
     * @param id Identificador unico del secuaz.
     * @param rolBase Rol principal del secuaz.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         o si el rol base es nulo.
     */
    public Secuaz(String id, TipoRol rolBase) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "El ID del secuaz no puede estar vacío"
            );
        }

        if (rolBase == null) {
            throw new IllegalArgumentException(
                "El rol base no puede ser nulo"
            );
        }

        id_ = id;
        rolBase_ = rolBase;
        cantidadAcumulada_ = 0.0;
        estado_ = EstadoSecuaz.LIBRE;
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene el identificador del secuaz.
     *
     * @return Identificador del secuaz.
     */
    public String getId() {
        return id_;
    }

    /**
     * Obtiene el rol principal del secuaz.
     *
     * @return Rol base del secuaz.
     */
    public TipoRol getRolBase() {
        return rolBase_;
    }

    /**
     * Obtiene la cantidad total de botin acumulada por el secuaz.
     *
     * @return Cantidad de botin acumulada.
     */
    public double getCantidadAcumulada() {
        return cantidadAcumulada_;
    }

    /**
     * Obtiene el estado actual del secuaz.
     *
     * @return Estado actual del secuaz.
     */
    public EstadoSecuaz getEstado() {
        return estado_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Comprueba si el secuaz esta disponible para participar en una mision.
     *
     * @return true si el secuaz esta libre; false en caso contrario.
     */
    public boolean estaLibre() {
        return estado_ == EstadoSecuaz.LIBRE;
    }

    /**
     * Libera a un secuaz ocupado, cambiando su estado a libre.
     *
     * @throws IllegalStateException Si el secuaz no esta ocupado.
     */
    public void desocupar() {
        if (estado_ != EstadoSecuaz.OCUPADO) {
            throw new IllegalStateException(
                "El secuaz no está ocupado"
            );
        }

        estado_ = EstadoSecuaz.LIBRE;
    }

    /**
     * Rescata a un secuaz encarcelado, cambiando su estado a libre.
     *
     * @throws IllegalStateException Si el secuaz no esta encarcelado.
     */
    public void rescatarDePrision() {
        if (estado_ != EstadoSecuaz.PRISIONERO) {
            throw new IllegalStateException(
                "El secuaz no está encarcelado"
            );
        }

        estado_ = EstadoSecuaz.LIBRE;
    }

    /**
     * Encarcela al secuaz, cambiando su estado a prisionero.
     *
     * @throws IllegalStateException Si el secuaz ya esta encarcelado.
     */
    public void encarcelar() {
        if (estado_ == EstadoSecuaz.PRISIONERO) {
            throw new IllegalStateException(
                "El secuaz ya está encarcelado"
            );
        }

        estado_ = EstadoSecuaz.PRISIONERO;
    }

    /**
     * Asigna el secuaz a una mision, cambiando su estado a ocupado.
     * Solo puede asignarse si esta libre.
     *
     * @throws IllegalStateException Si el secuaz no esta libre.
     */
    public void asignarAMision() {
        if (!estaLibre()) {
            throw new IllegalStateException(
                "El secuaz no está disponible para una misión"
            );
        }

        estado_ = EstadoSecuaz.OCUPADO;
    }

    /**
     * Modifica el rol principal del secuaz.
     *
     * @param nuevoRolBase Nuevo rol principal del secuaz.
     * @throws IllegalArgumentException Si el nuevo rol es nulo.
     */
    public void setRolBase(TipoRol nuevoRolBase) {
        if (nuevoRolBase == null) {
            throw new IllegalArgumentException(
                "El rol base no puede ser nulo"
            );
        }

        rolBase_ = nuevoRolBase;
    }

    /**
     * Incrementa la cantidad de botin acumulada por el secuaz.
     *
     * @param monto Cantidad de botin que se desea añadir.
     * @throws IllegalArgumentException Si el monto es negativo
     *         o no es un numero finito.
     */
    public void sumarBotin(double monto) {
        if (monto < 0 || !Double.isFinite(monto)) {
            throw new IllegalArgumentException(
                "El monto debe ser finito y no negativo"
            );
        }

        cantidadAcumulada_ += monto;
    }

    // ----------
}
