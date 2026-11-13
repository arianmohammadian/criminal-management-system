package controllers;

import domain.Secuaz;
import domain.ConjuntoSecuaces;
import domain.HistorialEjecuciones;
import domain.enums.TipoRol;

import java.util.List;


/**
 *
 * CtrlSecuaces
 *
 * Controlador encargado de gestionar las operaciones relacionadas con los secuaces
 * de la organizacion. Coordina las operaciones sobre la plantilla y las
 * consultas de informacion individual y historica.
 *
 * @author Arian Mohammadian
 */
public class CtrlSecuaces {
    private final ConjuntoSecuaces conjuntoSecuaces_;

    // CONSTRUCTOR

    /**
     * Crea un controlador asociado a una coleccion de secuaces.
     *
     * @param conjuntoSecuaces Coleccion de secuaces que gestionara el controlador.
     * @throws IllegalArgumentException Si la coleccion de secuaces es nula.
     */
    public CtrlSecuaces(ConjuntoSecuaces conjuntoSecuaces) {
        if (conjuntoSecuaces == null) {
            throw new IllegalArgumentException(
                "La colección de secuaces no puede ser nula"
            );
        }

        conjuntoSecuaces_ = conjuntoSecuaces;
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene la coleccion de secuaces gestionada por el controlador.
     *
     * @return Coleccion de secuaces asociada al controlador.
     */
    public ConjuntoSecuaces getConjuntoSecuaces() {
        return conjuntoSecuaces_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Crea un nuevo secuaz y lo incorpora a la coleccion.
     *
     * @param id Identificador del nuevo secuaz.
     * @param rolBase Rol principal del nuevo secuaz.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el rol base es nulo o ya existe un secuaz con el mismo identificador.
     */
    public void crearSecuaz(String id, TipoRol rolBase) {
        Secuaz secuaz = new Secuaz(id, rolBase);
        conjuntoSecuaces_.addSecuaz(secuaz);
    }

    /**
     * Modifica el rol principal de un secuaz existente.
     *
     * @param id Identificador del secuaz que se desea editar.
     * @param nuevoRolBase Nuevo rol principal del secuaz.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con el identificador indicado.
     * @throws IllegalArgumentException Si el nuevo rol es nulo.
     */
    public void editarSecuaz(String id, TipoRol nuevoRolBase) {
        Secuaz secuaz = conjuntoSecuaces_.consultarSecuaz(id);
        secuaz.setRolBase(nuevoRolBase);
    }

    /**
     * Elimina un secuaz de la coleccion mediante su identificador.
     * Solo permite eliminar secuaces que se encuentren libres.
     *
     * @param id Identificador del secuaz que se desea eliminar.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con el identificador indicado.
     * @throws IllegalStateException Si el secuaz esta ocupado o encarcelado.
     */
    public void eliminarSecuaz(String id) {
        conjuntoSecuaces_.eliminarSecuaz(id);
    }

    /**
     * Consulta un secuaz mediante su identificador.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @return Secuaz asociado al identificador indicado.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con ese identificador.
     */
    public Secuaz consultarSecuaz(String id) {
        return conjuntoSecuaces_.consultarSecuaz(id);
    }

    /**
     * Obtiene una lista con todos los secuaces registrados.
     *
     * @return Nueva lista con todos los secuaces de la coleccion.
     */
    public List<Secuaz> listarTodos() {
        return conjuntoSecuaces_.listarTodos();
    }

    /**
     * Obtiene una lista con los secuaces disponibles para participar en misiones.
     *
     * @return Nueva lista con los secuaces que se encuentran libres.
     */
    public List<Secuaz> listarSecuacesLibres() {
        return conjuntoSecuaces_.listarSecuacesLibres();
    }

    /**
     * Consulta la cantidad total de botin acumulada por un secuaz.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @return Cantidad de botin acumulada por el secuaz.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con el identificador indicado.
     */
    public double consultarEstadoCuentas(String id) {
        Secuaz secuaz = conjuntoSecuaces_.consultarSecuaz(id);
        return secuaz.getCantidadAcumulada();
    }

    /**
     * Consulta el complice habitual de un secuaz a partir del historial
     * de ejecuciones.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @param historial Historial de ejecuciones utilizado para la consulta.
     * @return Secuaz identificado como complice habitual por el historial.
     * @throws IllegalArgumentException Si el historial es nulo.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con el identificador indicado.
     */
    public Secuaz consultarCompliceHabitual(String id, HistorialEjecuciones historial) {
        if (historial == null) {
            throw new IllegalArgumentException("El historial de ejecuciones no puede ser nulo");
        }
        conjuntoSecuaces_.consultarSecuaz(id);
        return historial.calcularCompliceHabitual(id);
    }

    /**
     * Consulta la hoja de servicios de un secuaz a partir del historial
     * de ejecuciones.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @param historial Historial de ejecuciones utilizado para la consulta.
     * @return Numero de servicios calculado por el historial para el secuaz.
     * @throws IllegalArgumentException Si el historial es nulo.
     * @throws java.util.NoSuchElementException Si no existe un secuaz
     *         con el identificador indicado.
     */
    public int consultarHojaServicios(String id, HistorialEjecuciones historial) {
        if (historial == null) {
            throw new IllegalArgumentException("El historial de ejecuciones no puede ser nulo");
        }
        conjuntoSecuaces_.consultarSecuaz(id);
        return historial.calcularHojaServicios(id);
    }

    // ----------
}
