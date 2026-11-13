package domain.strategy;

import domain.Mision;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.TipoRol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Clase AsignacionBacktracking
 * 
 * Implementa una estrategia de busqueda Backtracking para encontrar la 
 * asignacion optima de secuaces a misiones. Evalua todas las combinaciones 
 * posibles (hacer o no hacer cada mision, y con que equipo) para encontrar 
 * la asignacion que maximice el beneficio total esperado, utilizando Branch 
 *  and Bound para poder cortar las ramas no prometedoras.
 * 
 * @author Arian Mohammadian
 */

public class AsignacionBacktracking implements EstrategiaAsignacion {

    private double MaximoDinero;
    private Map<Mision, Map<Secuaz, TipoRol>> mejorAsignacion;
    private List<Mision> misiones;
    private List<Secuaz> listaSecuaces;

    /**
     * Crea una instancia de Asignacion Voraz
     */
    public AsignacionBacktracking() {}

    /**
     * Este es el metodo principal que inicia la busqueda.
     * 
     * @param misiones_ Lista de misiones disponibles.
     * @param listaSecuaces_ Lista de todos los secuaces.
     * @return Map con la asignacion optima de secuaces y sus roles para cada mision seleccionada.
     */

    @Override
    public Map<Mision, Map<Secuaz, TipoRol>> seleccionarEquipos(List<Mision> misiones_, List<Secuaz> listaSecuaces_) {
        this.MaximoDinero = -1.0;
        this.mejorAsignacion = new HashMap<>();
        this.misiones = new ArrayList<>();

         if (misiones_ == null || listaSecuaces_ == null || misiones_.isEmpty() || listaSecuaces_.isEmpty()) {
            return mejorAsignacion;
        }

        for (int i = 0; i< misiones_.size(); i++) {
            Mision m = misiones_.get(i);
            if (m != null && m.esEjecutable()) {
                this.misiones.add(m);
            }
        }
        listaSecuaces = new ArrayList<>();


       
        for (int i = 0; i < listaSecuaces_.size(); i++){
            Secuaz check = listaSecuaces_.get(i);
            if (check != null && check.estaLibre()){
                this.listaSecuaces.add(check);
            }
        }
        Map<Mision, Map<Secuaz, TipoRol>> asignacionActual = new HashMap<>();
        Set<Secuaz> secuacesUsados = new HashSet<>();
        
        //Ordeno ya que asi el branch and bound es mas eficiente
        misiones.sort((m1, m2) -> Double.compare(m2.getBotin(), m1.getBotin()));
        backtrack(0, 0.0, secuacesUsados, asignacionActual); 

        return mejorAsignacion;
    }

    /**
     * Metodo recursivo que explora los caminos de misiones.
     * 
     * @param indiceMision Indice de la mision que se esta evaluando actualmente.
     * @param beneficioAcumulado Dinero total esperado con las misiones aceptadas hasta este punto.
     * @param secuacesUsados Set de secuaces que ya estan asignados a misiones anteriores.
     * @param asignacionActual Mapa temporal con los equipos formados en esta rama.
     */
    private void backtrack(int indiceMision,
                           double beneficioAcumulado,
                           Set<Secuaz> secuacesUsados,
                           Map<Mision, Map<Secuaz, TipoRol>> asignacionActual){

        if (indiceMision == misiones.size()){
            if (beneficioAcumulado>MaximoDinero){
                mejorAsignacion= clonar(asignacionActual); //para no copiar la direccion de memoria
                MaximoDinero= beneficioAcumulado; 
            }
            return;
        }

        //aqui es donde se hace el branch and bound, si no podemos mejorar el resultado con nuestra mejor opcion, return
        int NumlistaSecuacesLibres = listaSecuaces.size() - secuacesUsados.size();
        double maximoQueLlegaria = calcularMaximoRestante(indiceMision, misiones, NumlistaSecuacesLibres); 
        if (beneficioAcumulado + maximoQueLlegaria < MaximoDinero){ //dejo de buscar en esta rama
            return;
        }

        Mision mision_current = misiones.get(indiceMision);

        backtrack(indiceMision + 1, beneficioAcumulado, secuacesUsados, asignacionActual);
        List<TipoRol> RolesNecesitados = buscarPlant(mision_current.getRequisitos());
        int numRequeridos = RolesNecesitados.size();

        if (NumlistaSecuacesLibres >0 && NumlistaSecuacesLibres >= numRequeridos ){
            List<Secuaz> secuacesSinUsar = new ArrayList<>();
            for (int i = 0; i<listaSecuaces.size(); i++){
                Secuaz check = listaSecuaces.get(i);
                if (!secuacesUsados.contains(check)){
                    secuacesSinUsar.add(check);
                }
            }
        

            Map<Secuaz, TipoRol> equipoProvisional = new HashMap<>();
            Set<Secuaz> asignadosMision = new HashSet<>();

            irProbandoEquipos(0, RolesNecesitados, secuacesSinUsar, equipoProvisional, asignadosMision,
                        indiceMision, mision_current, numRequeridos, secuacesUsados, asignacionActual, beneficioAcumulado, 0);
        }
    }

