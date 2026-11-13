
package domain.enums;

/**
 * EstadoSecuaz
 *
 * Define los posibles estados de un secuaz de la organizacion criminal.
 * Determina si se encuentra disponible, asignado a una mision
 * o encarcelado.
 */
public enum EstadoSecuaz {

    /** El secuaz esta asignado a una mision. */
    OCUPADO,

    /** El secuaz esta disponible para participar en misiones. */
    LIBRE,

    /** El secuaz se encuentra encarcelado. */
    PRISIONERO
}
