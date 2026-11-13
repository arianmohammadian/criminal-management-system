package test.domain;

import domain.Mision;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.TipoRol;
import domain.strategy.AsignacionBacktracking;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * OrganizacionTest
 *
 * Pruebas para la estrategia asignacion por Backtracking.
 *
 * @author Arian Mohammadian
 */
public class AsignacionBacktrackingTest {

    private AsignacionBacktracking estrategia;
    private List<Mision> misiones;
    private List<Secuaz> secuaces;

    @Before
    public void setUp() {
        estrategia = new AsignacionBacktracking();
        misiones = new ArrayList<>();
        secuaces = new ArrayList<>();
    }

    @Test
    public void testProbarConListaVacioONula() {
        Map<Mision, Map<Secuaz, TipoRol>> resultadoNulo = estrategia.seleccionarEquipos(null, null);
        assertTrue("Si la respuesta es null, tiene que devolver un map vacío", resultadoNulo.isEmpty());

        Map<Mision, Map<Secuaz, TipoRol>> resultadoVacio = estrategia.seleccionarEquipos(misiones, secuaces);
        assertTrue("Si la respuesta es vacia, tiene que devolver un map vacío", resultadoVacio.isEmpty());
    }

    @Test
    public void testSeleccionarEquiposConSecuacesInsuficientes() {
        //usamos mock para simular que tenemos una mision funcional,que requiera de 
        //de dos conductores pero solo tenemos uno en el sistema. Por lo tanto la mision
        //no puede realizarse.
        Mision mision = mock(Mision.class);
        when(mision.esEjecutable()).thenReturn(true);
        when(mision.getBotin()).thenReturn(5000.0);
        
        RequisitoRol req1 = mock(RequisitoRol.class);
        when(req1.getTipoRol()).thenReturn(TipoRol.CONDUCTOR);
        when(req1.getCantidadRequerida()).thenReturn(2); // Requiere 2
        when(mision.getRequisitos()).thenReturn(Arrays.asList(req1));
        when(mision.getCantidadTotalRequerida()).thenReturn(2);

        Secuaz s1 = mock(Secuaz.class);
        when(s1.estaLibre()).thenReturn(true);
        when(s1.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        misiones.add(mision);
        secuaces.add(s1);

        Map<Mision, Map<Secuaz, TipoRol>> resultado = estrategia.seleccionarEquipos(misiones, secuaces);
        
        assertTrue("No debe asignarse ninguna mision si no tenemos secuaces suficientes", resultado.isEmpty());
    }

    @Test
    public void testSeleccionarEquipos_AsignacionPerfectaUnaMision() {
        //Simulamos que tenemos una mision que solo necesita un secuaz de tipo ASALTO para ejecutarse
        //y creamos un secuaz de ese tipo. Comprobamos que la solucion nos da esa sola mision, que la mision es 
        //esta disponible y que secuazz ha sido asignado como ASALTO
        Mision mision = mock(Mision.class);
        when(mision.esEjecutable()).thenReturn(true);
        when(mision.getBotin()).thenReturn(10000.0);
        when(mision.getProbFracaso()).thenReturn(10.0);
        
        RequisitoRol req = mock(RequisitoRol.class);
        when(req.getTipoRol()).thenReturn(TipoRol.ASALTO);
        when(req.getCantidadRequerida()).thenReturn(1);
        when(mision.getRequisitos()).thenReturn(Arrays.asList(req));
        when(mision.getCantidadTotalRequerida()).thenReturn(1);

        Secuaz secuazz = mock(Secuaz.class);
        when(secuazz.estaLibre()).thenReturn(true);
        when(secuazz.getRolBase()).thenReturn(TipoRol.ASALTO);

        misiones.add(mision);
        secuaces.add(secuazz);

        Map<Mision, Map<Secuaz, TipoRol>> resultado = estrategia.seleccionarEquipos(misiones, secuaces);

        assertEquals("La solucion solamente debe tener una mision", 1, resultado.size());
        assertTrue("La solucion tiene que tener la mision disponible", resultado.containsKey(mision));
        assertEquals("Tiene que asignar a secuazz como ASALTO", TipoRol.ASALTO, resultado.get(mision).get(secuazz));
    }

    @Test
    public void testSeleccionarEquipos_MaximizacionBeneficioConSustitucion() {
        //Simulamos una mision A que tiene 50k euros de botin, requiere 2 ASALTO y tiene prob Fracaso = 0%
        //Simulamos una mision B que tiene 10k euros de botin, requiere 2 HACKER y tiene prob Fracaso = 0%
        
        //MISION A
        Mision misionA = mock(Mision.class);
        when(misionA.esEjecutable()).thenReturn(true);
        when(misionA.getBotin()).thenReturn(50000.0);
        when(misionA.getProbFracaso()).thenReturn(0.0);
        
        RequisitoRol reqA = mock(RequisitoRol.class);
        when(reqA.getTipoRol()).thenReturn(TipoRol.ASALTO);
        when(reqA.getCantidadRequerida()).thenReturn(2);
        when(misionA.getRequisitos()).thenReturn(Arrays.asList(reqA));
        when(misionA.getCantidadTotalRequerida()).thenReturn(2);

        // MISION B
        Mision misionB = mock(Mision.class);
        when(misionB.esEjecutable()).thenReturn(true);
        when(misionB.getBotin()).thenReturn(10000.0);
        when(misionB.getProbFracaso()).thenReturn(0.0);
        
        RequisitoRol reqB = mock(RequisitoRol.class);
        when(reqB.getTipoRol()).thenReturn(TipoRol.HACKER);
        when(reqB.getCantidadRequerida()).thenReturn(2);
        when(misionB.getRequisitos()).thenReturn(Arrays.asList(reqB));
        when(misionB.getCantidadTotalRequerida()).thenReturn(2);

        misiones.add(misionA);
        misiones.add(misionB);

        // Tenemos 1 Asalto y 1 Hacker. 
        // Si hacen la Misión A: m=1, N=2. P(Éxito)= 1/2 * 1 =0.5. Beneficio = 25k €.
        // Si hacen la Misión B: m=1, N=2. P(Éxito)= 1/2 * 1 =0.5. Beneficio = 5k €.
        // Por lo tanto, el backtracking tiene que escoger antes la mision A que la B,
        // y tiene que sustituir roles.
        Secuaz asalto = mock(Secuaz.class);
        when(asalto.estaLibre()).thenReturn(true);
        when(asalto.getRolBase()).thenReturn(TipoRol.ASALTO);

        Secuaz hacker = mock(Secuaz.class);
        when(hacker.estaLibre()).thenReturn(true);
        when(hacker.getRolBase()).thenReturn(TipoRol.HACKER);

        secuaces.add(asalto);
        secuaces.add(hacker);

        Map<Mision, Map<Secuaz, TipoRol>> resultado = estrategia.seleccionarEquipos(misiones, secuaces);

        assertTrue("El backtrack debe priorizar la mision A a pesar de que se tiene que sustituir un rol", 
        resultado.containsKey(misionA));
        assertFalse("No se debe añadir la Mision B", resultado.containsKey(misionB));
        
        Map<Secuaz, TipoRol> equipo = resultado.get(misionA);
        assertEquals("Mision A debe tener 2 secuaces asignados", 2, equipo.size());
        assertEquals("El HACKER debe hacer el rol ASALTO", TipoRol.ASALTO, equipo.get(hacker));
    }

}