    /**
     * Subrutina recursiva que genera todas las permutaciones posibles de secuaces para los roles de una mision.
     * 
     * @param indiceRol Posicion del rol actual que se intenta cubrir.
     * @param RolesNecesitados Lista aplanada con los roles requeridos por la mision.
     * @param secuacesSinUsar Lista de secuaces disponibles que no estan en ninguna otra mision.
     * @param equipoProvisional Mapa con la asignacion temporal de secuaces a roles para esta mision.
     * @param asignadosMision Set temporal para evitar que un mismo secuaz ocupe dos roles en la misma mision.
     * @param indiceMision Indice de la mision actual, necesario para continuar el backtrack principal.
     * @param mision_current El objeto Mision que se esta evaluando.
     * @param numRequeridos Tamaño total del equipo necesario.
     * @param secuacesUsados Set global de secuaces ya ocupados en la rama actual del arbol.
     * @param asignacionActual Mapa global con todas las misiones y equipos de la rama.
     * @param beneficioAcumulado Dinero acumulado antes de realizar esta mision.
     */

    private void irProbandoEquipos(int indiceRol, List<TipoRol> RolesNecesitados,List<Secuaz> secuacesSinUsar,
                                    Map<Secuaz, TipoRol> equipoProvisional, Set<Secuaz> asignadosMision,int indiceMision,
                                    Mision mision_current, int numRequeridos,Set<Secuaz> secuacesUsados,Map<Mision, Map<Secuaz, 
                                    TipoRol>> asignacionActual, double beneficioAcumulado, int InicioSecuazAnterior){
        
        if (indiceRol==RolesNecesitados.size()){
            int NumSecuazRolCorrecto = contarSecuacesConSuRol(equipoProvisional);
            double probabilidadExito = calcularProbExito(numRequeridos, NumSecuazRolCorrecto, mision_current.getProbFracaso());
            double ganancia = mision_current.getBotin() * probabilidadExito;

            secuacesUsados.addAll(asignadosMision);
            asignacionActual.put(mision_current, new HashMap<>(equipoProvisional));

            // LLamo al backtrack para que vuelva con la siguiente mision
            backtrack(indiceMision + 1, beneficioAcumulado + ganancia, secuacesUsados, asignacionActual);

            secuacesUsados.removeAll(asignadosMision);
            asignacionActual.remove(mision_current);
            return;

        }
        TipoRol rolDeAhora = RolesNecesitados.get(indiceRol);
        int inicio = 0;
        if (indiceRol> 0 && rolDeAhora == RolesNecesitados.get(indiceRol - 1)) {
            inicio = InicioSecuazAnterior + 1;
        }
        for (int i = inicio; i<secuacesSinUsar.size(); i++){
            Secuaz check = secuacesSinUsar.get(i);
            if (!asignadosMision.contains(check)){
                asignadosMision.add(check);
                equipoProvisional.put(check, rolDeAhora);
                irProbandoEquipos(indiceRol+1, RolesNecesitados, secuacesSinUsar,
                     equipoProvisional, asignadosMision, indiceMision,
                     mision_current, numRequeridos, secuacesUsados, 
                     asignacionActual, beneficioAcumulado, i);
                equipoProvisional.remove(check);
                asignadosMision.remove(check);

            }
        }
        
    
    }


