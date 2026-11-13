package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domain.GrafoMisiones;
import domain.Mision;
import domain.MisionRescate;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.EstadoSecuaz;
import domain.enums.TipoRol;
import domain.exceptions.MisionCicloDependenciaException;
import domain.exceptions.MisionNoEncontradaException;
import domain.exceptions.MisionYaExistenteException;

/**
 * CtrlMisiones
 *
 * Controlador encargado de gestionar las misiones de la organizacion.
 * Permite crear, editar, eliminar y consultar misiones, establecer sus
 * dependencias y obtener informacion para planificar su ejecucion.
 *
 * @author Arian Mohammadian
 */
public class CtrlMisiones {
    private final GrafoMisiones grafoMisiones;

    /**
     * Crea un controlador de misiones con un grafo inicialmente vacio.
     */
    public CtrlMisiones() {
        this.grafoMisiones = new GrafoMisiones();
    }

    /**
     * Crea un controlador asociado al grafo de misiones indicado.
     * Si el grafo proporcionado es nulo, crea uno nuevo inicialmente vacio.
     *
     * @param grafoMisiones Grafo de misiones que gestionara el controlador.
     */
    public CtrlMisiones(GrafoMisiones grafoMisiones) {
        this.grafoMisiones = (grafoMisiones != null) ? grafoMisiones : new GrafoMisiones();
    }

