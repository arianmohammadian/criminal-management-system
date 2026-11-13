package test.controllers;

import controllers.CtrlEstadisticas;
import domain.ConjuntoSecuaces;
import domain.GrafoMisiones;
import domain.HistorialEjecuciones;
import domain.Mision;
import domain.Secuaz;
import domain.RequisitoRol;
import domain.enums.TipoRol;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * CtrlEstadisticasTest
 *
 * Pruebas solo para la clase CtrlEstadisticas.
 *
 * @author Arian Mohammadian
 */
public class CtrlEstadisticasTest {

    private CtrlEstadisticas ctrl_;
    private ConjuntoSecuaces conjuntoSecuaces_;
    private GrafoMisiones grafoMisiones_;
    private HistorialEjecuciones historialEjecuciones_;

    //inicializo todas las variales vacias para cada test diferente y construyo el controlador
    @Before
    public void setUp() {
        conjuntoSecuaces_ = new ConjuntoSecuaces();
        grafoMisiones_ = new GrafoMisiones();
        historialEjecuciones_ = new HistorialEjecuciones();

        ctrl_ = new CtrlEstadisticas(conjuntoSecuaces_, grafoMisiones_, historialEjecuciones_);
    }

    @Test //comprobamos que se crea el controlador de manera no nula
    public void testCreador() {
        assertNotNull(ctrl_);
    }

    //CASO LÍMITE 1: no hay ningun dato en el sistema
    //Compruebo que la respuesta no sea nula y que este vacia
    @Test
    public void testTop3OperativosNull() {
        List<Secuaz> resuultado =ctrl_.consultarTop3Operativos();
        assertNotNull(resuultado);
        assertTrue(resuultado.isEmpty());
    }
    @Test
    public void testTop3GolpesHistoricosNull() {
        List<Mision> resuultado =ctrl_.consultarTop3GolpesHistoricos();
        assertNotNull(resuultado);
        assertTrue(resuultado.isEmpty());
    }
    @Test
    public void testTop3PuntosCalientesNull() {
        List<Mision> resuultado =ctrl_.consultarTop3PuntosCalientes();
        assertNotNull(resuultado);
        assertTrue(resuultado.isEmpty());
    }

    //CASO LÍMITE 2: el sistema no esta vacío pero tiene menos de 3 elementos para comprovar
    @Test
    public void testTop3ConMenosDeTres() throws Exception {
        Mision mision_1= new Mision("Mision_1",4000.0, 10.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        Mision mision_2 =new Mision("Mision_2", 20000.0, 20.0, Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        grafoMisiones_.anadirMision(mision_1);
        grafoMisiones_.anadirMision(mision_2);

        //llamada a funcion + comprovar que esten bien ordenados (el 1 no es necesario)
        List<Mision> resuultado = ctrl_.consultarTop3GolpesHistoricos();
        assertEquals(2, resuultado.size());  //miramos que se devuelvan 2 elementos y no 3
        assertEquals("Mision_2", resuultado.get(0).getId()); 
    }

    //CASO LÍMITE 3: el sistema tiene 3 o mas elementos
    @Test
    public void testTop3GolpesHistoricosOrden() throws Exception{
        Mision mision_1= new Mision("Mision_1", 1000.0, 5.0, Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        grafoMisiones_.anadirMision(mision_1);
        Mision mision_2 = new Mision("Mision_2",5000.0,10.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        grafoMisiones_.anadirMision(mision_2);
        Mision mision_3 =new Mision("Mision_3", 2500.0, 15.0, Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        grafoMisiones_.anadirMision(mision_3);
        Mision mision_4= new Mision("Mision_4", 8000.0, 20.0, Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        grafoMisiones_.anadirMision(mision_4);

        List<Mision> res= ctrl_.consultarTop3GolpesHistoricos();
        assertEquals(3, res.size()); //devuelve 3 elementos
        assertEquals("Mision_4",res.get(0).getId()); //estan bien ordenados
        assertEquals("Mision_2", res.get(1).getId());
        assertEquals("Mision_3",res.get(2).getId());
    }

    //Comprovamos que se ordenen por fracasos y no por botín
    @Test
    public void testTop3PuntosCalientesOrden() throws Exception{
        Mision mision_1 =new Mision("Mision_1",1000.0, 5.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        Mision mision_2= new Mision("Mision_2",2000.0, 5.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        Mision mision_3= new Mision("Mision_3",3000.0, 5.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        Mision mision_4= new Mision("Mision_4",4000.0, 5.0,Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1)));
        mision_1.alFracasar();
        mision_2.alFracasar();
        mision_2.alFracasar();
        mision_2.alFracasar();
        mision_4.alFracasar();
        mision_4.alFracasar();
        grafoMisiones_.anadirMision(mision_1);
        grafoMisiones_.anadirMision(mision_2);
        grafoMisiones_.anadirMision(mision_3);
        grafoMisiones_.anadirMision(mision_4);

        List<Mision> resultado = ctrl_.consultarTop3PuntosCalientes();
        assertEquals(3, resultado.size());
        assertEquals("Mision_2", resultado.get(0).getId());
        assertEquals("Mision_4", resultado.get(1).getId());
        assertEquals("Mision_1", resultado.get(2).getId());
    }
}