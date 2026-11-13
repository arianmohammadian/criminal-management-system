package test.controllers;

import controllers.CtrlSecuaces;

import domain.ConjuntoSecuaces;
import domain.Secuaz;
import domain.enums.TipoRol;

import java.util.Scanner;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * DriverCtrlSecuaces
 *
 * Driver interactivo para probar las operaciones de CtrlSecuaces.
 *
 * @author Arian Mohammadian
 */
public class DriverCtrlSecuaces {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ConjuntoSecuaces conjunto = new ConjuntoSecuaces();
        CtrlSecuaces controlador = new CtrlSecuaces(conjunto);

        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();

            if (!scanner.hasNextInt()) {
                System.out.println("Error: introduce un numero entero");
                scanner.nextLine();
                continue;
            }

            opcion = scanner.nextInt();
            scanner.nextLine();

            System.out.print("\n");

            try {
                switch (opcion) {
                    case 0:
                        System.out.println("\nSaliendo del driver...");
                        break;

                    case 1: {
                        System.out.print("ID del secuaz a crear: ");
                        String id = scanner.nextLine();

                        System.out.print("Rol del secuaz a crear: ");
                        String rol = scanner.nextLine();

                        TipoRol rolBase = TipoRol.valueOf(rol.trim().toUpperCase());

                        controlador.crearSecuaz(id, rolBase);

                        System.out.println("\nSecuaz creado correctamente");
                        break;
                    }

                    case 2: {
                        System.out.print("ID del secuaz a editar: ");
                        String id = scanner.nextLine();

                        System.out.print("Nuevo rol del secuaz: ");
                        String rol = scanner.nextLine();

                        TipoRol nuevoRolBase = TipoRol.valueOf(rol.trim().toUpperCase());

                        controlador.editarSecuaz(id, nuevoRolBase);

                        System.out.println("\nSecuaz editado correctamente");
                        break;
                    }

                    case 3: {
                        System.out.print("ID del secuaz a eliminar: ");
                        String id = scanner.nextLine();

                        controlador.eliminarSecuaz(id);

                        System.out.println("\nSecuaz eliminado correctamente");
                        break;
                    }

                    case 4: {
                        System.out.print("ID del secuaz a consultar: ");
                        String id = scanner.nextLine();

                        Secuaz secuaz = controlador.consultarSecuaz(id);
                        
                        System.out.println("\n");
                        System.out.println("ID: " + secuaz.getId());
                        System.out.println("Rol: " + secuaz.getRolBase());
                        System.out.println("Estado: " + secuaz.getEstado());
                        System.out.println("Botin: " + secuaz.getCantidadAcumulada());
                        break;
                    }

                    case 5: {
                        List<Secuaz> secuaces = controlador.listarTodos();

                        System.out.println("\n");

                        if (secuaces.isEmpty()) {
                            System.out.println("No hay secuaces registrados");
                        }

                        for (Secuaz secuaz : secuaces) {
                            System.out.println(
                                secuaz.getId() + " - " +
                                secuaz.getRolBase() + " - " +
                                secuaz.getEstado()
                            );
                        }

                        break;
                    }

                    case 6: {
                        List<Secuaz> libres = controlador.listarSecuacesLibres();

                        System.out.println("\n");

                        if (libres.isEmpty()) {
                            System.out.println("No hay secuaces disponibles");
                        }

                        for (Secuaz secuaz : libres) {
                            System.out.println(
                                secuaz.getId() + " - " + secuaz.getRolBase()
                            );
                        }

                        break;
                    }

                    case 7: {
                        System.out.print("ID del secuaz: ");
                        String id = scanner.nextLine();

                        double cantidad = controlador.consultarEstadoCuentas(id);

                        System.out.println("\n");

                        System.out.println("Botin acumulado: " + cantidad);
                        break;
                    }

                    default:
                        System.out.println("\nError: opcion no valida");
                        break;
                }
            }
            catch (IllegalArgumentException | IllegalStateException |
                   NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n========== DRIVER CTRL SECUACES ==========");
        System.out.println("1. Crear secuaz");
        System.out.println("2. Editar rol de secuaz");
        System.out.println("3. Eliminar secuaz");
        System.out.println("4. Consultar secuaz");
        System.out.println("5. Listar todos los secuaces");
        System.out.println("6. Listar secuaces libres");
        System.out.println("7. Consultar estado de cuentas");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opcion: ");
    }
}