    /**
     * Crea una nueva mision y la incorpora al grafo de misiones.
     * Los requisitos se construyen a partir de la plantilla de roles,
     * utilizando unicamente las entradas con rol valido y cantidad positiva.
     *
     * @param id Identificador de la nueva mision.
     * @param botin Cantidad de botin asociada a la mision.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param plantilla Mapa que relaciona cada rol con la cantidad
     *        de secuaces requeridos.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el botin es negativo, la probabilidad esta fuera del intervalo
     *         [0, 100] o no existe al menos un requisito de rol valido.
     * @throws MisionYaExistenteException Si ya existe una mision
     *         con el mismo identificador.
     */
    public void crearMision(String id, double botin, double probFracaso, Map<TipoRol, Integer> plantilla) throws MisionYaExistenteException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la mision no puede estar vacio");
        }
        if (botin < 0 || probFracaso < 0 || probFracaso > 100) {
            throw new IllegalArgumentException("Los datos de la mision no son validos");
        }
        if (plantilla == null || plantilla.isEmpty()) {
            throw new IllegalArgumentException("La mision debe tener al menos un requisito de rol");
        }

        List<RequisitoRol> requisitos = crearListaRequisitos(plantilla);
        if (requisitos.isEmpty()) {
            throw new IllegalArgumentException("La mision debe tener al menos un requisito de rol con cantidad mayor que 0");
        }

        Mision mision = new Mision(id, botin, probFracaso, requisitos);
        grafoMisiones.anadirMision(mision);
    }

    /**
     * Crea una mision de rescate destinada a liberar a un secuaz encarcelado.
     * La mision no tiene botin economico y sus requisitos se obtienen
     * de la plantilla de roles indicada.
     *
     * @param id Identificador de la nueva mision de rescate.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param plantilla Mapa que relaciona cada rol con la cantidad
     *        de secuaces requeridos.
     * @param secuazPrisionero Secuaz encarcelado que se desea rescatar.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el secuaz objetivo es nulo o no esta encarcelado,
     *         la probabilidad no es valida o no existen requisitos validos.
     * @throws MisionYaExistenteException Si ya existe una mision
     *         con el mismo identificador.
     */
    public void crearMisionRescate(String id, double probFracaso, Map<TipoRol, Integer> plantilla, Secuaz secuazPrisionero) throws MisionYaExistenteException {
        if (id == null || id.trim().isEmpty() || secuazPrisionero == null) {
            throw new IllegalArgumentException("El ID de la mision o el secuaz a rescatar no son validos");
        }
        if (secuazPrisionero.getEstado() != EstadoSecuaz.PRISIONERO) {
            throw new IllegalArgumentException("El prisionero no esta preso");
        }
        if (!Double.isFinite(probFracaso) || probFracaso < 0 || probFracaso > 100) {
            throw new IllegalArgumentException("Los datos de la mision no son validos");
        }
        if (plantilla == null || plantilla.isEmpty()) {
            throw new IllegalArgumentException("La mision de rescate debe tener al menos un requisito de rol");
        }

        List<RequisitoRol> requisitos = crearListaRequisitos(plantilla);
        if (requisitos.isEmpty()) {
            throw new IllegalArgumentException("La mision de rescate debe tener al menos un requisito de rol con cantidad mayor que 0");
        }

        MisionRescate misionRescate = new MisionRescate(id, probFracaso, requisitos, secuazPrisionero);
        grafoMisiones.anadirMision(misionRescate);
    }

    /**
     * Modifica el botin y la probabilidad de fracaso de una mision existente.
     *
     * @param id Identificador de la mision que se desea editar.
     * @param botin Nueva cantidad de botin de la mision.
     * @param probFracaso Nueva probabilidad de fracaso, entre 0 y 100.
     * @throws IllegalArgumentException Si el botin es negativo o la
     *         probabilidad esta fuera del intervalo [0, 100].
     * @throws MisionNoEncontradaException Si la mision no existe.
     * @throws UnsupportedOperationException Si se intenta asignar un botin
     *         distinto de cero a una mision de rescate.
     */
    public void editarMision(String id, double botin, double probFracaso) throws MisionNoEncontradaException {
        if (botin < 0 || probFracaso < 0 || probFracaso > 100) {
            throw new IllegalArgumentException("Valores de botín o probabilidad no válidos.");
        }
        Mision mision = grafoMisiones.buscarMision(id);
        mision.setBotin(botin);
        mision.setProbFracaso(probFracaso);
    }

    /**
     * Elimina una mision del grafo junto con sus dependencias.
     *
     * @param id Identificador de la mision que se desea eliminar.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public void eliminarMision(String id) throws MisionNoEncontradaException {
        grafoMisiones.eliminarMision(id);
    }

    /**
     * Consulta una mision mediante su identificador.
     *
     * @param id Identificador de la mision que se desea consultar.
     * @return Mision asociada al identificador indicado.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public Mision consultarMision(String id) throws MisionNoEncontradaException {
        return grafoMisiones.buscarMision(id);
    }

    /**
     * Obtiene todas las misiones registradas en el grafo.
     *
     * @return Nueva lista con las misiones registradas.
     */
    public List<Mision> listarMisiones() {
        return grafoMisiones.obtenerTodas();
    }

    /**
     * Establece una dependencia entre dos misiones.
     * La mision dependiente requiere que se complete previamente
     * la mision identificada como prerrequisito.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador de la mision prerrequisito.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     * @throws MisionCicloDependenciaException Si la dependencia genera un ciclo.
     * @throws IllegalArgumentException Si una mision intenta depender de si misma.
     */
    public void anadirDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException, MisionCicloDependenciaException {
        grafoMisiones.anadirDependencia(idMision, idPrerrequisito);
    }

    /**
     * Elimina una dependencia existente entre dos misiones.
     * Actualiza el estado de la mision dependiente tras la eliminacion.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador del prerrequisito que se desea eliminar.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     */
    public void eliminarDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException {
        grafoMisiones.eliminarDependencia(idMision, idPrerrequisito);
    }

    /**
     * Obtiene una planificacion de las misiones respetando sus dependencias.
     * Entre las misiones cuyos prerrequisitos ya se han procesado,
     * se priorizan aquellas con mayor botin.
     *
     * @return Lista de misiones ordenadas para su ejecucion.
     */
    public List<Mision> planificarGolpes() {
        return grafoMisiones.obtenerOrdenEjecucion();
    }

    /**
     * Consulta todos los prerrequisitos directos e indirectos de una mision.
     *
     * @param idMision Identificador de la mision que se desea consultar.
     * @return Lista de misiones que deben preceder a la mision indicada.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> consultarPrerrequisitos(String idMision) throws MisionNoEncontradaException {
        return grafoMisiones.obtenerPrerrequisitos(idMision);
    }

    /**
     * Consulta las misiones sucesoras directas que podrian quedar disponibles
     * al completar la mision indicada.
     *
     * Solo considera aquellas cuyos demas prerrequisitos ya estan completados.
     * La consulta no modifica el estado de las misiones.
     *
     * @param idMision Identificador de la mision cuyos desbloqueos se consultan.
     * @return Lista de misiones que podrian desbloquearse al completarla.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> consultarDesbloqueosPotenciales(String idMision) throws MisionNoEncontradaException {
        return grafoMisiones.obtenerSucesores(idMision);
    }

    /**
     * Obtiene el grafo de misiones gestionado por el controlador.
     *
     * @return Grafo de misiones asociado al controlador.
     */
    public GrafoMisiones getGrafoMisiones() {
        return this.grafoMisiones;
    }

    private List<RequisitoRol> crearListaRequisitos(Map<TipoRol, Integer> plantilla) {
        List<RequisitoRol> requisitos = new ArrayList<>();
        if (plantilla != null) {
            for (Map.Entry<TipoRol, Integer> entrada : plantilla.entrySet()) {
                TipoRol rol = entrada.getKey();
                Integer cantidad = entrada.getValue();
                if (rol != null && cantidad != null && cantidad > 0) {
                    requisitos.add(new RequisitoRol(rol, cantidad));
                }
            }
        }
        return requisitos;
    }
}
