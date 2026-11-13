package controllers;

import domain.AsignacionRol;
import domain.ConjuntoSecuaces;
import domain.EjecucionMision;
import domain.GrafoMisiones;
import domain.HistorialEjecuciones;
import domain.Mision;
import domain.Secuaz;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase CtrlEstadisticas
 * 
 * Controlador de la capa de dominio que se encarga de calcular las estadísticas globales
 * del sindicato del Don. Utiliza algoritmos de búsqueda lineal con coste O(N) recorriendo
 * las listas de una sola pasada, utilizando variables auxiliares para calcular los elementos
 * del Top 3 de forma óptima.
 * 
 * @author Arian Mohammadian
 */
public class CtrlEstadisticas {

    private final ConjuntoSecuaces conjuntoSecuaces_;
    private final GrafoMisiones grafoMisiones_;
    private final HistorialEjecuciones historialEjecuciones_;

    /**
     * Constructor del controlador de estadísticas.
     * 
     * @param conjuntoSecuaces Contenedor de todos los secuaces registrados.
     * @param grafoMisiones Contenedor del grafo de misiones.
     * @param historialEjecuciones Registro histórico de ejecuciones de todas las misiones pasadas.
     */
    public CtrlEstadisticas(ConjuntoSecuaces conjuntoSecuaces, GrafoMisiones grafoMisiones, HistorialEjecuciones historialEjecuciones) {
        if (conjuntoSecuaces == null) {
            throw new IllegalArgumentException("El conjunto de secuaces no puede ser nulo");
        }
        if (grafoMisiones == null) {
            throw new IllegalArgumentException("El grafo de misiones no puede ser nulo");
        }
        if (historialEjecuciones == null) {
            throw new IllegalArgumentException("El historial de ejecuciones no puede ser nulo");
        }

        conjuntoSecuaces_ = conjuntoSecuaces;
        grafoMisiones_ = grafoMisiones;
        historialEjecuciones_ = historialEjecuciones;
    }

    /**
     * Consulta los tres secuaces que han participado en un mayor número de misiones.
     * 
     * @return List de objetos Secuaz que pertenecen al Top 3 de operativos.
     */
    public List<Secuaz> consultarTop3Operativos() {
        Map<String, Integer> participacionesPorSecuaz = new HashMap<>();
        for (EjecucionMision ejec : historialEjecuciones_.obtenerHistorial()) {
            if (ejec != null && ejec.getEquipo() != null) {
                Set<String> idsEnEjecucion = new HashSet<>();
                for (AsignacionRol asig : ejec.getEquipo()) {
                    if (asig != null && asig.getSecuaz() != null && asig.getSecuaz().getId() != null) {
                        idsEnEjecucion.add(asig.getSecuaz().getId());
                    }
                }
                for (String idSecuaz : idsEnEjecucion) {
                    participacionesPorSecuaz.merge(idSecuaz, 1, Integer::sum);
                }
            }
        }

        List<Secuaz> secuaces = conjuntoSecuaces_.listarTodos();
        Secuaz primero = null;
        Secuaz segundo = null;
        Secuaz tercero = null;

        int val1 = -1;
        int val2 = -1;
        int val3 = -1;

        for (int i = 0; i < secuaces.size(); i++) {
            Secuaz act = secuaces.get(i);
            int valor = participacionesPorSecuaz.getOrDefault(act.getId(), 0);

            if (valor > val1) {
                tercero = segundo;
                val3 = val2;
                segundo = primero;
                val2 = val1;
                primero = act;
                val1 = valor;
            } else if (valor > val2) {
                tercero = segundo;
                val3 = val2;
                segundo = act;
                val2 = valor;
            } else if (valor > val3) {
                tercero = act;
                val3 = valor;
            }
        }

        List<Secuaz> resultado = new ArrayList<>();
        if (primero != null) resultado.add(primero);
        if (segundo != null) resultado.add(segundo);
        if (tercero != null) resultado.add(tercero);
        return resultado;
    }

    /**
     * Consulta las tres misiones con mayor botín.
     * 
     * @return List de objetos Mision que pertenecen al Top 3 de golpes históricos.
     */
    public List<Mision> consultarTop3GolpesHistoricos() {
        List<Mision> misiones = grafoMisiones_.obtenerTodas();
        Mision primero = null;
        Mision segundo = null;
        Mision tercero = null;

        double val1 = -1.0;
        double val2 = -1.0;
        double val3 = -1.0;

        for (int i = 0; i < misiones.size(); i++) {
            Mision act = misiones.get(i);
            double valor = act.getBotin();

            if (valor > val1) {
                tercero = segundo;
                val3 = val2;
                segundo = primero;
                val2 = val1;
                primero = act;
                val1 = valor;
            } else if (valor > val2) {
                tercero = segundo;
                val3 = val2;
                segundo = act;
                val2 = valor;
            } else if (valor > val3) {
                tercero = act;
                val3 = valor;
            }
        }

        List<Mision> resultado = new ArrayList<>();
        if (primero != null) resultado.add(primero);
        if (segundo != null) resultado.add(segundo);
        if (tercero != null) resultado.add(tercero);
        return resultado;
    }

    /**
     * Consulta las tres misiones que acumulan un mayor número de intentos fallidos.
     * 
     * @return List de objetos Mision que pertenecen al Top 3 de puntos calientes.
     */
    public List<Mision> consultarTop3PuntosCalientes() {
        List<Mision> misiones = grafoMisiones_.obtenerTodas();
        Mision primero = null;
        Mision segundo = null;
        Mision tercero = null;

        int val1 = -1;
        int val2 = -1;
        int val3 = -1;

        for (int i = 0; i < misiones.size(); i++) {
            Mision act = misiones.get(i);
            int valor = act.getNumFracasos();

            if (valor > val1) {
                tercero = segundo;
                val3 = val2;
                segundo = primero;
                val2 = val1;
                primero = act;
                val1 = valor;
            } else if (valor > val2) {
                tercero = segundo;
                val3 = val2;
                segundo = act;
                val2 = valor;
            } else if (valor > val3) {
                tercero = act;
                val3 = valor;
            }
        }

        List<Mision> resultado = new ArrayList<>();
        if (primero != null) resultado.add(primero);
        if (segundo != null) resultado.add(segundo);
        if (tercero != null) resultado.add(tercero);
        return resultado;
    }
}