package test.domain;

import domain.state.EstadoBloqueada;
import domain.MisionRescate;
import domain.state.EstadoDisponible;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.EstadoSecuaz;
import domain.enums.TipoRol;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Clase MisionRescateTest
 * 
 * Pruebas para la clase mision rescate.
 * 
 * @author Arian Mohammadian
 */
public class MisionRescateTest {

    private Secuaz objetivoPrisionero_;
    private List<RequisitoRol> requisitosValidos_;

   
    //Inicializa los mocks necesarios antes de ejecutar cada prueba.
    @Before
    public void setUp() {
        objetivoPrisionero_ =mock(Secuaz.class);
        when(objetivoPrisionero_.getEstado()).thenReturn(EstadoSecuaz.PRISIONERO);

        RequisitoRol reqConductor = mock(RequisitoRol.class);
        when(reqConductor.getTipoRol()).thenReturn(TipoRol.CONDUCTOR);
        when(reqConductor.getCantidadRequerida()).thenReturn(1);
        requisitosValidos_ =Collections.singletonList(reqConductor);
    }

    //Comprueba el constructor parametrizado usando datos válidos
    @Test
    public void testConstructoraValida() {
        MisionRescate mis= new MisionRescate("Rescate_SadCaballero",15.0, requisitosValidos_, objetivoPrisionero_);

        assertEquals("El id de la mision tiene que coincir", "Rescate_SadCaballero", mis.getId());
        assertEquals("La mision de rescate nunca tiene botin, tiene que ser 0.0", 0.0, mis.getBotin(), 0.001);
        assertEquals("La probabilidad de fracaso tiene que coincidir", 15.0, mis.getProbFracaso(), 0.001);
        assertEquals("El secuaz objetivo tiene que coincidir", objetivoPrisionero_, mis.getSecuazObjetivo());
    }

    //Mira que pase excepcion si el constructro tiene un objetivo nulo
    @Test(expected = IllegalArgumentException.class)
    public void testConstructoraObjetivoNulo() {
        new MisionRescate("Rescate_Invalido, no podemos tener un secuaz objetivo nulo", 10.0, requisitosValidos_, null);
    }

    //Comprueba que el constructor lance excepción si el secuaz objetivo no está prisionero.
    @Test(expected = IllegalStateException.class)
    public void testConstructoraObjetivoNoPrisionero() {
        Secuaz secuazLibre = mock(Secuaz.class);
        when(secuazLibre.getEstado()).thenReturn(EstadoSecuaz.LIBRE);
        new MisionRescate("Rescate_Invalido, el secuaz objetivo no puede ser libre", 10.0, requisitosValidos_, secuazLibre);
    }

    //Comprueba que funcione cuando el botin es 0 y los demas parametros son correctos
    @Test
    public void testSetBotinCeroPermitido() {
        MisionRescate mision = new MisionRescate("Rescate de loblan (quitarle la correa)", 15.0, requisitosValidos_, objetivoPrisionero_);
        mision.setBotin(0.0);
        assertEquals("El botin tiene que ser 0", 0.0, mision.getBotin(), 0.001);
    }

    //Comprueba que intentar asignar un botín distinto de cero lanza excepción.
    @Test(expected = UnsupportedOperationException.class)
    public void testSetBotinDistintoDeCero() {
        MisionRescate mision = new MisionRescate("Rescate Marc de la Play5", 15.0, requisitosValidos_, objetivoPrisionero_);
        mision.setBotin(5000.0);
    }

    //Comprueba que alCompletar() no rescata al objetivo si la misión está bloqueada (no ejecutable).
    @Test
    public void testAlCompletarMisionNoEjecutable() {
       MisionRescate mision = new MisionRescate("Rescate Arian de su casa (no sale)", 15.0, requisitosValidos_, objetivoPrisionero_);
       mision.cambiarEstado(new EstadoBloqueada());
       assertFalse("Una misión bloqueada no debe ser ejecutable", mision.esEjecutable());
       mision.alCompletar();

       verify(objetivoPrisionero_, never()).rescatarDePrision();
    }

    //Comprueba que alCompletar() cambia el estado de la misión y rescata al secuaz prisionero.
    @Test
    public void testAlCompletarRescataSecuaz() {
        MisionRescate mision = new MisionRescate("Rescate_NoviaRaul", 15.0, requisitosValidos_, objetivoPrisionero_);
        mision.cambiarEstado(new EstadoDisponible());
        assertTrue("La misión debe ser ejecutable en estado DISPONIBLE", mision.esEjecutable());
        mision.alCompletar();
        assertFalse("Después de completarse, la misión ya no debe ser ejecutable", mision.esEjecutable());
        verify(objetivoPrisionero_, times(1)).rescatarDePrision(); //rescatar solo lo llamamos una vez
    }

    
    //Comprueba que si el objetivo ya no está en prisión al acabar la misión, no se vuelve a rescatar.
    @Test
    public void testAlCompletarObjetivoYaLiberado() {
        MisionRescate mision = new MisionRescate("Rescate_SadPan", 15.0, requisitosValidos_, objetivoPrisionero_);
        mision.cambiarEstado(new EstadoDisponible());
        when(objetivoPrisionero_.getEstado()).thenReturn(EstadoSecuaz.LIBRE);
        mision.alCompletar();
        verify(objetivoPrisionero_, never()).rescatarDePrision(); //rescatarDePrision nunca se ha llamado
    }
} 
    

