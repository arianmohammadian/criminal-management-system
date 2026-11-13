package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import domain.exceptions.MisionCicloDependenciaException;
import domain.exceptions.MisionNoEncontradaException;
import domain.exceptions.MisionYaExistenteException;
import domain.state.EstadoBloqueada;
import domain.state.EstadoCompletada;
import domain.state.EstadoDisponible;

/**
 * GrafoMisiones
 *
 * Gestiona las misiones de la organizacion y sus relaciones de dependencia
 * mediante un grafo dirigido.
 *
 * Permite añadir y eliminar misiones, establecer prerrequisitos, detectar
 * ciclos, determinar un orden de ejecucion y actualizar la disponibilidad
 * de las misiones segun el estado de sus dependencias.
 *
 * @author Arian Mohammadian
 */
public class GrafoMisiones {
    private final Map<String, Mision> nodos;
    private final Map<String, Set<String>> sucesores;
    private final Map<String, Set<String>> prerrequisitos;

    /**
     * Crea un grafo de misiones inicialmente vacio, sin dependencias.
     */
    public GrafoMisiones() {
        nodos = new HashMap<>();
        sucesores = new HashMap<>();
        prerrequisitos = new HashMap<>();
    }

    /**
     * Añade una mision al grafo, identificada por su ID.
     * Inicialmente, la mision no tiene dependencias registradas.
     *
     * @param m Mision que se desea incorporar al grafo.
     * @throws IllegalArgumentException Si la mision o su identificador son nulos.
     * @throws MisionYaExistenteException Si ya existe una mision
     *         con el mismo identificador.
     */
    public void anadirMision(Mision m) throws MisionYaExistenteException {
        if (m == null || m.getId() == null) {
            throw new IllegalArgumentException("La misión o su ID no pueden ser nulos.");
        }
        if (nodos.containsKey(m.getId())) {
            throw new MisionYaExistenteException(m.getId());
        }

        String id = m.getId();
        nodos.put(id, m);
        sucesores.putIfAbsent(id, new HashSet<>());
        prerrequisitos.putIfAbsent(id, new HashSet<>());
    }

    /**
     * Elimina una mision del grafo junto con todas sus dependencias.
     * Despues de eliminarla, actualiza los estados de las misiones restantes.
     *
     * @param id Identificador de la mision que se desea eliminar.
     * @throws MisionNoEncontradaException Si no existe una mision
     *         con el identificador indicado.
     */
    public void eliminarMision(String id) throws MisionNoEncontradaException {
        if (!nodos.containsKey(id)) {
            throw new MisionNoEncontradaException(id);
        }

        for (String preId : prerrequisitos.getOrDefault(id, new HashSet<>())) {
            Set<String> sucSet = sucesores.get(preId);
            if (sucSet != null) sucSet.remove(id);
        }

        for (String sucId : sucesores.getOrDefault(id, new HashSet<>())) {
            Set<String> preSet = prerrequisitos.get(sucId);
            if (preSet != null) preSet.remove(id);
        }

        nodos.remove(id);
        sucesores.remove(id);
        prerrequisitos.remove(id);

        actualizarEstadosMisiones();
    }

    /**
     * Busca una mision en el grafo mediante su identificador.
     *
     * @param id Identificador de la mision que se desea consultar.
     * @return Mision asociada al identificador indicado.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public Mision buscarMision(String id) throws MisionNoEncontradaException {
        Mision m = nodos.get(id);
        if (m == null) {
            throw new MisionNoEncontradaException(id);
        }
        return m;
    }

    /**
     * Comprueba si existe una mision registrada en el grafo.
     *
     * @param id Identificador de la mision.
     * @return true si la mision existe; false en caso contrario.
     */
    public boolean existeMision(String id) {
        return nodos.containsKey(id);
    }

    /**
     * Obtiene todas las misiones registradas en el grafo.
     *
     * @return Nueva lista con todas las misiones del grafo.
     */
    public List<Mision> obtenerTodas() {
        return new ArrayList<>(nodos.values());
    }

