package domain;

import java.util.Map;
import java.util.NoSuchElementException;

import domain.enums.EstadoSecuaz;

import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * ConjuntoSecuaces
 *
 * Gestiona la coleccion de secuaces de la organizacion. Permite anyadir, eliminar
 * y buscar secuaces, y tambien consultar aquellos que estan disponibles.
 *
 * @author Arian Mohammadian
 */
public class ConjuntoSecuaces {
    private Map<String, Secuaz> plantillaSecuaces_;

    // CONSTRUCTOR

    /**
     * Crea una coleccion de secuaces inicialmente vacia.
     */
    public ConjuntoSecuaces() {
        plantillaSecuaces_ = new HashMap<>();
    }

    // ----------

    // GETTERS CLASE

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Anade un nuevo secuaz a la coleccion, utilizando su identificador
     * como clave. No permite registrar secuaces con identificadores repetidos.
     *
     * @param secuaz Secuaz que se desea incorporar a la coleccion.
     * @throws IllegalArgumentException Si el secuaz es nulo o ya existe
     *         otro secuaz con el mismo identificador.
     */
    public void addSecuaz(Secuaz secuaz) {
        if(secuaz == null) {
            throw new IllegalArgumentException(
                "El secuaz no puede ser nulo"
            );
        }

        if (plantillaSecuaces_.containsKey(secuaz.getId())) {
            throw new IllegalArgumentException(
                "Ya existe un secuaz con ID: " + secuaz.getId()
            );
        }

        plantillaSecuaces_.put(secuaz.getId(), secuaz);
    }

    /**
     * Elimina de la coleccion el secuaz con el identificador indicado.
     * Solo permite eliminar secuaces que se encuentren en estado libre.
     *
     * @param id Identificador del secuaz que se desea eliminar.
     * @throws NoSuchElementException Si no existe un secuaz con ese identificador.
     * @throws IllegalStateException Si el secuaz esta ocupado o encarcelado.
     */
    public void eliminarSecuaz(String id) {
        Secuaz secuaz = consultarSecuaz(id);

        if (secuaz.getEstado() == EstadoSecuaz.OCUPADO || secuaz.getEstado() == EstadoSecuaz.PRISIONERO) {
            throw new IllegalStateException(
                "No se puede eliminar un secuaz ocupado/prisionero"
            );
        }

        plantillaSecuaces_.remove(id);
    }

    /**
     * Busca un secuaz en la coleccion mediante su identificador.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @return Secuaz asociado al identificador indicado.
     * @throws NoSuchElementException Si no existe un secuaz con ese identificador.
     */
    public Secuaz consultarSecuaz(String id) {
        Secuaz secuaz = plantillaSecuaces_.get(id);

        if (secuaz == null) {
            throw new NoSuchElementException(
                "No existe ningún secuaz con ID: " + id
            );
        }

        return secuaz;
    }

    /**
     * Comprueba si existe un secuaz registrado con el identificador indicado.
     *
     * @param id Identificador del secuaz que se desea comprobar.
     * @return true si existe un secuaz con ese identificador;
     *         false en caso contrario.
     */
    public boolean existeSecuaz(String id) {
        return plantillaSecuaces_.containsKey(id);
    }

    /**
     * Obtiene una lista con todos los secuaces registrados en la coleccion.
     *
     * @return Nueva lista que contiene todos los secuaces registrados.
     */
    public List<Secuaz> listarTodos() {
        return new ArrayList<>(plantillaSecuaces_.values());
    }

    /**
     * Obtiene una lista con todos los secuaces que se encuentran
     * disponibles para participar en misiones.
     *
     * @return Nueva lista que contiene unicamente los secuaces en estado libre.
     */
    public List<Secuaz> listarSecuacesLibres() {
        List<Secuaz> result = new ArrayList<>();

        for(Secuaz secuaz : plantillaSecuaces_.values()) {
            if(secuaz.estaLibre()) result.add(secuaz);
        }

        return result;
    }
}
