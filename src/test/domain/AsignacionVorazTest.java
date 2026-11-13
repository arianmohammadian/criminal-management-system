
package test.domain;

import domain.strategy.AsignacionVoraz;
import domain.Mision;
import domain.Secuaz;
import domain.RequisitoRol;
import domain.enums.TipoRol;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 * AsignacionVorazTest
 *
 * Ejecuta los tests unitarios correspondientes a la estrategia
 * voraz de asignacion de secuaces a misiones.
 *
 * @author Arian Mohammadian
 */
public class AsignacionVorazTest {

    private AsignacionVoraz estrategia_;

    @Mock
    private Mision mision1_;

    @Mock
    private Mision mision2_;

    @Mock
    private Secuaz secuaz1_;

    @Mock
    private Secuaz secuaz2_;

    @Mock
    private Secuaz secuaz3_;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        estrategia_ = new AsignacionVoraz();
    }

    
    // TESTS ASIGNACION
    @Test
    public void testAsignacionEspecialista() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Arrays.asList(secuaz1_)
            );

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey(mision1_));

        Map<Secuaz, TipoRol> equipo = resultado.get(mision1_);

        assertEquals(1, equipo.size());
        assertEquals(TipoRol.CONDUCTOR, equipo.get(secuaz1_));
    }

    @Test
    public void testAsignacionMultiplesRoles() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(
                new RequisitoRol(TipoRol.CONDUCTOR, 1),
                new RequisitoRol(TipoRol.HACKER, 1)
            )
        );

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        when(secuaz2_.estaLibre()).thenReturn(true);
        when(secuaz2_.getRolBase()).thenReturn(TipoRol.HACKER);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Arrays.asList(secuaz1_, secuaz2_)
            );

        assertTrue(resultado.containsKey(mision1_));

        Map<Secuaz, TipoRol> equipo = resultado.get(mision1_);

        assertEquals(2, equipo.size());
        assertEquals(TipoRol.CONDUCTOR, equipo.get(secuaz1_));
        assertEquals(TipoRol.HACKER, equipo.get(secuaz2_));
    }

    // TESTS SUSTITUCIONES

    @Test
    public void testSustitucionRol() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(
                new RequisitoRol(TipoRol.CONDUCTOR, 1),
                new RequisitoRol(TipoRol.HACKER, 1)
            )
        );

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        when(secuaz2_.estaLibre()).thenReturn(true);
        when(secuaz2_.getRolBase()).thenReturn(TipoRol.CERRAJERO);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Arrays.asList(secuaz1_, secuaz2_)
            );

        assertTrue(resultado.containsKey(mision1_));

        Map<Secuaz, TipoRol> equipo = resultado.get(mision1_);

        assertEquals(2, equipo.size());
        assertEquals(TipoRol.CONDUCTOR, equipo.get(secuaz1_));
        assertEquals(TipoRol.HACKER, equipo.get(secuaz2_));
    }

    // TESTS VIABILIDAD

    @Test
    public void testPersonalInsuficiente() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 2))
        );

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Arrays.asList(secuaz1_)
            );

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testMisionNoEjecutable() {
        when(mision1_.esEjecutable()).thenReturn(false);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Collections.<Secuaz>emptyList()
            );

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testCandidatosOcupados() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );

        when(secuaz1_.estaLibre()).thenReturn(false);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_),
                Arrays.asList(secuaz1_)
            );

        assertTrue(resultado.isEmpty());
    }

    // TESTS PRIORIDAD Y REUTILIZACION

    @Test
    public void testPrioridadBeneficio() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision2_.esEjecutable()).thenReturn(true);

        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );
        when(mision2_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );

        // Prioridad mision1 = 100 * 0.8 / 1 = 80
        when(mision1_.getBotin()).thenReturn(100.0);
        when(mision1_.getProbFracaso()).thenReturn(20.0);

        // Prioridad mision2 = 200 * 0.9 / 1 = 180
        when(mision2_.getBotin()).thenReturn(200.0);
        when(mision2_.getProbFracaso()).thenReturn(10.0);

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_, mision2_),
                Arrays.asList(secuaz1_)
            );

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey(mision2_));
        assertFalse(resultado.containsKey(mision1_));
    }

    @Test
    public void testNoReutilizarSecuaces() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision2_.esEjecutable()).thenReturn(true);

        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );
        when(mision2_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_, mision2_),
                Arrays.asList(secuaz1_)
            );

        // Solo se puede completar una de las dos misiones.
        assertEquals(1, resultado.size());

        int apariciones = 0;

        for (Map<Secuaz, TipoRol> equipo : resultado.values()) {
            if (equipo.containsKey(secuaz1_)) {
                ++apariciones;
            }
        }

        assertEquals(1, apariciones);
    }

    @Test
    public void testMisionInviableNoConsumeSecuaces() {
        when(mision1_.esEjecutable()).thenReturn(true);
        when(mision2_.esEjecutable()).thenReturn(true);

        when(mision1_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 3))
        );
        when(mision2_.getRequisitos()).thenReturn(
            Arrays.asList(new RequisitoRol(TipoRol.CONDUCTOR, 1))
        );

        when(mision1_.getBotin()).thenReturn(1000.0);
        when(mision2_.getBotin()).thenReturn(100.0);

        when(secuaz1_.estaLibre()).thenReturn(true);
        when(secuaz1_.getRolBase()).thenReturn(TipoRol.CONDUCTOR);

        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Arrays.asList(mision1_, mision2_),
                Arrays.asList(secuaz1_)
            );

        assertFalse(resultado.containsKey(mision1_));
        assertTrue(resultado.containsKey(mision2_));
        assertEquals(TipoRol.CONDUCTOR,
                     resultado.get(mision2_).get(secuaz1_));
    }

    // TESTS CASOS LIMITE
    @Test
    public void testListasVacias() {
        Map<Mision, Map<Secuaz, TipoRol>> resultado =
            estrategia_.seleccionarEquipos(
                Collections.<Mision>emptyList(),
                Collections.<Secuaz>emptyList()
            );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}