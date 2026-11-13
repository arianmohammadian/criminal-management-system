package domain;

import domain.enums.TipoRol;

/**
 * RequisitoRol
 *
 * Representa un requisito de personal para una mision de la organizacion.
 * Define el tipo de rol necesario y la cantidad de secuaces requeridos
 * para cubrirlo.
 */
public class RequisitoRol {

    private final TipoRol rol;
    private int cantidadRequerida;

    /**
     * Crea un requisito de rol con el tipo de rol y la cantidad indicada.
     *
     * @param rol Tipo de rol necesario para cumplir el requisito.
     * @param cantidadRequerida Numero de secuaces necesarios para ese rol.
     * @throws IllegalArgumentException Si el rol es nulo o la cantidad
     *         requerida no es positiva.
     */
    public RequisitoRol(TipoRol rol, int cantidadRequerida) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (cantidadRequerida <= 0) {
            throw new IllegalArgumentException("La cantidad requerida debe ser mayor que cero");
        }
        this.rol = rol;
        this.cantidadRequerida = cantidadRequerida;
    }

    /**
     * Obtiene el tipo de rol asociado al requisito.
     *
     * @return Tipo de rol requerido.
     */
    public TipoRol getTipoRol() {
        return rol;
    }

    /**
     * Obtiene la cantidad de secuaces necesarios para cubrir el requisito.
     *
     * @return Cantidad de secuaces requerida.
     */
    public int getCantidadRequerida() {
        return cantidadRequerida;
    }

    /**
     * Comprueba si la cantidad de secuaces disponibles es suficiente
     * para cumplir el requisito.
     *
     * @param cantidadDisponible Numero de secuaces disponibles para el rol.
     * @return true si la cantidad disponible es mayor o igual que la
     *         requerida; false en caso contrario.
     */
    public boolean cumpleRequisito(int cantidadDisponible) {
        return cantidadDisponible >= cantidadRequerida;
    }

    /**
     * Modifica la cantidad de secuaces requeridos para el rol.
     *
     * @param newCantidad Nueva cantidad de secuaces requerida.
     * @throws IllegalArgumentException Si la nueva cantidad no es positiva.
     */
    public void setCantidadRequerida(int newCantidad) {
        if (newCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad requerida debe ser mayor a cero");
        }
        cantidadRequerida = newCantidad;
    }

}
