
package domain;

import domain.enums.TipoRol;

/**
 *
 * AsignacionRol
 *
 * Representa la relación entre un secuaz y el rol que se le asigna
 * dentro del equipo de una misión.
 *
 * @author Arian Mohammadian
 */
public class AsignacionRol {
    private TipoRol rolEmpleado_;
    private Secuaz secuaz_;

    // CONSTRUCTOR

    /**
     * Crea una asignacion que relaciona un secuaz con el rol
     * que desempenara dentro del equipo de una mision.
     *
     * @param rolEmpleado Rol que desempenara el secuaz en la mision.
     * @param secuaz Secuaz asociado a la asignacion.
     * @throws IllegalArgumentException Si el rol empleado o el secuaz son nulos.
     */
    public AsignacionRol(TipoRol rolEmpleado, Secuaz secuaz) {
        if (rolEmpleado == null) {
            throw new IllegalArgumentException(
                "El rol empleado no puede ser nulo"
            );
        }

        if (secuaz == null) {
            throw new IllegalArgumentException(
                "El secuaz no puede ser nulo"
            );
        }

        rolEmpleado_ = rolEmpleado;
        secuaz_ = secuaz;
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene el rol que desempena el secuaz en la mision.
     *
     * @return Rol empleado por el secuaz.
     */
    public TipoRol getRolEmpleado() {
        return rolEmpleado_;
    }

    /**
     * Obtiene el secuaz asociado a la asignacion.
     *
     * @return Secuaz asignado al rol.
     */
    public Secuaz getSecuaz() {
        return secuaz_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Modifica el rol que desempena el secuaz dentro de la mision.
     *
     * @param rolEmpleado Nuevo rol que se desea asignar al secuaz.
     * @throws IllegalArgumentException Si el nuevo rol es nulo.
     */
    public void setRolEmpleado(TipoRol rolEmpleado) {
        if (rolEmpleado == null) {
            throw new IllegalArgumentException(
                "El rol empleado no puede ser nulo"
            );
        }

        rolEmpleado_ = rolEmpleado;
    }

    /**
     * Modifica el secuaz asociado a la asignacion.
     *
     * @param secuaz Nuevo secuaz que se desea asociar al rol.
     * @throws IllegalArgumentException Si el nuevo secuaz es nulo.
     */
    public void setSecuaz(Secuaz secuaz) {
        if (secuaz == null) {
            throw new IllegalArgumentException(
                "El secuaz no puede ser nulo"
            );
        }

        secuaz_ = secuaz;
    }

    // ----------
}
