package test.controllers;

import controllers.CtrlSecuaces;

import domain.ConjuntoSecuaces;
import domain.Secuaz;
import domain.enums.TipoRol;

import java.util.List;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 * CtrlSecuacesTest
 *
 * Ejecuta los tests correspondientes a la clase CtrlSecuaces.
 *
 * @author Arian Mohammadian
 */
@RunWith(MockitoJUnitRunner.class)
public class CtrlSecuacesTest {
    private CtrlSecuaces controlador;

    @Mock
    private ConjuntoSecuaces conjunto;

    @Before
    public void setUp() {
        controlador = new CtrlSecuaces(conjunto);
    }

    // TESTS CONSTRUCTOR
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNulo() {
        new CtrlSecuaces(null);
    }


    // TESTS CREACION Y EDICION
    @Test
    public void testCrearSecuaz() {
        controlador.crearSecuaz("S001", TipoRol.HACKER);

        ArgumentCaptor<Secuaz> captor =
            ArgumentCaptor.forClass(Secuaz.class);

        verify(conjunto).addSecuaz(captor.capture());

        Secuaz creado = captor.getValue();

        assertEquals("S001", creado.getId());
        assertEquals(TipoRol.HACKER, creado.getRolBase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearSecuazDuplicado() {
        doNothing()
            .doThrow(new IllegalArgumentException("ID duplicado"))
            .when(conjunto).addSecuaz(any(Secuaz.class));

        controlador.crearSecuaz("S001", TipoRol.HACKER);
        controlador.crearSecuaz("S001", TipoRol.CONDUCTOR);
    }

    @Test
    public void testEditarSecuaz() {
        Secuaz secuaz = new Secuaz("S001", TipoRol.HACKER);

        when(conjunto.consultarSecuaz("S001")).thenReturn(secuaz);

        controlador.editarSecuaz("S001", TipoRol.CONDUCTOR);

        assertEquals(TipoRol.CONDUCTOR, secuaz.getRolBase());
        verify(conjunto).consultarSecuaz("S001");
    }

    @Test(expected = NoSuchElementException.class)
    public void testEditarSecuazInexistente() {
        when(conjunto.consultarSecuaz("S999"))
            .thenThrow(new NoSuchElementException("Secuaz inexistente"));

        controlador.editarSecuaz("S999", TipoRol.HACKER);
    }


    // TESTS CONSULTAS
    @Test
    public void testConsultarEstadoCuentas() {
        Secuaz secuaz = new Secuaz("S001", TipoRol.HACKER);
        secuaz.sumarBotin(150.0);

        when(conjunto.consultarSecuaz("S001")).thenReturn(secuaz);

        assertEquals(
            150.0,
            controlador.consultarEstadoCuentas("S001"),
            0.001
        );
    }

    @Test
    public void testListarSecuacesLibres() {
        Secuaz libre = new Secuaz("S001", TipoRol.HACKER);

        List<Secuaz> libres = Collections.singletonList(libre);

        when(conjunto.listarSecuacesLibres()).thenReturn(libres);

        List<Secuaz> resultado = controlador.listarSecuacesLibres();

        assertEquals(1, resultado.size());
        assertSame(libre, resultado.get(0));

        verify(conjunto).listarSecuacesLibres();
    }

    
    // TESTS ELIMINACION
    @Test
    public void testEliminarSecuaz() {
        controlador.eliminarSecuaz("S001");

        verify(conjunto).eliminarSecuaz("S001");
    }
}
