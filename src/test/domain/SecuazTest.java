package test.domain;

import domain.Secuaz;

import domain.enums.TipoRol;
import domain.enums.EstadoSecuaz;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * 
 * SecuazTest
 * 
 * Ejecuta los tests correspondientes a la clase Secuaz
 * 
 * @author Arian Mohammadian
 */
public class SecuazTest {
    private Secuaz secuaz;

    @Before
    public void setUp() {
        secuaz = new Secuaz("S001", TipoRol.HACKER);
    }


    // TESTS CONSTRUCTOR
    @Test 
    public void testConstructorValido() {
        assertEquals("S001", secuaz.getId());
        assertEquals(TipoRol.HACKER, secuaz.getRolBase());
        assertEquals(EstadoSecuaz.LIBRE, secuaz.getEstado());
        assertEquals(0.0, secuaz.getCantidadAcumulada(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIdVacio() {
        new Secuaz("", TipoRol.HACKER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRolNulo() {
        new Secuaz("S001", null);
    }


    // TESTS TRANSICIONES DE ESTADO
    @Test
    public void testAsignarAMision() {
        secuaz.asignarAMision();

        assertEquals(EstadoSecuaz.OCUPADO, secuaz.getEstado());
        assertFalse(secuaz.estaLibre());
    }

    @Test
    public void testDesocupar() {
        secuaz.asignarAMision();

        secuaz.desocupar();

        assertEquals(EstadoSecuaz.LIBRE, secuaz.getEstado());
        assertTrue(secuaz.estaLibre());
    }

    @Test
    public void testEncarcelar() {
        secuaz.asignarAMision();

        secuaz.encarcelar();

        assertEquals(EstadoSecuaz.PRISIONERO, secuaz.getEstado());
        assertFalse(secuaz.estaLibre());
    }

    @Test
    public void testRescatarDePrision() {
        secuaz.encarcelar();

        secuaz.rescatarDePrision();

        assertEquals(EstadoSecuaz.LIBRE, secuaz.getEstado());
        assertTrue(secuaz.estaLibre());
    }

    @Test(expected = IllegalStateException.class)
    public void testAsignarSecuazOcupado() {
        secuaz.asignarAMision();
        secuaz.asignarAMision();
    }

    @Test(expected = IllegalStateException.class)
    public void testRescatarSecuazLibre() {
        secuaz.rescatarDePrision();
    }


    // TESTS MODIFICACION DE ATRIBUTOS
    @Test
    public void testSetRolBase() {
        secuaz.setRolBase(TipoRol.CONDUCTOR);

        assertEquals(TipoRol.CONDUCTOR, secuaz.getRolBase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRolBaseNulo() {
        secuaz.setRolBase(null);
    }

    @Test
    public void testSumarBotin() {
        secuaz.sumarBotin(100.0);
        secuaz.sumarBotin(50.0);

        assertEquals(150.0, secuaz.getCantidadAcumulada(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSumarBotinNegativo() {
        secuaz.sumarBotin(-50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSumarBotinInfinito() {
        secuaz.sumarBotin(Double.POSITIVE_INFINITY);
    }
}