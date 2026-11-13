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
import domain.state.EstadoDisponible;
import domain.state.EstadoFallida;
import domain.state.EstadoCompletada;

public class EstadoDisponibleTest {
    private EstadoDisponible estadoDisponible;

    @Mock
    private Mision misionMock;
    
    @Before
    public void before() {
        estadoDisponible = new EstadoDisponible();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNombre() {
        assertEquals("DISPONIBLE", estadoDisponible.getNombreEstado());
    }

    @Test
    public void testEsEjecutable() {
        assertTrue(estadoDisponible.esEjecutable());
    }

    @Test
    public void testAlCompletar() {
        estadoDisponible.alCompletar(misionMock);
        verify(misionMock).cambiarEstado(any(EstadoCompletada.class));
    }

    @Test
    public void testAlFracasar() {
        estadoDisponible.alFracasar(misionMock);
        verify(misionMock).cambiarEstado(any(EstadoFallida.class));
    }
}
