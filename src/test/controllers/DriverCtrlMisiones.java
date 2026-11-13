package test.controllers;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import controllers.CtrlMisiones;
import domain.Mision;
import domain.RequisitoRol;
import domain.Secuaz;
import domain.enums.TipoRol;
import domain.exceptions.MisionCicloDependenciaException;
import domain.exceptions.MisionException;
import domain.exceptions.MisionNoEncontradaException;

public class DriverCtrlMisiones {
    private static CtrlMisiones ctrlMisiones;
    private static Scanner consola;

    public static void main(String[] args) {
        ctrlMisiones = new CtrlMisiones();
        consola = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            System.out.print("> ");
            if (!consola.hasNextLine()) break;

            String linea = consola.nextLine().trim();
            if (linea.isEmpty()) continue;

            try (Scanner sc = new Scanner(linea)) {
                sc.useLocale(Locale.US);
                String command = sc.next().toLowerCase();

                switch (command) {
                    case "ayuda":
                    case "help":
                        help();
                        break;
                    case "crear":
                        driverCrearMision(sc);
                        break;
                    case "rescate":
                        driverCrearMisionRescate(sc);
                        break;
                    case "editar":
                        driverEditarMision(sc);
                        break;
                    case "eliminar":
                        driverEliminarMision(sc);
                        break;
                    case "consultar":
                        driverConsultarMision(sc);
                        break;
                    case "listar":
                        driverListarMisiones();
                        break;
                    case "dependencia":
                        driverAnadirDependencia(sc);
                        break;
                    case "quitardependencia":
                        driverEliminarDependencia(sc);
                        break;
                    case "prerrequisitos":
                        driverConsultarPrerrequisitos(sc);
                        break;
                    case "desbloqueos":
                        driverConsultarDesbloqueos(sc);
                        break;
                    case "planificar":
                        driverPlanificarGolpe();
                        break;
                    case "salir":
                    case "exit":
                        exit = true;
                        System.out.println("Saliendo del Driver...");
                        break;
                    default:
                        System.out.println("[ERROR] Comando desconocido. Escribe 'help' para ver los métodos.");
                }
            } catch (InputMismatchException e) {
                System.out.println("[ERROR] Algún parámetro numérico o tipo de dato es incorrecto.");
            } catch (NoSuchElementException e) {
                System.out.println("[ERROR] Faltan parámetros en el comando. Escribe 'ayuda' para ver el uso.");
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[EXCEPCION] " + e.getMessage());
            }
        }
        consola.close();
    }

    private static void help() {
        System.out.println("Comandos disponibles:");
        System.out.println("  crear <id> <botin> <prob> [<ROL> <CANT> ...]        - Crea una misión con su plantilla");
        System.out.println("  rescate <id> <prob> <idSec> <rolSec> [<ROL> <CANT> ...] - Crea misión de rescate");
        System.out.println("  editar <id> <botin> <prob>                         - Edita botín y probabilidad");
        System.out.println("  eliminar <id>                                      - Elimina una misión");
        System.out.println("  consultar <id>                                     - Muestra detalles de una misión");
        System.out.println("  listar                                             - Lista todas las misiones");
        System.out.println("  dependencia <idMision> <idPrerrequisito>           - Añade prerrequisito a una misión");
        System.out.println("  quitardependencia <idMision> <idPrerrequisito>     - Elimina una dependencia");
        System.out.println("  prerrequisitos <idMision>                          - Lista prerrequisitos de la misión");
        System.out.println("  desbloqueos <idMision>                             - Lista misiones sucesoras");
        System.out.println("  planificar                                         - Orden de ejecución óptimo");
        System.out.println("  exit                                               - Termina el driver");
    }

    private static void driverCrearMision(Scanner sc) throws Exception {
        String id = sc.next();
        double botin = sc.nextDouble();
        double probFracaso = sc.nextDouble();

        Map<TipoRol, Integer> plantilla = new HashMap<>();

        while (sc.hasNext()) {
            TipoRol rol = TipoRol.valueOf(sc.next().toUpperCase());
            int cant = sc.nextInt();

            plantilla.put(rol, plantilla.getOrDefault(rol, 0) + cant);
        }

        ctrlMisiones.crearMision(id, botin, probFracaso, plantilla);
        System.out.println("Misión '" + id + "' creada con " + plantilla.size() + " roles distintos.");
    }

    private static void driverCrearMisionRescate(Scanner sc) throws Exception {
        String id = sc.next();
        double probFracaso = sc.nextDouble();
        String idPrisionero = sc.next();
        TipoRol rol = TipoRol.valueOf(sc.next().toUpperCase());

        Secuaz prisionero = new Secuaz(idPrisionero, rol);
        prisionero.encarcelar();
        Map<TipoRol, Integer> plantilla = new HashMap<>();

        while (sc.hasNext()) {
            TipoRol rol2 = TipoRol.valueOf(sc.next().toUpperCase());
            int cant = sc.nextInt();

            plantilla.put(rol2, plantilla.getOrDefault(rol2, 0) + cant);
        }

        ctrlMisiones.crearMisionRescate(id, probFracaso, plantilla, prisionero);
        System.out.println("Misión de rescate '" + id + "' creada (Prisionero: " + idPrisionero + ").");
    }

    private static void driverEditarMision(Scanner sc) throws MisionException {
        String id = sc.next();
        double botin = sc.nextDouble();
        double prob = sc.nextDouble();

        ctrlMisiones.editarMision(id, botin, prob);
        System.out.println("Misión '" + id + "' actualizada.");
    }

    private static void driverEliminarMision(Scanner sc) throws MisionException {
        String id = sc.next();
        ctrlMisiones.eliminarMision(id);
        System.out.println("Misión '" + id + "' eliminada.");
    }

    private static void driverConsultarMision(Scanner sc) throws MisionException {
        String id = sc.next();
        Mision m = ctrlMisiones.consultarMision(id);
        imprimirMision(m);
    }

    private static void driverListarMisiones() {
        List<Mision> misiones = ctrlMisiones.listarMisiones();
        if (misiones.isEmpty()) {
            System.out.println("No hay misiones registradas.");
            return;
        }
        for (Mision m : misiones) {
            imprimirMision(m);
        }
    }

    private static void driverAnadirDependencia(Scanner sc) throws MisionNoEncontradaException, MisionCicloDependenciaException {
        String idMision = sc.next();
        String idPrerrequisito = sc.next();

        ctrlMisiones.anadirDependencia(idMision, idPrerrequisito);
        System.out.println(idMision + "' ahora depende de '" + idPrerrequisito + "'.");
    }

    private static void driverEliminarDependencia(Scanner sc) throws MisionNoEncontradaException {
        String idMision = sc.next();
        String idPrerrequisito = sc.next();

        ctrlMisiones.eliminarDependencia(idMision, idPrerrequisito);
        System.out.println("Dependencia eliminada entre '" + idMision + "' y '" + idPrerrequisito + "'.");
    }

    private static void driverConsultarPrerrequisitos(Scanner sc) throws MisionNoEncontradaException {
        String id = sc.next();
        List<Mision> pres = ctrlMisiones.consultarPrerrequisitos(id);
        System.out.println("Prerrequisitos de '" + id + "' (" + pres.size() + "):");
        for (Mision m : pres) {
            System.out.println(" - " + m.getId());
        }
    }

    private static void driverConsultarDesbloqueos(Scanner sc) throws MisionNoEncontradaException {
        String id = sc.next();
        List<Mision> sucs = ctrlMisiones.consultarDesbloqueosPotenciales(id);
        System.out.println("Desbloqueos potenciales de '" + id + "' (" + sucs.size() + "):");
        for (Mision m : sucs) {
            System.out.println(" - " + m.getId());
        }
    }

    private static void driverPlanificarGolpe() {
        List<Mision> plan = ctrlMisiones.planificarGolpes();
        if (plan.isEmpty()) {
            System.out.println("No hay misiones para planificar.");
            return;
        }
        System.out.println("=== PLANIFICACIÓN DE GOLPES (Orden Topológico / Botín) ===");
        for (int i = 0; i < plan.size(); i++) {
            Mision m = plan.get(i);
            System.out.printf(Locale.US, "%d. [%s] Botín: %.2f | Prob. Fracaso: %.1f%%\n",
                    (i + 1), m.getId(), m.getBotin(), m.getProbFracaso());
        }
    }

    private static void imprimirMision(Mision m) {
        String estado = (m.getEstadoActual() != null) ? m.getEstadoActual().getClass().getSimpleName() : "N/A";
        System.out.println("-> ID: " + m.getId() + " | Botín: " + m.getBotin() +
                           " | Prob. Fracaso: " + m.getProbFracaso() + "% | Estado: " + estado);
        System.out.print("   Plantilla: ");
        if (m.getRequisitos().isEmpty()) {
            System.out.println("(Sin requisitos)");
        } else {
            for (RequisitoRol r : m.getRequisitos()) {
                System.out.print("[" + r.getTipoRol() + ": " + r.getCantidadRequerida() + "] ");
            }
            System.out.println();
        }
    }
}