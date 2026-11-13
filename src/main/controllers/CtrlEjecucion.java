package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import domain.AsignacionRol;
import domain.ConjuntoSecuaces;
import domain.EjecucionMision;
import domain.GrafoMisiones;
import domain.HistorialEjecuciones;
import domain.Mision;
import domain.MisionRescate;
import domain.Organizacion;
import domain.Prision;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.TipoRol;
import domain.exceptions.MisionNoEncontradaException;
import domain.strategy.EstrategiaAsignacion;

/**
 * CtrlEjecucion
 *
 * Controlador encargado de coordinar la ejecucion manual y automatica
 * de las misiones de la organizacion.
 *
 * Gestiona la asignacion y disponibilidad de los secuaces, registra
 * las ejecuciones en el historial y procesa sus resultados, incluyendo
 * el reparto del botin, los rescates y los posibles encarcelamientos.
 *
 * @author Arian Mohammadian
 */
public class CtrlEjecucion {
    private final HistorialEjecuciones historialEjecuciones_;
    private EstrategiaAsignacion estrategiaAsignacion_;
    private final Random random_;

    // CONSTRUCTORES

    /**
     * Crea un controlador de ejecuciones con un historial inicialmente
     * vacio y un generador aleatorio propio.
     *
     * La estrategia de asignacion queda inicialmente sin definir.
     */
    public CtrlEjecucion() {
        historialEjecuciones_ = new HistorialEjecuciones();
        estrategiaAsignacion_ = null;
        random_ = new Random();
    }

    /**
     * Crea un controlador asociado a un historial y una estrategia
     * de asignacion, utilizando un generador aleatorio propio.
     *
     * @param historialEjecuciones Historial donde se registraran las ejecuciones.
     * @param estrategiaAsignacion Estrategia utilizada para asignar equipos.
     * @throws IllegalArgumentException Si el historial de ejecuciones es nulo.
     */
    public CtrlEjecucion(HistorialEjecuciones historialEjecuciones, EstrategiaAsignacion estrategiaAsignacion) {
        this(historialEjecuciones, estrategiaAsignacion, new Random());
    }

