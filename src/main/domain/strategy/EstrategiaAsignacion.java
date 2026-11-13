
package domain.strategy;

import domain.Mision;
import domain.Secuaz;
import domain.enums.TipoRol;
import java.util.List;
import java.util.Map;

/**
 * EstrategiaAsignacion
 *
 * Define la interfaz comun para las estrategias de asignacion automatica
 * de secuaces a misiones.
 *
 * Permite implementar distintos algoritmos de seleccion de equipos,
 * manteniendo una interfaz comun para todas las estrategias.
 */
public interface EstrategiaAsignacion {

    /**
     * Selecciona equipos de secuaces para las misiones indicadas
     * siguiendo el criterio de la estrategia implementada.
     *
     * Cada equipo asocia los secuaces seleccionados con el rol
     * que desempenaran durante la ejecucion de su mision.
     *
     * @param misiones Lista de misiones que se desean considerar.
     * @param candidatos Lista de secuaces candidatos para formar los equipos.
     * @return Mapa que relaciona cada mision seleccionada con otro mapa
     *         que asocia los secuaces de su equipo con sus roles asignados.
     */
    Map<Mision, Map<Secuaz, TipoRol>> seleccionarEquipos(List<Mision> misiones, List<Secuaz> candidatos);
}
