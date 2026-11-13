package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.domain.AsignacionBacktrackingTest;
import test.domain.*;
import test.controllers.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AsignacionBacktrackingTest.class,
    AsignacionVorazTest.class,

    AsignacionRolTest.class,
    RequisitoRolTest.class,

    EstadoBloqueadaTest.class,
    EstadoFallidaTest.class,
    EstadoDisponibleTest.class,
    EstadoCompletadaTest.class,

    SecuazTest.class,
    ConjuntoSecuacesTest.class,
    
    MisionTest.class,
    GrafoMisionesTest.class,
    EjecucionMisionTest.class,

    HistorialEjecucionesTest.class,

    OrganizacionTest.class,

    CtrlSecuacesTest.class,
    CtrlEstadisticasTest.class,

    CtrlSecuacesTest.class,
    PrisionTest.class
})

public class AllTests {}