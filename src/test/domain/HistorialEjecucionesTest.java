package test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import domain.AsignacionRol;
import domain.EjecucionMision;
import domain.HistorialEjecuciones;
import domain.Secuaz;

/**
 * 
 * HistorialEjecucionesTest
 * 
 * @author Arian Mohammadian
 */
public class HistorialEjecucionesTest {
    private HistorialEjecuciones historial_;

    @Mock
    private EjecucionMision ejec1Mock_;

    @Mock
    private EjecucionMision ejec2Mock_;

    @Mock
    private EjecucionMision ejec3Mock_;

    @Mock
    private Secuaz secuaz1Mock_;

    @Mock
    private Secuaz secuaz2Mock_;

    @Mock
    private Secuaz secuaz3Mock_;

    @Mock
    private AsignacionRol asig1Mock_;

    @Mock
    private AsignacionRol asig2Mock_;

    @Mock
    private AsignacionRol asig3Mock_;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        historial_ = new HistorialEjecuciones();

        when(secuaz1Mock_.getId()).thenReturn("loblan");
        when(secuaz2Mock_.getId()).thenReturn("raulros");
        when(secuaz3Mock_.getId()).thenReturn("marcbby");

        when(asig1Mock_.getSecuaz()).thenReturn(secuaz1Mock_);
        when(asig2Mock_.getSecuaz()).thenReturn(secuaz2Mock_);
        when(asig3Mock_.getSecuaz()).thenReturn(secuaz3Mock_);

        when(ejec1Mock_.getIdEjecucion()).thenReturn("E1");
        when(ejec1Mock_.getEquipo()).thenReturn(Arrays.asList(asig1Mock_, asig2Mock_));

        when(ejec2Mock_.getIdEjecucion()).thenReturn("E2");
        when(ejec2Mock_.getEquipo()).thenReturn(Arrays.asList(asig1Mock_, asig2Mock_, asig3Mock_));

        when(ejec3Mock_.getIdEjecucion()).thenReturn("E3");
        when(ejec3Mock_.getEquipo()).thenReturn(Arrays.asList(asig2Mock_, asig3Mock_));
    }

    @Test
    public void testConstructorValido() {
        assertNotNull(historial_.obtenerHistorial());
        assertTrue(historial_.obtenerHistorial().isEmpty());
    }

    @Test
    public void testRegistrarEjecucionValida() {
        historial_.registrarEjecucion(ejec1Mock_);
        historial_.registrarEjecucion(ejec2Mock_);

        List<EjecucionMision> lista_ejecs = historial_.obtenerHistorial();
        assertEquals(2, lista_ejecs.size());
        assertEquals(ejec1Mock_, lista_ejecs.get(0));
        assertEquals(ejec2Mock_, lista_ejecs.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarEjecucionNula() {
        historial_.registrarEjecucion(null);
    }

    @Test
    public void testBuscarEjecucionExistente() {
        historial_.registrarEjecucion(ejec1Mock_);

        assertEquals(ejec1Mock_, historial_.buscarEjecucion("E1"));
    }

    @Test
    public void testBuscarEjecucionNoExistente() {
        historial_.registrarEjecucion(ejec1Mock_);

        assertNull(historial_.buscarEjecucion("E80"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuscarEjecucionIdNulo() {
        historial_.buscarEjecucion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuscarEjecucionIdVacio() {
        historial_.buscarEjecucion(" ");
    }

    @Test
    public void testObtenerEjecucionesDeSecuazValido() {
        historial_.registrarEjecucion(ejec1Mock_);
        historial_.registrarEjecucion(ejec2Mock_);
        historial_.registrarEjecucion(ejec3Mock_);

        List<EjecucionMision> lista_ejecs = historial_.obtenerEjecucionesDeSecuaz("loblan");
        assertEquals(2, lista_ejecs.size());
        assertTrue(lista_ejecs.contains(ejec1Mock_));
        assertTrue(lista_ejecs.contains(ejec2Mock_));

        assertTrue(historial_.obtenerEjecucionesDeSecuaz("kiki").isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtenerEjecucionesDeSecuazIdNulo() {
        historial_.obtenerEjecucionesDeSecuaz(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObtenerEjecucionesDeSecuazIdVacio() {
        historial_.obtenerEjecucionesDeSecuaz(" ");
    }

    @Test
    public void testCalcularCompliceHabitualValido() {
        historial_.registrarEjecucion(ejec1Mock_);
        historial_.registrarEjecucion(ejec2Mock_);
        historial_.registrarEjecucion(ejec3Mock_);

        assertEquals(secuaz2Mock_, historial_.calcularCompliceHabitual("loblan"));
    }

    @Test
    public void testCalcularCompliceHabitualSinComplices() {
        when(ejec1Mock_.getEquipo()).thenReturn(Collections.singletonList(asig1Mock_));
        historial_.registrarEjecucion(ejec1Mock_);

        assertNull(historial_.calcularCompliceHabitual("loblan"));
        assertNull(historial_.calcularCompliceHabitual("kiki"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularCompliceHabitualIdNulo() {
        historial_.calcularCompliceHabitual(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularCompliceHabitualIdVacio() {
        historial_.calcularCompliceHabitual(" ");
    }

    @Test
    public void testCalcularHojaServiciosValido() {
        historial_.registrarEjecucion(ejec1Mock_);
        historial_.registrarEjecucion(ejec2Mock_);
        historial_.registrarEjecucion(ejec3Mock_);

        assertEquals(2, historial_.calcularHojaServicios("loblan"));
        assertEquals(3, historial_.calcularHojaServicios("raulros"));
        assertEquals(2, historial_.calcularHojaServicios("marcbby"));
        assertEquals(0, historial_.calcularHojaServicios("kiki"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularHojaServiciosIdNulo() {
        historial_.calcularHojaServicios(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularHojaServiciosIdVacio() {
        historial_.calcularHojaServicios(" ");
    }
}
