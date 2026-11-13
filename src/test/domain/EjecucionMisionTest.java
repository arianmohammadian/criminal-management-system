package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.AsignacionRol;
import domain.EjecucionMision;
import domain.Mision;
import domain.Secuaz;
import domain.enums.TipoRol;

/**
 * EjecucionMisionTest
 * 
 * @author Arian Mohammadian
 */
public class EjecucionMisionTest {
    private List<AsignacionRol> equipo_;
    private EjecucionMision ejecucionMision_;

    @Mock
    private Mision misionMock_;

    @Mock
    private Secuaz secuazConductorMock_;

    @Mock
    private Secuaz secuazHackerMock_;

    @Mock
    private AsignacionRol asig1Mock_;

    @Mock
    private AsignacionRol asig2Mock_;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(misionMock_.getProbFracaso()).thenReturn(15.0);

        when(secuazConductorMock_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);
        when(secuazHackerMock_.getRolBase()).thenReturn(TipoRol.HACKER);

        when(asig1Mock_.getSecuaz()).thenReturn(secuazConductorMock_);
        when(asig1Mock_.getRolEmpleado()).thenReturn(TipoRol.CONDUCTOR);

        when(asig2Mock_.getSecuaz()).thenReturn(secuazHackerMock_);
        when(asig2Mock_.getRolEmpleado()).thenReturn(TipoRol.HACKER);

        equipo_ = new ArrayList<>();
        equipo_.add(asig1Mock_);
        equipo_.add(asig2Mock_);

        ejecucionMision_ = new EjecucionMision("970", misionMock_, equipo_);
    }

    @Test
    public void testConstructorValido() {
        assertEquals("970", ejecucionMision_.getIdEjecucion());
        assertEquals(misionMock_, ejecucionMision_.getMision());
        assertEquals(equipo_, ejecucionMision_.getEquipo());
        assertFalse(ejecucionMision_.isExito());
        assertFalse(ejecucionMision_.isFinalizada());
        assertEquals(0.0, ejecucionMision_.getBotinObtenido(), 0.0);
        assertEquals(0.85, ejecucionMision_.getProbExitoCalculada(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIdEjecucionNulo() {
        new EjecucionMision(null, misionMock_, equipo_);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIdEjecucionVacio() {
        new EjecucionMision(" ", misionMock_, equipo_);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorMisionNula() {
        new EjecucionMision("970", null, equipo_);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEquipoNulo() {
        new EjecucionMision("970", misionMock_, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIdEquipoVacio() {
        new EjecucionMision("970", misionMock_, new ArrayList<>());
    }

    @Test
    public void testCalcularProbabilidadExitoValido() {
        double probability = EjecucionMision.calcularProbabilidadExito(misionMock_, equipo_);
        assertEquals(0.85, probability, 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularProbabilidadExitoMisionNula() {
        EjecucionMision.calcularProbabilidadExito(null, equipo_);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularProbabilidadExitoEquipoNulo() {
        EjecucionMision.calcularProbabilidadExito(misionMock_, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularProbabilidadExitoEquipoVacio() {
        EjecucionMision.calcularProbabilidadExito(misionMock_, new ArrayList<>());
    }

    @Test 
    public void testCalcularRepartoBotinExito() {
        ejecucionMision_.finalizarEjecucion(true, 100.0, 20.0);

        assertEquals(20.0, ejecucionMision_.calcularRepartoBotin(20.0), 0.001);
    }

    @Test 
    public void testCalcularRepartoBotinNoExito() {
        assertEquals(0.0, ejecucionMision_.calcularRepartoBotin(20.0), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularRepartoBotinComisionElevada() {
        ejecucionMision_.calcularRepartoBotin(101.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularRepartoBotinComisionNegativa() {
        ejecucionMision_.calcularRepartoBotin(-33.0);
    }

    @Test 
    public void testFinalizarEjecucionExito() {
        ejecucionMision_.finalizarEjecucion(true, 100.0, 20.0);

        assertTrue(ejecucionMision_.isFinalizada());
        assertTrue(ejecucionMision_.isExito());
        assertEquals(100.0, ejecucionMision_.getBotinObtenido(), 0.001);

        verify(misionMock_).alCompletar();
        verify(secuazConductorMock_).sumarBotin(AdditionalMatchers.eq(12.0, 0.001));
        verify(secuazHackerMock_).sumarBotin(AdditionalMatchers.eq(2.4, 0.001));
        verify(secuazConductorMock_).desocupar();
        verify(secuazHackerMock_).desocupar();
    }

    @Test
    public void testFinalizarEjecucionConEscaladoBotin() {
        List<AsignacionRol> equipoGrande = new ArrayList<>();
        List<Secuaz> secuacesMocks = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Secuaz sMock = mock(Secuaz.class);
            AsignacionRol asigMock = mock(AsignacionRol.class);

            when(sMock.getRolBase()).thenReturn(TipoRol.ASALTO);
            when(asigMock.getSecuaz()).thenReturn(sMock);
            when(asigMock.getRolEmpleado()).thenReturn(TipoRol.ASALTO);

            secuacesMocks.add(sMock);
            equipoGrande.add(asigMock);
        }

        EjecucionMision ejecGrande = new EjecucionMision("80", misionMock_, equipoGrande);
        ejecGrande.finalizarEjecucion(true, 1200.0, 0.0);

        for (Secuaz sMock : secuacesMocks) {
            verify(sMock).sumarBotin(AdditionalMatchers.eq(200.0, 0.001));
            verify(sMock).desocupar();
        }
    }

    @Test
    public void testFinalizarEjecucionNoExito() {
        ejecucionMision_.finalizarEjecucion(false, 100.0, 20.0);

        assertTrue(ejecucionMision_.isFinalizada());
        assertFalse(ejecucionMision_.isExito());
        assertEquals(0.0, ejecucionMision_.getBotinObtenido(), 0.0);

        verify(misionMock_).alFracasar();
        verify(secuazConductorMock_, never()).sumarBotin(anyDouble());
        verify(secuazHackerMock_, never()).sumarBotin(anyDouble());
        verify(secuazConductorMock_).desocupar();
        verify(secuazHackerMock_).desocupar();
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalizarEjecucionYaFinalizada() {
        ejecucionMision_.finalizarEjecucion(true, 100.0, 20.0);
        ejecucionMision_.finalizarEjecucion(true, 100.0, 20.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalizarEjecucionBotinNegativoConExito() {
        ejecucionMision_.finalizarEjecucion(true, -50.0, 20.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalizarEjecucionComisionNegativa() {
        ejecucionMision_.finalizarEjecucion(true, 100.0, -80.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalizarEjecucionComisionElevada() {
        ejecucionMision_.finalizarEjecucion(true, 100.0, 160.0);
    }
}