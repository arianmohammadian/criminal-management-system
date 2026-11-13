package test.domain;

import domain.ConjuntoSecuaces;
import domain.Secuaz;
import domain.enums.EstadoSecuaz;

import java.util.List;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * ConjuntoSecuacesTest
 *
 * Ejecuta los tests correspondientes a la clase ConjuntoSecuaces.
 *
 * @author Arian Mohammadian
 */
public class ConjuntoSecuacesTest {
    private ConjuntoSecuaces conjunto;
    private SecuazStub secuaz1;
    private SecuazStub secuaz2;

    @Before
    public void setUp() {
        conjunto = new ConjuntoSecuaces();

        secuaz1 = new SecuazStub("S001", EstadoSecuaz.LIBRE);
        secuaz2 = new SecuazStub("S002", EstadoSecuaz.OCUPADO);
    }


    // TESTS INSERCION
    @Test
    public void testAddSecuaz() {
        conjunto.addSecuaz(secuaz1);

        assertTrue(conjunto.existeSecuaz("S001"));
        assertEquals(1, conjunto.listarTodos().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSecuazDuplicado() {
        conjunto.addSecuaz(secuaz1);

        Secuaz duplicado = new SecuazStub("S001", EstadoSecuaz.LIBRE);
        conjunto.addSecuaz(duplicado);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSecuazNulo() {
        conjunto.addSecuaz(null);
    }


    // TESTS CONSULTAS
    @Test
    public void testConsultarSecuaz() {
        conjunto.addSecuaz(secuaz1);

        Secuaz resultado = conjunto.consultarSecuaz("S001");

        assertSame(secuaz1, resultado);
    }

    @Test
    public void testExisteSecuaz() {
        conjunto.addSecuaz(secuaz1);

        assertTrue(conjunto.existeSecuaz("S001"));
        assertFalse(conjunto.existeSecuaz("S999"));
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void testConsultarSecuazInexistente() {
        conjunto.consultarSecuaz("S999");
    }


    // TESTS ELIMINACION
    @Test
    public void testEliminarSecuaz() {
        conjunto.addSecuaz(secuaz1);

        conjunto.eliminarSecuaz("S001");

        assertFalse(conjunto.existeSecuaz("S001"));
        assertTrue(conjunto.listarTodos().isEmpty());
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void testEliminarSecuazInexistente() {
        conjunto.eliminarSecuaz("S999");
    }


    // TESTS LISTADOS
    @Test 
    public void testListarTodos() {
        conjunto.addSecuaz(secuaz1);
        conjunto.addSecuaz(secuaz2);

        List<Secuaz> resultado = conjunto.listarTodos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(secuaz1));
        assertTrue(resultado.contains(secuaz2));
    } 

    @Test
    public void testListarSecuacesLibres() {
        conjunto.addSecuaz(secuaz1);
        conjunto.addSecuaz(secuaz2);

        List<Secuaz> libres = conjunto.listarSecuacesLibres();

        assertEquals(1, libres.size());
        assertTrue(libres.contains(secuaz1));
        assertFalse(libres.contains(secuaz2));
    }
}