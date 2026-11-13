package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Mision;
import domain.RequisitoRol;
import domain.enums.TipoRol;
import domain.state.EstadoMision;

public class MisionTest {
    private Mision mision;

    @Mock
    private EstadoMision estadoMock;

    @Mock
    private RequisitoRol reqMock1;

    @Mock 
    private RequisitoRol reqMock2;

    @Mock 
    private RequisitoRol reqMock3;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        when(reqMock1.getTipoRol()).thenReturn(TipoRol.HACKER);
        when(reqMock2.getTipoRol()).thenReturn(TipoRol.HACKER);
        when(reqMock3.getTipoRol()).thenReturn(TipoRol.CONDUCTOR);
        
        when(reqMock1.getCantidadRequerida()).thenReturn(2);
        when(reqMock2.getCantidadRequerida()).thenReturn(1);
        when(reqMock3.getCantidadRequerida()).thenReturn(1);

        List<RequisitoRol> reqs = new ArrayList<>();
        reqs.add(new RequisitoRol(TipoRol.ASALTO, 1));
        mision = new Mision("TEST", 100.0, 10.0, reqs);
    }

    @Test
    public void testConstructorValido() {
        assertEquals("TEST", mision.getId());
        assertEquals(100.0, mision.getBotin(), 0.001);
        assertEquals(10.0, mision.getProbFracaso(), 0.001);
        assertEquals(0, mision.getNumFracasos());
        assertNotNull(mision.getEstadoActual());
        assertEquals("DISPONIBLE", mision.getNombreEstado());
        assertEquals(1, mision.getRequisitos().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequisitosNulos() {
        new Mision("TEST", 100.0, 10.0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRequisitosVacios() {
        new Mision("TEST", 100.0, 10.0, new ArrayList<>());
    }

    @Test
    public void testConstructorSiRequisitos() {
        List<RequisitoRol> lista1 = new ArrayList<>();
        List<RequisitoRol> lista2 = new ArrayList<>();

        lista1.add(reqMock1);
        lista1.add(reqMock3);

        lista2.add(reqMock1);
        lista2.add(reqMock2);
        
        Mision misionConReqs1 = new Mision("MREQ1", 200.0, 20.0, lista1);
        Mision misionConReqs2 = new Mision("MREQ2", 200.0, 20.0, lista2);

        assertEquals("MREQ1", misionConReqs1.getId());
        assertEquals(2, misionConReqs1.getRequisitos().size());
        assertEquals("MREQ2", misionConReqs2.getId());
        assertEquals(1, misionConReqs2.getRequisitos().size());
    }

    @Test
    public void testSetBotin() {
        mision.setBotin(0.0);
        assertEquals(0.0, mision.getBotin(), 0.001);    
    }

    @Test
    public void testSetProbFracaso() {
        mision.setProbFracaso(0.0);
        assertEquals(0.0, mision.getProbFracaso(), 0.001);    
    }

    @Test(expected = IllegalArgumentException.class)
    public void anadirRequisitoNulo() {
        mision.añadirRequisito(null);
    }

    @Test
    public void anadirRequisito() {
        mision.añadirRequisito(reqMock1);
        assertEquals(2, mision.getRequisitos().size());
        assertEquals(TipoRol.HACKER, mision.getRequisitos().get(1).getTipoRol());
        assertEquals(2, mision.getRequisitos().get(1).getCantidadRequerida());

        mision.añadirRequisito(reqMock2);
        assertEquals(2, mision.getRequisitos().size());
        verify(reqMock1).setCantidadRequerida(3);

        mision.añadirRequisito(reqMock3);
        assertEquals(3, mision.getRequisitos().size());
    }

    @Test
    public void testGetCantidadTotalRequerida() {
        mision.añadirRequisito(reqMock1);
        mision.añadirRequisito(reqMock3);

        assertEquals(4, mision.getCantidadTotalRequerida());
        verify(reqMock1, times(2)).getCantidadRequerida();
        verify(reqMock3, times(2)).getCantidadRequerida();

        mision.añadirRequisito(reqMock2);
        assertEquals(3, mision.getRequisitos().size());
        verify(reqMock1).setCantidadRequerida(3);
    }

    @Test
    public void testEsEjecutable() {
        mision.cambiarEstado(estadoMock);
        when(estadoMock.esEjecutable()).thenReturn(true);

        assertTrue(mision.esEjecutable());
        verify(estadoMock).esEjecutable();
    }

    @Test
    public void testGetNombreEstado() {
        mision.cambiarEstado(estadoMock);
        when(estadoMock.getNombreEstado()).thenReturn("BLOQUEADA");

        assertEquals("BLOQUEADA", mision.getNombreEstado());
        verify(estadoMock).getNombreEstado();
    }

    @Test
    public void testAlCompletar() {
        mision.cambiarEstado(estadoMock);
        mision.alCompletar();

        verify(estadoMock).alCompletar(mision);
    }

    @Test
    public void testAlFracasar() {
        mision.cambiarEstado(estadoMock);
        when(estadoMock.esEjecutable()).thenReturn(true);

        assertEquals(0, mision.getNumFracasos());

        mision.alFracasar();

        assertEquals(1, mision.getNumFracasos());
        verify(estadoMock).alFracasar(mision);
    }
}