    /**
     * Establece una dependencia entre dos misiones.
     * La mision indicada no podra ejecutarse hasta que se haya completado
     * su prerrequisito.
     *
     * Si la nueva dependencia genera un ciclo, se deshace la insercion.
     * En caso contrario, se actualiza el estado de la mision dependiente.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador de la mision que debe completarse antes.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     * @throws IllegalArgumentException Si una mision intenta depender de si misma.
     * @throws MisionCicloDependenciaException Si la dependencia genera un ciclo.
     */
    public void anadirDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException, MisionCicloDependenciaException {
        if (!nodos.containsKey(idMision)) throw new MisionNoEncontradaException(idMision);
        if (!nodos.containsKey(idPrerrequisito)) throw new MisionNoEncontradaException(idPrerrequisito);
        if (idMision.equals(idPrerrequisito)) {
            throw new IllegalArgumentException("Una misión no puede ser prerrequisito de sí misma.");
        }

        prerrequisitos.get(idMision).add(idPrerrequisito);
        sucesores.get(idPrerrequisito).add(idMision);

        if (detectarCiclos()) {
            prerrequisitos.get(idMision).remove(idPrerrequisito);
            sucesores.get(idPrerrequisito).remove(idMision);
            throw new MisionCicloDependenciaException(idMision);
        }

        actualizarEstadoMision(idMision);
    }

    /**
     * Elimina una dependencia entre dos misiones.
     * Despues de eliminarla, actualiza el estado de la mision dependiente.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador del prerrequisito que se desea eliminar.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     */
    public void eliminarDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException {
        if (!nodos.containsKey(idMision)) throw new MisionNoEncontradaException(idMision);
        if (!nodos.containsKey(idPrerrequisito)) throw new MisionNoEncontradaException(idPrerrequisito);

        if (prerrequisitos.containsKey(idMision)) {
            prerrequisitos.get(idMision).remove(idPrerrequisito);
        }

        if (sucesores.containsKey(idPrerrequisito)) {
            sucesores.get(idPrerrequisito).remove(idMision);
        }

        actualizarEstadoMision(idMision);
    }

