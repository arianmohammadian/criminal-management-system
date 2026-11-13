package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Mision;
import domain.state.EstadoBloqueada;

public class EstadoBloqueadaTest {
    private EstadoBloqueada estadoBloqueada;
    
    @Mock 
    private Mision misionMock;

    @Before
    public void before() {
        estadoBloqueada = new EstadoBloqueada();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNombre() {
        assertEquals("BLOQUEADA", estadoBloqueada.getNombreEstado());
    }

    @Test
    public void testEsEjecutable() {
        assertFalse(estadoBloqueada.esEjecutable());
    }

    @Test
    public void testAlCompletar() {
        estadoBloqueada.alCompletar(misionMock);
        verifyNoInteractions(misionMock);
    }

    @Test
    public void testAlFracasar() {
        estadoBloqueada.alFracasar(misionMock);
        verifyNoInteractions(misionMock);
    }
}
