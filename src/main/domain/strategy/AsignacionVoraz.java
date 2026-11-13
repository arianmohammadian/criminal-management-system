package domain.strategy;

import domain.Mision;
import domain.Secuaz;
import domain.enums.TipoRol;
import domain.RequisitoRol;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

import java.util.ArrayDeque;

import java.util.Comparator;

/**
 * 
 * AsignacionVoraz
 * 
 * Implementa una estrategia voraz para la asignacion automatica de secuaces
 * a misiones. 
 * 
 * Ordena las misiones ejecutables por su beneficio potencial por secuaz requerido 
 * y construye equipos priorizando a los especialistas en sus roles.
 * 
 * @author Arian Mohammadian
 */
public class AsignacionVoraz implements EstrategiaAsignacion{

    /**
     * Crea una instancia de Asignacion Voraz
     */
    public AsignacionVoraz() {}
    
    /**
     * Selecciona los equipos de secuaces para las misiones ejecutables
     * mediante una estrategia voraz.
     *
     * Las misiones se procesan en orden descendente de beneficio esperado
     * por secuaz requerido. Para cada mision, se asignan primero los secuaces
     * libres especializados en los roles necesarios y, si no son suficientes,
     * se utilizan secuaces de otros roles como sustitutos.
     *
     * Cada secuaz puede formar parte de un unico equipo y las misiones
     * para las que no hay suficientes candidatos disponibles se descartan.
     *
     * @param misiones Lista de misiones que se desean considerar.
     * @param candidatos Lista de secuaces disponibles para la seleccion,
     *        de los cuales solo se tienen en cuenta los que estan libres.
     * @return Mapa que asocia cada mision seleccionada con su equipo de
     *         secuaces y el rol que desempena cada uno en dicha mision.
     */
    @Override
    public Map<Mision, Map<Secuaz, TipoRol>> seleccionarEquipos(
        List<Mision> misiones, 
        List<Secuaz> candidatos
    ) {
        Map<Mision, Map<Secuaz, TipoRol>> seleccion = new HashMap<>();

        Map<TipoRol, ArrayDeque<Secuaz>> disponiblesPorRol = new HashMap<>();

        int totalDisponibles = 0;

        for (Secuaz secuaz : candidatos) {
            if (!secuaz.estaLibre()) continue;

            TipoRol rol = secuaz.getRolBase();

            if (!disponiblesPorRol.containsKey(rol)) {
                disponiblesPorRol.put(rol, new ArrayDeque<>());
            }

            disponiblesPorRol.get(rol).addLast(secuaz);

            ++totalDisponibles;
        }

        List<Mision> ordenadas = new ArrayList<>();

        for(Mision mision : misiones) {
            if(mision.esEjecutable()) ordenadas.add(mision);
        }

        ordenadas.sort(new Comparator<Mision>() {
            @Override
            public int compare(Mision a, Mision b) {
                return Double.compare(
                    calcularPrioridad(b),
                    calcularPrioridad(a)
                );
            }
        });

        for (Mision mision : ordenadas) {
            ResultadoEquipo res = construirEquipo(
                mision, disponiblesPorRol, totalDisponibles
            );

            if (res != null) {
                seleccion.put(mision, res.equipo_);
                totalDisponibles -= res.equipo_.size();
            }
        }

        return seleccion;
    }

    private double calcularPrioridad(Mision mision) {
        List<RequisitoRol> requisitos = mision.getRequisitos();
        double N = 0.0;

        for (RequisitoRol req : requisitos) {
            N += req.getCantidadRequerida();
        }

        if(N == 0.0) return 0.0;

        double k = mision.getProbFracaso();
        double B = mision.getBotin();

        return B * (1.0 - k / 100.0) / N;
    }

    private int myMin(int x, int y) {
        return (x <= y ? x : y);
    }

    private static class ResultadoEquipo {
        Map<Secuaz, TipoRol> equipo_;

        ResultadoEquipo(Map<Secuaz, TipoRol> equipo) {
            equipo_ = equipo;
        }
    }

    private ResultadoEquipo construirEquipo(
        Mision mision,
        Map<TipoRol, ArrayDeque<Secuaz>> disponiblesPorRol,
        int totalDisponibles
    ) { 
        List<RequisitoRol> requisitos = mision.getRequisitos();

        int totalNecesarios = 0;

        // calcular total de secuaces necesarios
        for (RequisitoRol req : requisitos) {
            totalNecesarios += req.getCantidadRequerida();
        }

        // comprobar si tenemos suficientes secuaces disponibles
        if (totalDisponibles < totalNecesarios) {
            return null;
        }

        Map<Secuaz, TipoRol> equipo = new HashMap<>();

        Map<TipoRol, Integer> pendientes = new HashMap<>();

        // asignar secuaces especialistas
        for (RequisitoRol requisito : requisitos) {
            TipoRol rol = requisito.getTipoRol();
            int necesarios = requisito.getCantidadRequerida();

            ArrayDeque<Secuaz> cola = disponiblesPorRol.get(rol);

            int asignables = 0;

            if (cola != null) {
                asignables = myMin(necesarios, cola.size());
            }

            for (int i = 0; i < asignables; i++) {
                Secuaz secuaz = cola.removeFirst();
                equipo.put(secuaz, rol);
            }

            int pendientesRol = necesarios - asignables;

            if (pendientesRol > 0) {
                pendientes.put(rol, pendientesRol);
            }
        }

        // cubrir los puestos pendientes mediante sustituciones
        for (Map.Entry<TipoRol, Integer> entry : pendientes.entrySet()) {
            TipoRol rol = entry.getKey();
            int cantidad = entry.getValue();

            for (TipoRol tipo : TipoRol.values()) {
                if (cantidad == 0) break;

                ArrayDeque<Secuaz> cola = disponiblesPorRol.get(tipo);

                if (cola == null) continue;

                while (cantidad > 0 && !cola.isEmpty()) {
                    Secuaz secuaz = cola.removeFirst();
                    equipo.put(secuaz, rol);
                    --cantidad;
                }
            }
        }

        return new ResultadoEquipo(equipo);
    }
}