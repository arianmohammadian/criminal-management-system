package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import domain.enums.TipoRol;
import domain.RequisitoRol;


public class RequisitoRolTest {
    private RequisitoRol reqValid;

    @Before
    public void before() {
        reqValid = new RequisitoRol(TipoRol.HACKER, 2);
    }

    @Test
    public void testCrearRequisitoRolValido() {        
        assertEquals(TipoRol.HACKER, reqValid.getTipoRol());
        assertEquals(2, reqValid.getCantidadRequerida());
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testRolNullLanzaExcepcion() {
        new RequisitoRol(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCantidadCeroLanzaExcepcion() {
        new RequisitoRol(TipoRol.CERRAJERO, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCantidadNegativaLanzaExcepcion() {
        new RequisitoRol(TipoRol.CERRAJERO, -1);
    }

    @Test
    public void testGetTipoRol() {
        assertEquals(TipoRol.HACKER, reqValid.getTipoRol());
    }

    @Test
    public void testGetCantidadRequerida() {
        assertEquals(2, reqValid.getCantidadRequerida());
    }

    @Test
    public void testCumpleRequisito() {
        assertTrue(reqValid.cumpleRequisito(3));  
        assertTrue(reqValid.cumpleRequisito(2));  
        assertFalse(reqValid.cumpleRequisito(1)); 
    }

    @Test
    public void testSetCantidadRequeridaVALIDA() {
        reqValid.setCantidadRequerida(3);
        assertEquals(3, reqValid.getCantidadRequerida());
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testSetCantidadRequeridaCeroLanzaExcepcion() {
        reqValid.setCantidadRequerida(0);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testSetCantidadRequeridaNOVALIDA() {
        reqValid.setCantidadRequerida(-1);
    }
}