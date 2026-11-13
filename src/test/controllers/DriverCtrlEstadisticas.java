package test.controllers;

import controllers.CtrlEstadisticas;
import domain.ConjuntoSecuaces;
import domain.GrafoMisiones;
import domain.HistorialEjecuciones;
import domain.Mision;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.TipoRol;

import java.util.List;
import java.util.Scanner;

/**
 * DriverCtrlEstadisticas
 *
 * Driver para probar las funcionalidades de CtrlEstadisticas.
 * 
 * @author Arian Mohammadian
 */
public class DriverCtrlEstadisticas {

    private static ConjuntoSecuaces conjuntoSecuaces_;
    private static GrafoMisiones grafoMisiones_;
    private static HistorialEjecuciones historialEjecuciones_;
    private static CtrlEstadisticas ctrl_;

    public static void main(String[] args) {
        Scanner scaner = new Scanner(System.in);

        conjuntoSecuaces_ = new ConjuntoSecuaces();
        grafoMisiones_ = new GrafoMisiones();
        historialEjecuciones_ = new HistorialEjecuciones();

        ctrl_ = new CtrlEstadisticas(conjuntoSecuaces_, grafoMisiones_, historialEjecuciones_);

        int opcion_a_escoger = -8493;
        while (opcion_a_escoger != 0) {
            enseñarOpciones();

            if (!scaner.hasNextInt()) {
                System.out.println("Error: pon un numero entero");
                scaner.nextLine();
                continue;
            }

            opcion_a_escoger = scaner.nextInt();
            scaner.nextLine();

            try {
                switch (opcion_a_escoger) {
                    case 0:
                        System.out.println("Salimos de la prueba");
                        break;

                    case 1: {
                        List<Secuaz> lista_top3 = ctrl_.consultarTop3Operativos();
                        System.out.println("\n TOP 3 OPERATIVOS");

                        if (lista_top3.isEmpty()) {
                            System.out.println("No hay secuaces o no han hecho misiones.");
                        } else {
                            for (int i = 0; i < lista_top3.size(); i++) {
                                Secuaz s = lista_top3.get(i);
                                System.out.println((i + 1) + ". Nombre: " + s.getId() + "   ROL: " + s.getRolBase());
                            }
                        }
                        break;
                    }

                    case 2: {
                        List<Mision> lista_top3 = ctrl_.consultarTop3GolpesHistoricos();
                        System.out.println("\n  TOP 3 GOLPES HISTORICOS  ");
                        if (lista_top3.isEmpty()) {
                            System.out.println("No hay misiones registradas.");
                        } else {
                            for (int i = 0; i < lista_top3.size(); i++) {
                                Mision m = lista_top3.get(i);
                                System.out.println((i + 1) + ". " + m.getId() + " (Botin: " + m.getBotin() + "€)");
                            }
                        }
                        break;
                    }

                    case 3: {
                        List<Mision> lista_top3 = ctrl_.consultarTop3PuntosCalientes();
                        System.out.println("\n   TOP 3 PUNTOS CALIENTES  ");
                        if (lista_top3.isEmpty()) {
                            System.out.println("No hay misiones registradas.");
                        } else {
                            for (int i = 0; i < lista_top3.size(); i++) {
                                Mision m = lista_top3.get(i);
                                System.out.println((i + 1) + ". " + m.getId() + " (Fracasos: " + m.getNumFracasos() + ")");
                            }
                        }
                        break;
                    }

                    case 4: {
                        cargamosDatitosDePrueba();
                        System.out.println("Datos cargados ");
                        break;
                    }
                    default:
                        System.out.println("Ópcion no válida, escribe un número entre 0 y 4");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scaner.close();
    }

    private static void cargamosDatitosDePrueba() throws Exception {
        Mision mision1 = new Mision("Robo casa Marc", 15000.0, 19.0, List.of(new RequisitoRol(TipoRol.ASALTO, 1)));
        Mision mision2 = new Mision("Asalto chalet Hugo", 800000.0, 25.0, List.of(new RequisitoRol(TipoRol.ASALTO, 1)));
        Mision mision3 = new Mision("Robo casa Arián", 6767000.0, 43.0, List.of(new RequisitoRol(TipoRol.ASALTO, 1)));
        Mision mision4 = new Mision("Robo de furgoneta de Raúl", 50000.0, 12.0, List.of(new RequisitoRol(TipoRol.ASALTO, 1)));

        mision1.alFracasar();
        mision1.alFracasar();
        mision4.alFracasar();
        mision4.alFracasar();
        mision4.alFracasar();
        mision4.alFracasar();
        mision2.alFracasar();

        grafoMisiones_.anadirMision(mision1);
        grafoMisiones_.anadirMision(mision2);
        grafoMisiones_.anadirMision(mision3);
        grafoMisiones_.anadirMision(mision4);

        Secuaz secuaz1 = new Secuaz("Vinicius", TipoRol.CONDUCTOR);
        Secuaz secuaz2 = new Secuaz("Arda", TipoRol.CERRAJERO);
        Secuaz secuaz3 = new Secuaz("Joan", TipoRol.ASALTO);

        conjuntoSecuaces_.addSecuaz(secuaz1);
        conjuntoSecuaces_.addSecuaz(secuaz2);
        conjuntoSecuaces_.addSecuaz(secuaz3);
    }

    private static void enseñarOpciones() {
        System.out.println("\n      MENU ESTADISTICAS ");
        System.out.println("1. Top 3 Operativos");
        System.out.println("2. Top 3 Golpes Historicos (botin)");
        System.out.println("3. Top 3 Puntos Calientes (fracasos)");
        System.out.println("4. Cargar datos de prueba");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }
}