package controllers;

import java.util.List;
import java.util.Map;

import domain.ConjuntoSecuaces;
import domain.GrafoMisiones;
import domain.HistorialEjecuciones;
import domain.Mision;
import domain.Organizacion;
import domain.Secuaz;
import domain.Prision;
import domain.enums.TipoRol;
import domain.exceptions.MisionCicloDependenciaException;
import domain.exceptions.MisionNoEncontradaException;
import domain.exceptions.MisionYaExistenteException;
import domain.strategy.EstrategiaAsignacion;

/**
 * CtrlDominio
 *
 * Controlador principal del dominio de la aplicacion.
 * Coordina los controladores responsables de las misiones, los secuaces,
 * las ejecuciones, las estadisticas y la persistencia de datos.
 *
 * Proporciona una interfaz centralizada para gestionar las operaciones
 * de la organizacion criminal y sus entidades asociadas.
 *
 * @author Arian Mohammadian
 */
public class CtrlDominio {
    private CtrlMisiones ctrlMisiones_;
    private CtrlSecuaces ctrlSecuaces_;
    private CtrlEjecucion ctrlEjecucion_;
    private CtrlEstadisticas ctrlEstadisticas_;
    private CtrlPersistencia ctrlPersistencia_;

    private Organizacion organizacion_;
    private Prision prision_;
    private HistorialEjecuciones historialEjecuciones_;
    private ConjuntoSecuaces conjuntoSecuaces_;
    private GrafoMisiones grafoMisiones_;

    // CONSTRUCTOR

