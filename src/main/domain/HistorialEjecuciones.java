
package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HistorialEjecuciones
 *
 * Administra el historial de todas las misiones llevadas a cabo en el sistema.
 * Ofrece herramientas para consultar las ejecuciones por secuaz, calcular
 * hojas de servicios e identificar el complice habitual de un secuaz.
 *
 * @author Arian Mohammadian
 */
public class HistorialEjecuciones {
    private List<EjecucionMision> ejecuciones_;

    // CONSTRUCTOR

    /**
     * Crea un historial de ejecuciones inicialmente vacio.
     */
    public HistorialEjecuciones() {
        this.ejecuciones_ = new ArrayList<>();
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene todas las ejecuciones registradas en el historial.
     *
     * @return Nueva lista con las ejecuciones registradas.
     */
    public List<EjecucionMision> obtenerHistorial() {
        return new ArrayList<>(ejecuciones_);
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Registra una nueva ejecucion de mision en el historial.
     *
     * @param e Ejecucion que se desea registrar.
     * @throws IllegalArgumentException Si la ejecucion es nula.
     */
    public void registrarEjecucion(EjecucionMision e) {
        if (e == null) {
            throw new IllegalArgumentException(
                "La ejecución que se quiere registrar no puede ser nula"
            );
        }

        ejecuciones_.add(e);
    }

    /**
     * Busca una ejecucion registrada mediante su identificador.
     *
     * @param idEjecucion Identificador de la ejecucion que se desea buscar.
     * @return Ejecucion asociada al identificador indicado, o null si
     *         no existe ninguna ejecucion con ese identificador.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     */
    public EjecucionMision buscarEjecucion(String idEjecucion) {
        if (idEjecucion == null || idEjecucion.isBlank()) {
            throw new IllegalArgumentException(
                "El ID de la ejecución que se busca no puede ser nulo o estar vacío"
            );
        }

        for (EjecucionMision ejec : ejecuciones_) {
            if (idEjecucion.equals(ejec.getIdEjecucion())) return ejec;
        }
        return null;
    }

    /**
     * Obtiene las ejecuciones en las que ha participado un secuaz.
     * Una ejecucion se incluye si el secuaz aparece entre los miembros
     * de su equipo, independientemente del resultado de la mision.
     *
     * @param idSecuaz Identificador del secuaz que se desea consultar.
     * @return Nueva lista con las ejecuciones en las que ha participado
     *         el secuaz, o una lista vacia si no se encuentra ninguna.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     */
    public List<EjecucionMision> obtenerEjecucionesDeSecuaz(String idSecuaz) {
        if (idSecuaz == null || idSecuaz.isBlank()) {
            throw new IllegalArgumentException(
                "El ID del secuaz no puede ser nulo o estar vacío"
            );
        }

        List<EjecucionMision> result = new ArrayList<>();

        for (EjecucionMision ejec : ejecuciones_) {
            if (ejec.getEquipo() != null) {
                for (AsignacionRol asig : ejec.getEquipo()) {
                    if (asig != null && asig.getSecuaz() != null && idSecuaz.equals(asig.getSecuaz().getId())) {
                        result.add(ejec);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Identifica el complice habitual de un secuaz a partir del historial.
     *
     * Se considera complice habitual al secuaz que ha coincidido con el
     * secuaz indicado en un mayor numero de ejecuciones registradas.
     * El propio secuaz consultado se excluye del calculo.
     *
     * Si varios complices tienen la misma frecuencia maxima, no se
     * garantiza cual de ellos sera seleccionado.
     *
     * @param idSecuaz Identificador del secuaz cuyo complice habitual
     *        se desea consultar.
     * @return Secuaz con mayor frecuencia de participacion conjunta,
     *         o null si no se encuentra ningun complice.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     */
    public Secuaz calcularCompliceHabitual(String idSecuaz) {
        if (idSecuaz == null || idSecuaz.isBlank()) {
            throw new IllegalArgumentException(
                "El ID del secuaz no puede ser nulo o estar vacío"
            );
        }

        List<EjecucionMision> misionesDelSecuaz = obtenerEjecucionesDeSecuaz(idSecuaz);
        Map<Secuaz, Integer> frecuencias = new HashMap<>();

        for (EjecucionMision e : misionesDelSecuaz) {
            if (e.getEquipo() != null) {
                for (AsignacionRol asig : e.getEquipo()) {
                    if (asig != null && asig.getSecuaz() != null) {
                        Secuaz complice = asig.getSecuaz();
                        if (!idSecuaz.equals(complice.getId())) {
                            frecuencias.put(complice, frecuencias.getOrDefault(complice, 0) + 1);
                        }
                    }
                }
            }
        }

        Secuaz compliceHabitual = null;
        int maxFrecuencia = 0;

        for (Map.Entry<Secuaz, Integer> entry : frecuencias.entrySet()) {
            if (entry.getValue() > maxFrecuencia) {
                maxFrecuencia = entry.getValue();
                compliceHabitual = entry.getKey();
            }
        }

        return compliceHabitual;
    }

    /**
     * Calcula la hoja de servicios de un secuaz.
     * Cuenta el numero de ejecuciones registradas en las que ha participado,
     * independientemente de si han tenido exito o han fracasado.
     *
     * @param idSecuaz Identificador del secuaz que se desea consultar.
     * @return Numero total de ejecuciones en las que ha participado el secuaz.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     */
    public int calcularHojaServicios(String idSecuaz) {
        if (idSecuaz == null || idSecuaz.isBlank()) {
            throw new IllegalArgumentException(
                "El ID del secuaz no puede ser nulo o estar vacío"
            );
        }

        return obtenerEjecucionesDeSecuaz(idSecuaz).size();
    }

    // ----------
}
