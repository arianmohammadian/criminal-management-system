package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.Organizacion;
import domain.Prision;
import domain.Secuaz;
import domain.enums.EstadoSecuaz;

public class PrisionTest {
    private Prision prision;

    @Mock
    private Secuaz secuaz1Mock;

    @Mock
    private Secuaz secuaz2Mock;

    @Mock
    private Secuaz secuaz3Mock;

    @Mock
    private Organizacion organizacionMock;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        prision = new Prision();

        when(secuaz1Mock.getId()).thenReturn("S1");
        when(secuaz1Mock.getEstado()).thenReturn(EstadoSecuaz.LIBRE);
        when(secuaz2Mock.getId()).thenReturn("S2");
        when(secuaz2Mock.getEstado()).thenReturn(EstadoSecuaz.LIBRE);
        when(secuaz3Mock.getId()).thenReturn("S3");
        when(secuaz3Mock.getEstado()).thenReturn(EstadoSecuaz.LIBRE);
    }

    @Test
    public void testConstructorDefecto() {
        assertEquals(5, prision.getNivelSeguridad());
        assertEquals(1000.0, prision.getFianzaBase(), 0.001);
        assertTrue(prision.obtenerPrisioneros().isEmpty());
        assertFalse(prision.isAlertaMaxima());
    }

    @Test
    public void testConstructorParam() {
        Prision prisionParam = new Prision(8, 800.0);
        assertEquals(8, prisionParam.getNivelSeguridad());
        assertEquals(800.0, prisionParam.getFianzaBase(), 0.001);
        assertTrue(prisionParam.obtenerPrisioneros().isEmpty());
        assertTrue(prisionParam.isAlertaMaxima());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSeguridadInvalida() {
        new Prision(0, 1000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFianzaInvalida() {
        new Prision(5, -1000.0);
    }

    @Test
    public void testEncarcelarYAlertaMaxima() {
        prision.encarcelar(secuaz1Mock);
        verify(secuaz1Mock).encarcelar();
        assertFalse(prision.isAlertaMaxima());

        prision.encarcelar(secuaz2Mock);
        prision.encarcelar(secuaz3Mock);
        assertEquals(3, prision.obtenerPrisioneros().size());
        assertTrue(prision.isAlertaMaxima());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncarcelarNulo() {
        prision.encarcelar(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testEncarcelarDuplicado() {
        prision.encarcelar(secuaz1Mock);
        prision.encarcelar(secuaz1Mock);
    }

    @Test
    public void testLiberar() {
        prision.encarcelar(secuaz1Mock);
        prision.encarcelar(secuaz2Mock);
        prision.encarcelar(secuaz3Mock);
        when(secuaz1Mock.getEstado()).thenReturn(EstadoSecuaz.PRISIONERO);

        Secuaz liberado = prision.liberar("S1");

        assertSame(secuaz1Mock, liberado);
        verify(secuaz1Mock).rescatarDePrision();
        assertEquals(2, prision.obtenerPrisioneros().size());
        assertFalse(prision.isAlertaMaxima());
    }

    @Test(expected = NoSuchElementException.class)
    public void testLiberarInexistente() {
        prision.liberar("S99");
    }

    @Test
    public void testPagarFianzaExito() {
        prision.encarcelar(secuaz1Mock);
        when(secuaz1Mock.getEstado()).thenReturn(EstadoSecuaz.PRISIONERO);
        when(organizacionMock.descontarFondos(1600.0)).thenReturn(true);

        assertTrue(prision.pagarFianza("S1", organizacionMock));
        verify(organizacionMock).descontarFondos(1600.0);
        assertTrue(prision.obtenerPrisioneros().isEmpty());
    }

    @Test
    public void testPagarFianzaSinFondos() {
        prision.encarcelar(secuaz1Mock);
        when(organizacionMock.descontarFondos(anyDouble())).thenReturn(false);

        assertFalse(prision.pagarFianza("S1", organizacionMock));
        assertEquals(1, prision.obtenerPrisioneros().size());
    }

    @Test
    public void testSobornarGuardias() {
        Prision prisionAlta = new Prision(8, 1000.0);
        when(organizacionMock.descontarFondos(2500.0)).thenReturn(true);

        prisionAlta.sobornarGuardias(2500.0, organizacionMock);

        assertEquals(6, prisionAlta.getNivelSeguridad());
        assertFalse(prisionAlta.isAlertaMaxima());
    }

    @Test(expected = IllegalStateException.class)
    public void testSobornarGuardiasSinFondos() {
        when(organizacionMock.descontarFondos(1000.0)).thenReturn(false);
        prision.sobornarGuardias(1000.0, organizacionMock);
    }

    @Test
    public void testCalcularDificultadRescate() {
        assertEquals(30.0, prision.calcularDificultadRescate(), 0.001);

        prision.encarcelar(secuaz1Mock);
        prision.encarcelar(secuaz2Mock);
        prision.encarcelar(secuaz3Mock);
        assertEquals(62.0, prision.calcularDificultadRescate(), 0.001);
    }
}