    /**
     * Crea un controlador con el historial, la estrategia de asignacion
     * y el generador aleatorio indicados.
     *
     * Permite proporcionar un generador aleatorio externo para controlar
     * la reproducibilidad de los resultados de las misiones.
     *
     * @param historialEjecuciones Historial donde se registraran las ejecuciones.
     * @param estrategiaAsignacion Estrategia utilizada para asignar equipos.
     * @param random Generador aleatorio utilizado para determinar resultados.
     * @throws IllegalArgumentException Si el historial de ejecuciones
     *         o el generador aleatorio son nulos.
     */
    public CtrlEjecucion(HistorialEjecuciones historialEjecuciones, EstrategiaAsignacion estrategiaAsignacion, Random random) {
        if (historialEjecuciones == null) {
            throw new IllegalArgumentException(
                "El historial de ejecuciones no puede ser nulo"
            );
        }
        if (random == null) {
            throw new IllegalArgumentException(
                "El generador aleatorio no puede ser nulo"
            );
        }

        historialEjecuciones_ = historialEjecuciones;
        estrategiaAsignacion_ = estrategiaAsignacion;
        random_ = random;
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene el historial de ejecuciones gestionado por el controlador.
     *
     * @return Historial de ejecuciones asociado al controlador.
     */
    public HistorialEjecuciones getHistorialEjecuciones() {
        return historialEjecuciones_;
    }

    /**
     * Obtiene la estrategia de asignacion configurada actualmente.
     *
     * @return Estrategia de asignacion actual, o null si no se ha definido.
     */
    public EstrategiaAsignacion getEstrategiaAsignacion() {
        return estrategiaAsignacion_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Establece o modifica la estrategia de asignacion automatica de equipos.
     *
     * @param e Nueva estrategia de asignacion que se desea utilizar.
     * @throws IllegalArgumentException Si la estrategia indicada es nula.
     */
    public void cambiarEstrategiaAsignacion(EstrategiaAsignacion e) {
        if (e == null) {
            throw new IllegalArgumentException(
                "La estrategia de asignación que se quiere registrar no puede ser nula"
            );
        }

        estrategiaAsignacion_ = e;
    }

    /**
     * Inicia manualmente la ejecucion de una mision con las asignaciones
     * de secuaces y roles indicadas.
     *
     * Comprueba que la mision sea ejecutable, que no tenga una ejecucion
     * activa y que el equipo cumpla los requisitos de roles establecidos.
     * Tambien verifica que todos los secuaces esten disponibles.
     *
     * Si se trata de una mision de rescate y se proporciona una prision,
     * actualiza su probabilidad de fracaso segun la dificultad de rescate.
     *
     * Finalmente, marca los secuaces como ocupados y registra una nueva
     * ejecucion pendiente de finalizar en el historial.
     *
     * @param idMision Identificador de la mision que se desea ejecutar.
     * @param asignaciones Mapa que relaciona los identificadores de los
     *        secuaces con los roles que desempenaran en la mision.
     * @param grafo Grafo que contiene la mision.
     * @param secuaces Coleccion de secuaces de la organizacion.
     * @param prision Prision utilizada para calcular la dificultad
     *        de las misiones de rescate; puede ser nula.
     * @throws MisionNoEncontradaException Si la mision no existe.
     * @throws IllegalArgumentException Si los parametros obligatorios
     *         no son validos o el equipo no cumple los requisitos de la mision.
     * @throws IllegalStateException Si la mision no es ejecutable,
     *         ya tiene una ejecucion activa o algun secuaz no esta disponible.
     * @throws java.util.NoSuchElementException Si alguno de los secuaces
     *         indicados no existe en la coleccion.
     */
    public void ejecutarMisionManual(String idMision, Map<String, TipoRol> asignaciones, GrafoMisiones grafo, ConjuntoSecuaces secuaces, Prision prision) throws MisionNoEncontradaException {
        if (idMision == null || idMision.isBlank()) {
            throw new IllegalArgumentException(
                "El ID de la misión no puede ser nulo o estar vacío"
            );
        }

        if (asignaciones == null || asignaciones.isEmpty()) {
            throw new IllegalArgumentException(
                "Las asignaciones no pueden ser nulas o estar vacías"
            );
        }

        if (grafo == null) {
            throw new IllegalArgumentException(
                "El grafo no puede ser nulo"
            );
        }

        if (secuaces == null) {
            throw new IllegalArgumentException(
                "El conjunto de secuaces no puede ser nulo"
            );
        }

        Mision misionObjetivo = grafo.buscarMision(idMision);

        if (!misionObjetivo.esEjecutable()) {
            throw new IllegalStateException(
                "La misión con ID " + idMision + " no se puede ejecutar"
            );
        }

        if (tieneEjecucionActiva(misionObjetivo)) {
            throw new IllegalStateException(
                "La misión con el ID " + idMision + " ya tiene una ejecución"
            );
        }

        validarRequisitosEquipo(misionObjetivo, asignaciones);

        List<AsignacionRol> equipoAsignado = new ArrayList<>();
        for (Map.Entry<String, TipoRol> entry : asignaciones.entrySet()) {
            String idSecuaz = entry.getKey();
            TipoRol rol = entry.getValue();

            Secuaz s = secuaces.consultarSecuaz(idSecuaz);
            if (s == null) {
                throw new IllegalArgumentException(
                    "No existe el secuaz con el ID " + idSecuaz
                );
            }

            if (!s.estaLibre()) {
                throw new IllegalStateException(
                    "El secuaz con ID " + idSecuaz + " no está disponible para realizar la misión"
                );
            }

            equipoAsignado.add(new AsignacionRol(rol, s));
        }

        if (misionObjetivo instanceof MisionRescate && prision != null) {
            misionObjetivo.setProbFracaso(prision.calcularDificultadRescate());
        }

        String idEjecucion = String.valueOf(historialEjecuciones_.obtenerHistorial().size() + 1);
        EjecucionMision ejecucion = new EjecucionMision(idEjecucion, misionObjetivo, equipoAsignado);

        for (AsignacionRol asig : equipoAsignado) {
            asig.getSecuaz().asignarAMision();
        }

        historialEjecuciones_.registrarEjecucion(ejecucion);
    }

    /**
     * Inicia automaticamente las ejecuciones de las misiones seleccionadas
     * mediante la estrategia de asignacion configurada.
     *
     * Considera las misiones disponibles que no tienen una ejecucion activa
     * y obtiene los equipos propuestos por la estrategia.
     *
     * Para cada equipo propuesto, comprueba los requisitos de roles
     * y la disponibilidad de los secuaces. Los equipos validos se registran
     * como nuevas ejecuciones y sus participantes pasan a estar ocupados.
     *
     * Si no hay misiones disponibles o la estrategia no selecciona
     * ningun equipo, no se registra ninguna ejecucion.
     *
     * @param grafo Grafo que contiene las misiones disponibles.
     * @param secuaces Coleccion de secuaces candidatos para las misiones.
     * @param prision Prision utilizada para calcular la dificultad
     *        de las misiones de rescate; puede ser nula.
     * @throws IllegalArgumentException Si el grafo o la coleccion
     *         de secuaces son nulos.
     * @throws IllegalStateException Si no se ha configurado una
     *         estrategia de asignacion.
     */
    public void ejecutarMisionAutomatica(GrafoMisiones grafo, ConjuntoSecuaces secuaces, Prision prision) {
        if (estrategiaAsignacion_ == null) {
            throw new IllegalStateException(
                "La estrategia de asignación no puede ser nula, se tiene que definir"
            );
        }

        if (grafo == null) {
            throw new IllegalArgumentException(
                "El grafo no puede ser nulo"
            );
        }

        if (secuaces == null) {
            throw new IllegalArgumentException(
                "El conjunto de secuaces no puede ser nulo"
            );
        }

        List<Mision> misiones = new ArrayList<>();
        for (Mision m : grafo.obtenerMisionesDisponibles()) {
            if (!tieneEjecucionActiva(m)) {
                if (m instanceof MisionRescate && prision != null) {
                    m.setProbFracaso(prision.calcularDificultadRescate());
                }
                misiones.add(m);
            }
        }

        if (misiones.isEmpty()) return;

        List<Secuaz> secuacesList = secuaces.listarTodos();
        Map<Mision, Map<Secuaz, TipoRol>> seleccion = estrategiaAsignacion_.seleccionarEquipos(misiones, secuacesList);
        if (seleccion == null || seleccion.isEmpty()) return;

        for (Map.Entry<Mision, Map<Secuaz, TipoRol>> entry : seleccion.entrySet()) {
            Mision mision = entry.getKey();
            Map<Secuaz, TipoRol> equipoMap = entry.getValue();

            if (mision == null || !mision.esEjecutable() || tieneEjecucionActiva(mision)
                    || equipoMap == null || equipoMap.isEmpty()) {
                continue;
            }

            if (!cumpleRequisitosEquipo(mision, equipoMap.values())) {
                continue;
            }

            List<AsignacionRol> equipoAsignado = new ArrayList<>();
            boolean validTeam = true;

            for (Map.Entry<Secuaz, TipoRol> asig : equipoMap.entrySet()) {
                Secuaz s = asig.getKey();
                TipoRol rol = asig.getValue();

                if (s == null || rol == null || !s.estaLibre()) {
                    validTeam = false;
                    break;
                }
                equipoAsignado.add(new AsignacionRol(rol, s));
            }

            if (validTeam && !equipoAsignado.isEmpty()) {
                String idEjecucion = String.valueOf(historialEjecuciones_.obtenerHistorial().size() + 1);
                EjecucionMision ejecucion = new EjecucionMision(idEjecucion, mision, equipoAsignado);

                for (AsignacionRol asig : equipoAsignado) {
                    asig.getSecuaz().asignarAMision();
                }

                historialEjecuciones_.registrarEjecucion(ejecucion);
            }
        }
    }

    /**
     * Determina y registra el resultado de una ejecucion pendiente de finalizar.
     *
     * Utiliza la probabilidad de exito calculada para determinar aleatoriamente
     * si la mision se completa o fracasa.
     *
     * En caso de exito, registra el botin obtenido, incorpora la comision
     * del Don a los fondos de la organizacion y evalua los desbloqueos
     * de otras misiones. Si se trata de una mision de rescate, tambien
     * libera al secuaz objetivo de la prision cuando corresponda.
     *
     * En caso de fracaso, puede encarcelar a los participantes
     * segun la probabilidad de fracaso de la mision, si se proporciona
     * una prision.
     *
     * @param idEjecucion Identificador de la ejecucion que se desea finalizar.
     * @param grafo Grafo utilizado para actualizar las dependencias
     *        de las misiones completadas.
     * @param org Organizacion que recibe la comision del Don.
     * @param prision Prision utilizada para gestionar rescates y
     *        posibles encarcelamientos; puede ser nula.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el grafo o la organizacion son nulos, o la ejecucion no existe.
     * @throws IllegalStateException Si la ejecucion ya esta finalizada
     *         o se produce una transicion de estado no permitida.
     */
    public void registrarResultadoMision(String idEjecucion, GrafoMisiones grafo, Organizacion org, Prision prision) {
        if (idEjecucion == null || idEjecucion.isBlank()) {
            throw new IllegalArgumentException(
                "El ID de la ejecución que se busca no puede ser nulo o estar vacío"
            );
        }

        if (grafo == null) {
            throw new IllegalArgumentException(
                "El grafo no puede ser nulo"
            );
        }

        if (org == null) {
            throw new IllegalArgumentException(
                "La organización no puede ser nula"
            );
        }

        EjecucionMision ejec = historialEjecuciones_.buscarEjecucion(idEjecucion);

        if (ejec == null) {
            throw new IllegalArgumentException(
                "La ejecución con el ID " + idEjecucion + " no existe"
            );
        }

        if (ejec.isFinalizada()) {
            throw new IllegalStateException(
                "La ejecución con ID " + idEjecucion + " ya está finalizada y registrada"
            );
        }

        boolean exito = random_.nextDouble() < ejec.getProbExitoCalculada();
        double botinObtenido = exito ? ejec.getMision().getBotin() : 0.0;

        ejec.finalizarEjecucion(exito, botinObtenido, org.getComision());

        if (exito) {
            double parteDon = ejec.calcularRepartoBotin(org.getComision());
            org.añadirFondos(parteDon);

            Mision completada = ejec.getMision();
            grafo.evaluarDesbloqueos(completada);

            if (completada instanceof MisionRescate && prision != null) {
                Secuaz objetivo = ((MisionRescate) completada).getSecuazObjetivo();
                if (objetivo != null && prision.obtenerPrisioneros().contains(objetivo)) {
                    prision.liberar(objetivo.getId());
                }
            }
        } else if (prision != null) {
            Mision fallida = ejec.getMision();
            double probTalego = (fallida.getProbFracaso() / 2.0) / 100.0;

            for (AsignacionRol asig : ejec.getEquipo()) {
                Secuaz secuaz = asig.getSecuaz();
                if (secuaz != null && random_.nextDouble() < probTalego) {
                    prision.encarcelar(secuaz);
                }
            }
        }
    }

    private boolean tieneEjecucionActiva(Mision mision) {
        for (EjecucionMision ejec : historialEjecuciones_.obtenerHistorial()) {
            if (!ejec.isFinalizada() && ejec.getMision() != null && ejec.getMision().getId().equals(mision.getId())) {
                return true;
            }
        }
        return false;
    }

    private void validarRequisitosEquipo(Mision mision, Map<String, TipoRol> asignaciones) {
        for (TipoRol rol : asignaciones.values()) {
            if (rol == null) {
                throw new IllegalArgumentException("El rol asignado no puede ser nulo");
            }
        }

        if (!cumpleRequisitosEquipo(mision, asignaciones.values())) {
            throw new IllegalArgumentException(
                "El equipo asignado no cumple con la plantilla de roles requerida por la misión " + mision.getId()
            );
        }
    }

    private boolean cumpleRequisitosEquipo(Mision mision, Collection<TipoRol> rolesAsignados) {
        Map<TipoRol, Integer> requeridos = new EnumMap<>(TipoRol.class);
        for (RequisitoRol req : mision.getRequisitos()) {
            requeridos.merge(req.getTipoRol(), req.getCantidadRequerida(), Integer::sum);
        }

        Map<TipoRol, Integer> proporcionados = new EnumMap<>(TipoRol.class);
        for (TipoRol rol : rolesAsignados) {
            if (rol == null) {
                return false;
            }
            proporcionados.merge(rol, 1, Integer::sum);
        }

        return requeridos.equals(proporcionados);
    }

    // ----------
}