    /**
     * Comprueba si el grafo contiene dependencias ciclicas.
     * Utiliza una busqueda en profundidad para detectar ciclos dirigidos.
     *
     * @return true si existe algun ciclo; false en caso contrario.
     */
    public boolean detectarCiclos() {
        Map<String, Integer> estadoVisita = new HashMap<>();
        for (String id : nodos.keySet()) {
            estadoVisita.put(id, 0); // 0: Blanco, 1: Gris, 2: Negro
        }

        for (String id : nodos.keySet()) {
            if (estadoVisita.get(id) == 0) {
                if (dfsTieneCiclo(id, estadoVisita)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsTieneCiclo(String actual, Map<String, Integer> estadoVisita) {
        estadoVisita.put(actual, 1);

        for (String sucId : sucesores.getOrDefault(actual, new HashSet<>())) {
            int estadoSuc = estadoVisita.getOrDefault(sucId, 0);
            if (estadoSuc == 1) {
                return true;
            }
            if (estadoSuc == 0 && dfsTieneCiclo(sucId, estadoVisita)) {
                return true;
            }
        }

        estadoVisita.put(actual, 2);
        return false;
    }

    /**
     * Obtiene un orden de ejecucion que respeta las dependencias del grafo.
     * Entre las misiones cuyos prerrequisitos ya se han procesado,
     * se priorizan aquellas con mayor botin.
     *
     * @return Lista de misiones ordenadas topologicamente, priorizando
     *         el mayor botin entre las misiones elegibles en cada paso.
     */
    public List<Mision> obtenerOrdenEjecucion() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String id : nodos.keySet()) {
            inDegree.put(id, prerrequisitos.getOrDefault(id, new HashSet<>()).size());
        }

        PriorityQueue<Mision> pq = new PriorityQueue<>((m1, m2) -> Double.compare(m2.getBotin(), m1.getBotin()));

        for (String id : nodos.keySet()) {
            if (inDegree.get(id) == 0) {
                pq.add(nodos.get(id));
            }
        }

        List<Mision> orden = new ArrayList<>();
        while (!pq.isEmpty()) {
            Mision m = pq.poll();
            orden.add(m);

            for (String sucId : sucesores.getOrDefault(m.getId(), new HashSet<>())) {
                inDegree.put(sucId, inDegree.get(sucId) - 1);
                if (inDegree.get(sucId) == 0) {
                    pq.add(nodos.get(sucId));
                }
            }
        }

        return orden;
    }

    /**
     * Obtiene las misiones que pueden ejecutarse en su estado actual.
     *
     * @return Nueva lista con las misiones ejecutables del grafo.
     */
    public List<Mision> obtenerMisionesDisponibles() {
        List<Mision> disponibles = new ArrayList<>();
        for (Mision m : nodos.values()) {
            if (m.esEjecutable()) {
                disponibles.add(m);
            }
        }
        return disponibles;
    }

    /**
     * Reevalua el estado de las misiones que dependen directamente
     * de una mision completada.
     *
     * Si la mision indicada es nula o no tiene identificador,
     * no realiza ninguna accion.
     *
     * @param misionCompletada Mision cuya finalizacion puede desbloquear
     *         otras misiones.
     */
    public void evaluarDesbloqueos(Mision misionCompletada) {
        if (misionCompletada == null || misionCompletada.getId() == null) return;

        Set<String> sucSet = sucesores.getOrDefault(misionCompletada.getId(), new HashSet<>());
        for (String sucId : sucSet) {
            actualizarEstadoMision(sucId);
        }
    }

    /**
     * Obtiene todos los prerrequisitos directos e indirectos de una mision.
     *
     * @param idMision Identificador de la mision que se desea consultar.
     * @return Lista de misiones que son prerrequisitos directos o indirectos.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> obtenerPrerrequisitos(String idMision) throws MisionNoEncontradaException {
        if (!nodos.containsKey(idMision)) throw new MisionNoEncontradaException(idMision);

        Set<String> visitados = new HashSet<>();
        recopilarPrerrequisitosRec(idMision, visitados);
        visitados.remove(idMision);

        List<Mision> resultado = new ArrayList<>();
        for (String preId : visitados) {
            Mision m = nodos.get(preId);
            if (m != null) resultado.add(m);
        }
        return resultado;
    }

    private void recopilarPrerrequisitosRec(String idActual, Set<String> visitados) {
        Set<String> pres = prerrequisitos.get(idActual);
        if (pres != null) {
            for (String preId : pres) {
                if (visitados.add(preId)) {
                    recopilarPrerrequisitosRec(preId, visitados);
                }
            }
        }
    }

    /**
     * Obtiene los sucesores directos de una mision que quedarian disponibles
     * si esta se considerase completada.
     *
     * Solo incluye los sucesores cuyos demas prerrequisitos ya estan
     * completados. No modifica el estado de ninguna mision.
     *
     * @param idMision Identificador de la mision cuyos sucesores se consultan.
     * @return Lista de sucesores directos que cumplen las condiciones indicadas.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> obtenerSucesores(String idMision) throws MisionNoEncontradaException {
        if (!nodos.containsKey(idMision)) throw new MisionNoEncontradaException(idMision);

        List<Mision> resultado = new ArrayList<>();
        Set<String> sucIds = sucesores.get(idMision);
        if (sucIds == null) return resultado;

        for (String sucId : sucIds) {
            Mision sucMision = nodos.get(sucId);
            if (sucMision == null) continue;

            Set<String> presOfSuc = prerrequisitos.getOrDefault(sucId, new HashSet<>());
            boolean quedariaDisponible = true;

            for (String preId : presOfSuc) {
                if (preId.equals(idMision)) continue;
                Mision p = nodos.get(preId);
                if (p == null || !(p.getEstadoActual() instanceof EstadoCompletada)) {
                    quedariaDisponible = false;
                    break;
                }
            }

            if (quedariaDisponible) {
                resultado.add(sucMision);
            }
        }
        return resultado;
    }

    private void actualizarEstadosMisiones() {
        for (String id : nodos.keySet()) {
            actualizarEstadoMision(id);
        }
    }

    private void actualizarEstadoMision(String idMision) {
        Mision mision = nodos.get(idMision);
        if (mision == null) return;

        if (mision.getEstadoActual() instanceof EstadoCompletada) {
            return;
        }

        Set<String> pres = prerrequisitos.getOrDefault(idMision, new HashSet<>());
        boolean todosCompletados = true;

        for (String preId : pres) {
            Mision preMision = nodos.get(preId);
            if (preMision == null || !(preMision.getEstadoActual() instanceof EstadoCompletada)) {
                todosCompletados = false;
                break;
            }
        }

        if (todosCompletados) {
            if (mision.getEstadoActual() instanceof EstadoBloqueada) {
                mision.cambiarEstado(new EstadoDisponible());
            }
        } else {
            mision.cambiarEstado(new EstadoBloqueada());
        }
    }
}