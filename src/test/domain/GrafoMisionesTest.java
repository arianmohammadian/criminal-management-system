package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.GrafoMisiones;
import domain.Mision;
import domain.exceptions.MisionCicloDependenciaException;
import domain.exceptions.MisionNoEncontradaException;
import domain.exceptions.MisionYaExistenteException;
import domain.state.EstadoDisponible;

public class GrafoMisionesTest {

    private GrafoMisiones grafo;

    @Mock
    private Mision misionA;

    @Mock
    private Mision misionB;

    @Mock
    private Mision misionC;

    @Mock
    private Mision misionD;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        grafo = new GrafoMisiones();

        when(misionA.getId()).thenReturn("M01");
        when(misionB.getId()).thenReturn("M02");
        when(misionC.getId()).thenReturn("M03");
        when(misionD.getId()).thenReturn(null);

        when(misionA.getBotin()).thenReturn(1000.0);
        when(misionB.getBotin()).thenReturn(2500.0);
        when(misionC.getBotin()).thenReturn(1500.0);

        when(misionA.getEstadoActual()).thenReturn(new EstadoDisponible());
        when(misionB.getEstadoActual()).thenReturn(new EstadoDisponible());
        when(misionC.getEstadoActual()).thenReturn(new EstadoDisponible());
    }

    @Test
    public void testAnadirMisionNoException() throws Exception {
        grafo.anadirMision(misionA);

        assertTrue(grafo.existeMision("M01"));
        assertEquals(misionA, grafo.buscarMision("M01"));
        assertEquals(1, grafo.obtenerTodas().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnadirMisionNullLanzaExcepcion() throws Exception {
        grafo.anadirMision(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnadirMisionIDNullLanzaExcepcion() throws Exception {
        grafo.anadirMision(misionD);
    }

    @Test(expected = MisionYaExistenteException.class)
    public void testAnadirMisionDuplicadaLanzaExcepcion() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirMision(misionA);
    }

    @Test(expected = MisionNoEncontradaException.class)
    public void testBuscarMisionInexistenteLanzaExcepcion() throws Exception {
        grafo.buscarMision("NO_ESTOY");
    }

    @Test
    public void testEliminarMisionAisladaExito() throws Exception {
        grafo.anadirMision(misionA);
        assertTrue(grafo.existeMision(misionA.getId()));

        grafo.eliminarMision(misionA.getId());

        assertFalse(grafo.existeMision(misionA.getId()));
        assertEquals(0, grafo.obtenerTodas().size());
    }

    @Test
    public void testEliminarMisionExito() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirMision(misionB);
        grafo.anadirMision(misionC);
        grafo.anadirDependencia(misionB.getId(), misionA.getId()); //M01 -> M02
        grafo.anadirDependencia(misionC.getId(), misionB.getId()); //M01 -> M02 -> M03
        grafo.eliminarMision(misionB.getId());
        
        assertFalse(grafo.existeMision(misionB.getId()));
        assertTrue(grafo.existeMision(misionA.getId()));
        assertTrue(grafo.existeMision(misionC.getId()));
        assertTrue(grafo.obtenerPrerrequisitos(misionC.getId()).size() == 0);
        assertTrue(grafo.obtenerSucesores(misionA.getId()).size() == 0);
    }

    @Test(expected = MisionNoEncontradaException.class)
    public void testEliminarMisionNoEncontradaLanzaExcepcion() throws Exception {
        grafo.eliminarMision("NO_EXISTE");
    }

    @Test
    public void testAnadirDependenciaValida() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirMision(misionB); //M01 -> M02

        grafo.anadirDependencia(misionB.getId(), misionA.getId());

        List<Mision> pres = grafo.obtenerPrerrequisitos(misionB.getId());
        assertEquals(1, pres.size());
        assertEquals(misionA, pres.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnadirAutoDependenciaLanzaExcepcion() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirDependencia(misionA.getId(), misionA.getId());
    }

    @Test(expected = MisionCicloDependenciaException.class)
    public void testAnadirDependenciaCicloLanzaExcepcion() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirMision(misionB);

        grafo.anadirDependencia(misionB.getId(), misionA.getId()); // M01 -> M02
        grafo.anadirDependencia(misionA.getId(), misionB.getId()); // M02 -> M01
    }

    @Test
    public void testObtenerOrdenEjecucion() throws Exception {
        grafo.anadirMision(misionA); 
        grafo.anadirMision(misionB); 
        grafo.anadirMision(misionC); 

        List<Mision> orden = grafo.obtenerOrdenEjecucion();

        assertEquals(3, orden.size());
        assertEquals(misionB.getId(), orden.get(0).getId()); 
        assertEquals(misionC.getId(), orden.get(1).getId()); 
        assertEquals(misionA.getId(), orden.get(2).getId()); 
    }

    @Test
    public void testObtenerOrdenEjecucion2() throws Exception {
        grafo.anadirMision(misionA); 
        grafo.anadirMision(misionB); 
        
        grafo.anadirDependencia(misionB.getId(), misionA.getId());

        List<Mision> orden = grafo.obtenerOrdenEjecucion();

        assertEquals(2, orden.size());
        assertEquals(misionA.getId(), orden.get(0).getId()); 
        assertEquals(misionB.getId(), orden.get(1).getId());
    }

    @Test
    public void testEliminarDependenciaActualizaPrerrequisitos() throws Exception {
        grafo.anadirMision(misionA);
        grafo.anadirMision(misionB);
        grafo.anadirDependencia(misionB.getId(), misionA.getId());

        assertEquals(1, grafo.obtenerPrerrequisitos(misionB.getId()).size());

        grafo.eliminarDependencia(misionB.getId(), misionA.getId());
        assertEquals(0, grafo.obtenerPrerrequisitos(misionB.getId()).size());
    }
}