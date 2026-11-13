package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Mision;
import domain.state.EstadoFallida;

public class EstadoFallidaTest {
    private EstadoFallida estadoFallida;
    
    @Mock 
    private Mision misionMock;

    @Before
    public void before() {
        estadoFallida = new EstadoFallida();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNombre() {
        assertEquals("FALLIDA", estadoFallida.getNombreEstado());
    }

    @Test
    public void testEsEjecutable() {
        assertTrue(estadoFallida.esEjecutable());
    }

    @Test
    public void testAlCompletar() {
        estadoFallida.alCompletar(misionMock);
        verify(misionMock).cambiarEstado(any(domain.state.EstadoCompletada.class));
    }

    @Test
    public void testAlFracasar() {
        estadoFallida.alFracasar(misionMock);
        verify(misionMock).cambiarEstado(any(EstadoFallida.class));
    }
}
