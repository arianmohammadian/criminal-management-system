package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Mision;
import domain.state.EstadoCompletada;

public class EstadoCompletadaTest {
    private EstadoCompletada estadoCompletada;

    @Mock
    private Mision misionMock;

    @Before
    public void before() {
        estadoCompletada = new EstadoCompletada();
        MockitoAnnotations.initMocks(this);    
    }

    @Test
    public void testGetNombre() {
        assertEquals("COMPLETADA", estadoCompletada.getNombreEstado());
    }

    @Test
    public void testEsEjecutable() {
        assertFalse(estadoCompletada.esEjecutable());
    }

    @Test
    public void testAlCompletar() {
        estadoCompletada.alCompletar(misionMock);
        verifyNoInteractions(misionMock);
    }

    @Test
    public void testAlFracasar() {
        estadoCompletada.alFracasar(misionMock);
        verifyNoInteractions(misionMock);
    }
}