    //Funciones auxiliares

    /**
     * Aplana los requisitos de rol de una mision en una lista donde cada rol aparece tantas veces como se requiere.
     * 
     * @param requisitos Lista de objetos RequisitoRol obtenidos de la mision.
     * @return Lista de TipoRol.
     */
    private List<TipoRol> buscarPlant(List<RequisitoRol> requisitos) {
        List<TipoRol> lista = new ArrayList<>();
        if (requisitos != null) {
            for (int i = 0; i<requisitos.size(); i++) {
                RequisitoRol req = requisitos.get(i);
                TipoRol rol = req.getTipoRol();
                int cantidad = req.getCantidadRequerida();
                for (int j = 0; j < cantidad; j++) {
                    lista.add(rol);
                }
            }
        }
        return lista;
    }

    /**
     * Calcula la cantidad maxima de dinero que se podria llegar a ganar por este 
     * camino sumando el beneficio maximo posible de las misiones restantes.
     * Para ver la cantidad maxima, tambien se tiene en cuenta el numero de secuaces
     * restantes, ya que no se puede superar, por eso tambien hemos ordenado las misiones,
     * para priorizar primero las que tienen mayor botin.
     * Sirve para Branch and Bound.
     * 
     * @param indiceMision Posicion desde la cual sumar los beneficios.
     * @param misiones Lista completa de misiones.
     * @return Suma de los botines ajustados por su probabilidad de fracaso.
     */
    private double calcularMaximoRestante(int indiceMision, List<Mision> misiones, int NumlistaSecuacesLibres) {
        double suma = 0;
        int mequedan = NumlistaSecuacesLibres;
        for (int i = indiceMision; i<misiones.size(); i++){
            if (mequedan<=0){
                break;
            }
            Mision mm = misiones.get(i);
            suma = suma + (mm.getBotin() *((100.0 - mm.getProbFracaso()) / 100.0));
            mequedan = mequedan - mm.getCantidadTotalRequerida();
        }    
        return suma;
    }

    /**
     * Crea una copia del mapa de asignaciones para guardar la solucion sin referencias compartidas.
     * 
     * @param original Mapa de asignaciones que se quiera clonar.
     * @return Clon del mapa original.
     */
    private Map<Mision, Map<Secuaz, TipoRol>> clonar(Map<Mision, Map<Secuaz, TipoRol>> original) {
        Map<Mision, Map<Secuaz, TipoRol>> copia = new HashMap<>();
        
        for (Mision misionCurr : original.keySet()) {
            Map<Secuaz,TipoRol> equipoOriginal = original.get(misionCurr);
            Map<Secuaz,TipoRol> equipoCopia = new HashMap<>(equipoOriginal);
            copia.put(misionCurr, equipoCopia);
        }
        return copia;
    }

    /**
     * Cuenta el numero de secuaces en un equipo que el rol del secuaz coincide con su rol base.
     * 
     * @param equipo Mapa con la relacion de Secuaz y el TipoRol que se le ha asignado en la mision.
     * @return Numero entero de secuaces trabajando en su rol natural.
     */
    private int contarSecuacesConSuRol(Map<Secuaz, TipoRol> equipo) {
        int correctos = 0;
        for (Secuaz check : equipo.keySet()) { 
            TipoRol rolAsignado = equipo.get(check);    
            if (check.getRolBase()== rolAsignado) {
                correctos++;
            }
        }
        return correctos;
    }

    /**
     * Calcula la probabilidad de exito de una mision basandose en las penalizaciones .
     * 
     * @param N Total de roles requeridos por la mision.
     * @param m Numero de secuaces asignados a su rol base correspondiente.
     * @param k Probabilidad de fracaso de la mision (en porcentaje).
     * @return Factor de exito para ajustar el botin esperado.
     */
    private double calcularProbExito(int N, int m, double k) {
        if (N <= 0){
            return 0.0;
        }
        double proporcion = (double) m / N;
        double factorFracaso = (100.0 -k)/100.0;
        return proporcion * factorFracaso;
    }
}
