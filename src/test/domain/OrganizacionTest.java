package test.domain;

import domain.Organizacion;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * OrganizacionTest
 *
 * Pruebas para la clase Organizacion.
 *
 * @author Arian Mohammadian
 */
public class OrganizacionTest {

    private Organizacion org_;

    // inicializo organizacion vacia antes de cada test 
    @Before
    public void setUp() {
        org_ = new Organizacion();
    }

     //comprovar constructor
    @Test
    public void testConstructora() {
        assertNotNull(org_); 
        assertEquals("Sindicato Criminal",org_.getNombre()); //se comprueba que los dadtos sean correctos
        assertEquals(20.0, org_.getComision(), 0.001);
        assertEquals(0.0, org_.getFondos(), 0.001);
    }

    //comprovar constructor con datos personalizados
    @Test 
    public void testCreadorPersonalizado() {
        Organizacion customOrg = new Organizacion("Banda Barcelona FC", 12.0, 500.0);
        assertEquals("Banda Barcelona FC", customOrg.getNombre());
        assertEquals(12.0,customOrg.getComision(), 0.001);
        assertEquals(500.0, customOrg.getFondos(), 0.001);
    }

     //cambiar nombre quitando espacios extras
    @Test
    public void testSetNombreValido() {
        org_.setNombre(" Barcelona FC    ");
        assertEquals("Barcelona FC", org_.getNombre());
    }

    @Test(expected = IllegalArgumentException.class) // lanzo excepcion si  nombre es null
    public void testSetNombreNull() {
        org_.setNombre(null);
    }

    @Test(expected =IllegalArgumentException.class) // lanzo excepcion si el nombre esta vacio
    public void testSetNombreVacio() {
        org_.setNombre("   ");
    }

    //Comprovar cuando comision esta dentro de 0 y 100
    @Test 
    public void testSetComisionValida() {
        org_.setComisionDon(0.0);
        assertEquals(0.0, org_.getComision(), 0.001);
        org_.setComisionDon(100.0);
        assertEquals(100.0, org_.getComision(), 0.001);
        org_.setComisionDon(67.0);
        assertEquals(67.0, org_.getComision(), 0.001);
    }

    //comprovar comision menor que 0
    @Test(expected= IllegalArgumentException.class) 
    public void testSetComisionNegativa() {
        org_.setComisionDon(-67.0);
    }

    // comprovar comision mayor que 100
    @Test(expected= IllegalArgumentException.class) 
    public void testSetComisionSuperaCien() {
        org_.setComisionDon(100.1);
    }

    // comprovamos que se añaden fondos correctamente
    @Test 
    public void testAnadirFondos() {
        org_.añadirFondos(100.0);
        assertEquals(100.0, org_.getFondos(), 0.001);
        org_.añadirFondos(50.5);
        assertEquals(150.5, org_.getFondos(), 0.001);
    }

    // Comprovamos que de error al añadir fondos negativos
    @Test(expected =IllegalArgumentException.class) 
    public void testAnadirFondosNegativos(){
        org_.añadirFondos(-10.0);
    }

    // Comprovamos que se descuente si tenemos fondos suficientes
    @Test 
    public void testDescontarFondosSuficiente() {
        org_.setFondos(100.0);
        boolean bool=org_.descontarFondos(40.0);
        assertTrue(bool);
        assertEquals(60.0, org_.getFondos(), 0.001);
    }

    //Comprovamos que no se puede descontar cuando el descuento es mayor a uestros fondos
    @Test 
    public void testDescontarFondosMayores() {
        org_.setFondos(50.0);
        boolean bool =org_.descontarFondos(100.0);
        assertFalse(bool);
        assertEquals(50.0, org_.getFondos(), 0.001);
    }

    // comprovamos que da error al descontar negativo
    @Test(expected =IllegalArgumentException.class) 
    public void testDescontarFondosNegativo(){
        org_.descontarFondos(-67.0);
    }
}