    /**
     * Crea el controlador principal del dominio e inicializa los
     * componentes necesarios para gestionar la organizacion.
     *
     * Crea una organizacion, una prision, una coleccion de secuaces,
     * un grafo de misiones y un historial de ejecuciones.
     * Tambien inicializa los controladores especializados y obtiene
     * la instancia del controlador de persistencia.
     */
    public CtrlDominio() {
        organizacion_ = new Organizacion();
        prision_ = new Prision();
        conjuntoSecuaces_= new ConjuntoSecuaces();
        grafoMisiones_ = new GrafoMisiones();
        historialEjecuciones_ = new HistorialEjecuciones();

        ctrlPersistencia_ = CtrlPersistencia.getInstance();
        ctrlMisiones_ = new CtrlMisiones(grafoMisiones_);
        ctrlEjecucion_ = new CtrlEjecucion(historialEjecuciones_, null);
        ctrlEstadisticas_ = new CtrlEstadisticas(conjuntoSecuaces_, grafoMisiones_, historialEjecuciones_);
        ctrlSecuaces_ = new CtrlSecuaces(conjuntoSecuaces_);
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene el controlador encargado de gestionar las misiones.
     *
     * @return Controlador de misiones.
     */
    public CtrlMisiones getCtrlMisiones() {
        return ctrlMisiones_;
    }

    /**
     * Obtiene el controlador encargado de gestionar los secuaces.
     *
     * @return Controlador de secuaces.
     */
    public CtrlSecuaces getCtrlSecuaces() {
        return ctrlSecuaces_;
    }

    /**
     * Obtiene el controlador encargado de gestionar las ejecuciones.
     *
     * @return Controlador de ejecuciones de misiones.
     */
    public CtrlEjecucion getCtrlEjecucion() {
        return ctrlEjecucion_;
    }

    /**
     * Obtiene el controlador encargado de las consultas estadisticas.
     *
     * @return Controlador de estadisticas.
     */
    public CtrlEstadisticas getCtrlEstadisticas() {
        return ctrlEstadisticas_;
    }

    /**
     * Obtiene el controlador encargado de la persistencia de datos.
     *
     * @return Controlador de persistencia.
     */
    public CtrlPersistencia getCtrlPersistencia() {
        return ctrlPersistencia_;
    }

    /**
     * Obtiene la organizacion gestionada por el dominio.
     *
     * @return Organizacion asociada al controlador.
     */
    public Organizacion getOrganizacion() {
        return organizacion_;
    }

    /**
     * Obtiene la prision asociada a la organizacion.
     *
     * @return Prision gestionada por el dominio.
     */
    public Prision getPrision() {
        return prision_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Carga los datos de la aplicacion desde un fichero.
     *
     * Permite recuperar la informacion almacenada de la organizacion
     * y de sus entidades para restaurar el estado del sistema.
     *
     * @param path Ruta del fichero del que se desean cargar los datos.
     */
    public void cargarDatosFichero(String path) {
    }

    /**
     * Exporta los datos de la aplicacion a un fichero.
     *
     * Permite almacenar la informacion de la organizacion y de sus
     * entidades para conservar el estado del sistema.
     *
     * @param path Ruta del fichero en el que se desean guardar los datos.
     */
    public void exportarDatosFichero(String path) {
    }

    /**
     * Crea una nueva mision y la incorpora al grafo de misiones.
     *
     * @param id Identificador de la nueva mision.
     * @param botin Cantidad de botin asociada a la mision.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param plantilla Mapa que relaciona los roles necesarios con
     *        sus cantidades requeridas.
     * @throws IllegalArgumentException Si los datos de la mision
     *         no son validos o no existen requisitos de rol validos.
     * @throws MisionYaExistenteException Si ya existe una mision
     *         con el mismo identificador.
     */
    public void crearMision(String id, double botin, double probFracaso, Map<TipoRol, Integer> plantilla) throws MisionYaExistenteException {
        ctrlMisiones_.crearMision(id, botin, probFracaso, plantilla);
    }

    /**
     * Crea una mision de rescate destinada a liberar a un secuaz encarcelado.
     *
     * Consulta el secuaz objetivo y delega la creacion y el registro
     * de la mision de rescate en el controlador de misiones.
     *
     * @param id Identificador de la nueva mision de rescate.
     * @param botin Parametro de botin recibido por la operacion.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param plantilla Mapa de roles y cantidades requeridas para el rescate.
     * @param idSecuazPrisionero Identificador del secuaz que se desea rescatar.
     * @throws IllegalArgumentException Si los datos de la mision
     *         no son validos o el secuaz objetivo no esta encarcelado.
     * @throws java.util.NoSuchElementException Si el secuaz objetivo no existe.
     * @throws MisionYaExistenteException Si ya existe una mision
     *         con el mismo identificador.
     */
    public void crearMisionRescate(String id, double botin, double probFracaso, Map<TipoRol, Integer> plantilla, String idSecuazPrisionero) throws MisionYaExistenteException {
        Secuaz prisionero = ctrlSecuaces_.consultarSecuaz(idSecuazPrisionero);
        ctrlMisiones_.crearMisionRescate(id, probFracaso, plantilla, prisionero);
    }

    /**
     * Modifica el botin y la probabilidad de fracaso de una mision existente.
     *
     * @param id Identificador de la mision que se desea editar.
     * @param botin Nueva cantidad de botin de la mision.
     * @param probFracaso Nueva probabilidad de fracaso, entre 0 y 100.
     * @throws IllegalArgumentException Si los valores indicados no son validos.
     * @throws MisionNoEncontradaException Si la mision no existe.
     * @throws UnsupportedOperationException Si se intenta asignar un botin
     *         distinto de cero a una mision de rescate.
     */
    public void editarMision(String id, double botin, double probFracaso) throws MisionNoEncontradaException {
        ctrlMisiones_.editarMision(id, botin, probFracaso);
    }

    /**
     * Elimina una mision del grafo junto con sus dependencias.
     *
     * @param id Identificador de la mision que se desea eliminar.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public void eliminarMision(String id) throws MisionNoEncontradaException {
        ctrlMisiones_.eliminarMision(id);
    }

    /**
     * Consulta una mision mediante su identificador.
     *
     * @param id Identificador de la mision que se desea consultar.
     * @return Mision asociada al identificador indicado.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public Mision consultarMision(String id) throws MisionNoEncontradaException {
        return ctrlMisiones_.consultarMision(id);
    }

    /**
     * Obtiene todas las misiones registradas en el sistema.
     *
     * @return Nueva lista con las misiones registradas.
     */
    public List<Mision> listarMisiones() {
        return ctrlMisiones_.listarMisiones();
    }

    /**
     * Establece una dependencia entre dos misiones.
     *
     * La mision dependiente necesitara que se complete previamente
     * la mision indicada como prerrequisito.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador de la mision prerrequisito.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     * @throws MisionCicloDependenciaException Si la dependencia genera un ciclo.
     * @throws IllegalArgumentException Si una mision intenta depender de si misma.
     */
    public void anadirDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException, MisionCicloDependenciaException {
        ctrlMisiones_.anadirDependencia(idMision, idPrerrequisito);
    }

    /**
     * Elimina una dependencia entre dos misiones.
     *
     * Actualiza la disponibilidad de la mision dependiente
     * en funcion de los prerrequisitos restantes.
     *
     * @param idMision Identificador de la mision dependiente.
     * @param idPrerrequisito Identificador del prerrequisito que se desea eliminar.
     * @throws MisionNoEncontradaException Si alguna de las misiones no existe.
     */
    public void eliminarDependencia(String idMision, String idPrerrequisito) throws MisionNoEncontradaException {
        ctrlMisiones_.eliminarDependencia(idMision, idPrerrequisito);
    }

    /**
     * Obtiene una planificacion de las misiones respetando sus dependencias.
     *
     * Entre las misiones cuyos prerrequisitos ya se han procesado,
     * se priorizan aquellas que tienen mayor botin.
     *
     * @return Lista de misiones ordenadas para su ejecucion.
     */
    public List<Mision> planificarGolpes() {
        return ctrlMisiones_.planificarGolpes();
    }

    /**
     * Consulta todos los prerrequisitos directos e indirectos de una mision.
     *
     * @param idMision Identificador de la mision que se desea consultar.
     * @return Lista con los prerrequisitos de la mision.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> consultarPrerrequisitos(String idMision) throws MisionNoEncontradaException {
        return ctrlMisiones_.consultarPrerrequisitos(idMision);
    }

    /**
     * Consulta las misiones que podrian desbloquearse al completar
     * una determinada mision.
     *
     * Considera los sucesores directos cuyos demas prerrequisitos
     * ya se encuentran completados.
     *
     * @param idMision Identificador de la mision que se desea consultar.
     * @return Lista de misiones que podrian quedar disponibles.
     * @throws MisionNoEncontradaException Si la mision no existe.
     */
    public List<Mision> consultarDesbloqueosPotenciales(String idMision) throws MisionNoEncontradaException {
        return ctrlMisiones_.consultarDesbloqueosPotenciales(idMision);
    }

    // --- GESTIÓN DE SECUACES ---

    /**
     * Crea un nuevo secuaz y lo registra en la organizacion.
     *
     * @param id Identificador del nuevo secuaz.
     * @param rolBase Rol principal del secuaz.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el rol es nulo o ya existe un secuaz con el mismo identificador.
     */
    public void crearSecuaz(String id, TipoRol rolBase) {
        ctrlSecuaces_.crearSecuaz(id, rolBase);
    }

    /**
     * Modifica el rol principal de un secuaz existente.
     *
     * @param id Identificador del secuaz que se desea editar.
     * @param nuevoRolBase Nuevo rol principal del secuaz.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     * @throws IllegalArgumentException Si el nuevo rol es nulo.
     */
    public void editarSecuaz(String id, TipoRol nuevoRolBase) {
        ctrlSecuaces_.editarSecuaz(id, nuevoRolBase);
    }

    /**
     * Elimina un secuaz de la organizacion.
     * Solo permite eliminar secuaces que se encuentran libres.
     *
     * @param id Identificador del secuaz que se desea eliminar.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     * @throws IllegalStateException Si el secuaz esta ocupado o encarcelado.
     */
    public void eliminarSecuaz(String id) {
        ctrlSecuaces_.eliminarSecuaz(id);
    }

    /**
     * Consulta un secuaz mediante su identificador.
     *
     * @param id Identificador del secuaz que se desea consultar.
     * @return Secuaz asociado al identificador indicado.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     */
    public Secuaz consultarSecuaz(String id) {
        return ctrlSecuaces_.consultarSecuaz(id);
    }

    /**
     * Obtiene todos los secuaces registrados en la organizacion.
     *
     * @return Nueva lista con los secuaces registrados.
     */
    public List<Secuaz> listarSecuaces() {
        return ctrlSecuaces_.listarTodos();
    }

    /**
     * Consulta la cantidad total de botin acumulada por un secuaz.
     *
     * @param idSecuaz Identificador del secuaz que se desea consultar.
     * @return Cantidad de botin acumulada por el secuaz.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     */
    public double consultarEstadoCuentas(String idSecuaz) {
        return ctrlSecuaces_.consultarEstadoCuentas(idSecuaz);
    }

    /**
     * Identifica el complice habitual de un secuaz a partir
     * del historial de ejecuciones.
     *
     * Se considera complice habitual al secuaz con el que ha coincidido
     * en un mayor numero de ejecuciones registradas.
     *
     * @param idSecuaz Identificador del secuaz que se desea consultar.
     * @return Secuaz identificado como complice habitual, o null si
     *         no se encuentra ninguno.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     */
    public Secuaz consultarCompliceHabitual(String idSecuaz) {
        return ctrlSecuaces_.consultarCompliceHabitual(idSecuaz, historialEjecuciones_);
    }

    /**
     * Consulta la hoja de servicios de un secuaz.
     *
     * Cuenta las ejecuciones registradas en las que ha participado
     * el secuaz, independientemente de su resultado.
     *
     * @param idSecuaz Identificador del secuaz que se desea consultar.
     * @return Numero total de ejecuciones en las que ha participado.
     * @throws java.util.NoSuchElementException Si el secuaz no existe.
     */
    public int consultarHojaServicios(String idSecuaz) {
        return ctrlSecuaces_.consultarHojaServicios(idSecuaz, historialEjecuciones_);
    }

    /**
     * Intenta liberar a un secuaz encarcelado mediante el pago de una fianza.
     *
     * El coste depende de la seguridad de la prision y de su estado
     * de alerta. El importe se descuenta de los fondos de la organizacion.
     *
     * @param idSecuaz Identificador del secuaz que se desea liberar.
     * @return true si se paga la fianza y se libera al secuaz;
     *         false si no hay fondos suficientes.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     * @throws java.util.NoSuchElementException Si el secuaz no esta en prision.
     */
    public boolean pagarFianzaSecuaz(String idSecuaz) {
        return prision_.pagarFianza(idSecuaz, organizacion_);
    }

    /**
     * Intenta reducir el nivel de seguridad de la prision mediante un soborno.
     *
     * El importe del soborno se descuenta de los fondos de la organizacion.
     * La reduccion depende de la cantidad pagada y de la fianza base.
     *
     * @param monto Cantidad de dinero destinada al soborno.
     * @throws IllegalArgumentException Si el monto no es positivo.
     * @throws IllegalStateException Si la seguridad ya esta en su nivel minimo
     *         o la organizacion no dispone de fondos suficientes.
     */
    public void sobornarGuardiasPrision(double monto) {
        prision_.sobornarGuardias(monto, organizacion_);
    }

    // --- GESTIÓN DE EJECUCIÓN ---

    /**
     * Establece la estrategia utilizada para la asignacion automatica
     * de secuaces a misiones.
     *
     * @param e Nueva estrategia de asignacion.
     * @throws IllegalArgumentException Si la estrategia es nula.
     */
    public void cambiarEstrategiaAsignacion(EstrategiaAsignacion e) {
        ctrlEjecucion_.cambiarEstrategiaAsignacion(e);
    }

    /**
     * Inicia manualmente una ejecucion de mision con el equipo indicado.
     *
     * Comprueba que la mision sea ejecutable, que no tenga otra ejecucion
     * activa y que los secuaces seleccionados esten disponibles y cumplan
     * los requisitos de roles establecidos.
     *
     * Una vez validada la asignacion, registra la ejecucion en el historial
     * y marca los secuaces participantes como ocupados.
     *
     * @param idMision Identificador de la mision que se desea ejecutar.
     * @param asignaciones Mapa que relaciona los identificadores de los
     *        secuaces con los roles que desempenaran.
     * @throws MisionNoEncontradaException Si la mision no existe.
     * @throws IllegalArgumentException Si los datos o las asignaciones
     *         no son validos.
     * @throws IllegalStateException Si la mision no puede ejecutarse,
     *         ya tiene una ejecucion activa o algun secuaz no esta disponible.
     * @throws java.util.NoSuchElementException Si algun secuaz no existe.
     */
    public void ejecutarMisionManual(String idMision, Map<String, TipoRol> asignaciones) throws MisionNoEncontradaException {
        ctrlEjecucion_.ejecutarMisionManual(idMision, asignaciones, grafoMisiones_, conjuntoSecuaces_, prision_);
    }

    /**
     * Inicia automaticamente las ejecuciones de las misiones seleccionadas
     * mediante la estrategia de asignacion configurada.
     *
     * Considera las misiones disponibles y los secuaces registrados,
     * construye los equipos y registra las ejecuciones validas
     * en el historial.
     *
     * @throws IllegalStateException Si no se ha configurado una
     *         estrategia de asignacion.
     */
    public void ejecutarMisionAutomatica() {
        ctrlEjecucion_.ejecutarMisionAutomatica(grafoMisiones_, conjuntoSecuaces_, prision_);
    }

    /**
     * Determina y registra el resultado de una ejecucion de mision.
     *
     * Finaliza la ejecucion, actualiza el estado de la mision y
     * de los secuaces participantes y procesa las consecuencias
     * economicas y de disponibilidad correspondientes.
     *
     * @param idEjecucion Identificador de la ejecucion que se desea finalizar.
     * @param exito Indicador de exito recibido por la operacion.
     * @param botinObtenido Cantidad de botin recibida por la operacion.
     * @throws IllegalArgumentException Si el identificador no es valido
     *         o la ejecucion no existe.
     * @throws IllegalStateException Si la ejecucion ya ha sido finalizada.
     */
    public void registrarResultadoMision(String idEjecucion, boolean exito, double botinObtenido) {
        ctrlEjecucion_.registrarResultadoMision(idEjecucion, grafoMisiones_, organizacion_, prision_);
    }

    // --- CONSULTAS Y ESTADÍSTICAS ---

    /**
     * Obtiene los tres secuaces mejor clasificados segun
     * el criterio de rendimiento establecido por el sistema.
     *
     * @return Lista con un maximo de tres secuaces.
     */
    public List<Secuaz> consultarTop3Operativos() {
        return ctrlEstadisticas_.consultarTop3Operativos();
    }

    /**
     * Obtiene las tres misiones mejor clasificadas
     * segun su botin economico.
     *
     * @return Lista con un maximo de tres misiones,
     *         ordenadas segun su botin.
     */
    public List<Mision> consultarTop3GolpesHistoricos() {
        return ctrlEstadisticas_.consultarTop3GolpesHistoricos();
    }

    /**
     * Obtiene las tres misiones con mayor numero
     * de fracasos registrados.
     *
     * @return Lista con un maximo de tres misiones,
     *         ordenadas por numero de fracasos.
     */
    public List<Mision> consultarTop3PuntosCalientes() {
        return ctrlEstadisticas_.consultarTop3PuntosCalientes();
    }

    // ----------
}
