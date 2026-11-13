package test.domain;

import domain.AsignacionRol;
import domain.Secuaz;
import domain.enums.TipoRol;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * 
 * AsignacionRolTest
 * 
 * 
 * @author Arian Mohammadian
 */
public class AsignacionRolTest {
    private AsignacionRol asignacionRol_;

    @Mock
    private Secuaz secuazMock_;

    @Mock
    private Secuaz secuazNuevoMock_;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        asignacionRol_ = new AsignacionRol(TipoRol.CERRAJERO, secuazMock_);
    }

    @Test
    public void testConstructorValido() {
        assertEquals(TipoRol.CERRAJERO, asignacionRol_.getRolEmpleado());
        assertEquals(secuazMock_, asignacionRol_.getSecuaz());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRolEmpleadoNulo() {
        new AsignacionRol(null, secuazMock_);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSecuazNulo() {
        new AsignacionRol(TipoRol.CERRAJERO, null);
    }

    @Test
    public void testSetRolEmpleadoValido() {
        asignacionRol_.setRolEmpleado(TipoRol.CONDUCTOR);
        assertEquals(TipoRol.CONDUCTOR, asignacionRol_.getRolEmpleado());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRolEmpleadoNulo() {
        asignacionRol_.setRolEmpleado(null);
    }

    @Test 
    public void testSetSecuazValido() {
        asignacionRol_.setSecuaz(secuazNuevoMock_);
        assertEquals(secuazNuevoMock_, asignacionRol_.getSecuaz());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSecuazNulo() {
        asignacionRol_.setSecuaz(null);
    }
}