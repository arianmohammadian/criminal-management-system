package test.domain;

import domain.Organizacion;

import java.util.Scanner;
import java.util.NoSuchElementException;

/**
 * DriverOrganizacion
 *
 * Driver para probar las funcionalidades de Organizacion.
 * 
 * 
 * @author Arian Mohammadian
 */
public class DriverOrganizacion {

    private static Organizacion org_;

    public static void main(String[] args) {

        Scanner scaner =new Scanner(System.in); //creo el scanner para leer terminal

        //creo la organizacion por defecto
        org_ = new Organizacion();
        int opcion_a_escoger = -756;
        while (opcion_a_escoger != 0) {
            enseñarOpciones();

            //Hay que poner numero entero
            if (!scaner.hasNextInt()) {
                System.out.println("Error: pon un numero del 0 al 6 entero");
                scaner.nextLine();
                continue;
            }
            opcion_a_escoger = scaner.nextInt();
            scaner.nextLine(); //paso a siguiente linea y limpio buffer

            try {
                switch (opcion_a_escoger) {
                    case 0:
                        System.out.println("Salimos de la prueba");
                        break;
                    case 1: {
                        System.out.println("\n DATOS ORGANIZACION");
                        System.out.println("Nombre: " + org_.getNombre());
                        System.out.println("Comision Don: "+ org_.getComision() +"%");
                        System.out.println("Fondos actuales: "+ org_.getFondos()+"€");
                        break;
                    }
                    case 2: {
                        System.out.print("Nuevo nombre: ");
                        String nom = scaner.nextLine(); //leo nombre que se escriba en terminal
                        org_.setNombre(nom);
                        System.out.println("Nombre cambiado a: " + org_.getNombre());
                        break;
                    }
                    case 3: {
                        System.out.print("Nueva comision (0 a 100): ");
                        if (!scaner.hasNextDouble()) {
                            System.out.println("Error: pon un numero decimal");
                            scaner.nextLine();
                            break;
                        }
                        double comi = scaner.nextDouble();
                        scaner.nextLine();
                        org_.setComisionDon(comi);
                        System.out.println("Comision cambiada a: " +org_.getComision() +"%");
                        break;
                    }
                    case 4: {
                        System.out.print("Cantidad a ingresar: ");
                        if (!scaner.hasNextDouble()) {
                            System.out.println("Error: pon un numero decimal");
                            scaner.nextLine();
                            break;
                        }
                        double dinero =scaner.nextDouble();
                        scaner.nextLine();
                        org_.añadirFondos(dinero);
                        System.out.println("Fondos actualizados: " + org_.getFondos() + "€");
                        break;
                    }
                    case 5: {
                        System.out.print("Cantidad a descontar: ");
                        if (!scaner.hasNextDouble()) {
                            System.out.println("Error: pon un numero decimal");
                            scaner.nextLine();
                            break;
                        }
                        double dinero = scaner.nextDouble();
                        scaner.nextLine();
                        //compruevo si es posible
                        if (org_.descontarFondos(dinero)) {
                            System.out.println("Descuento realizado. Quedan: " + org_.getFondos() + "€");
                        } else {
                            System.out.println("No hay dinero suficiente. Fondos actuales: " + org_.getFondos() + "€");
                        }
                        break;
                    }
                    case 6: { 
                        cargamosDatitosDePrueba();
                        System.out.println("Datos cargados ");
                        break;
                    }

                    default: //caso descarte
                        System.out.println("Ópcion no válida, escribe un número entre 0 y 6");
                        break;
                }
            } 
            catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
                System.out.println("Error: "+ e.getMessage());
            }
        }
        scaner.close();
    }

    //Añadimos los datos que queramos comprovar
    private static void cargamosDatitosDePrueba() {
        org_ = new Organizacion("Sindicato de Barcelona", 25.0, 150000.0);
    }

    private static void enseñarOpciones() {
        System.out.println("\n      MENU ORGANIZACION ");
        System.out.println("1. Consultar estado organizacion");
        System.out.println("2. Modificar nombre");
        System.out.println("3. Modificar comision Don");
        System.out.println("4. Añadir fondos");
        System.out.println("5. Descontar fondos");
        System.out.println("6. Cargar datos de prueba");
        System.out.println("0. Salir");
        System.out.println("Escribe el numero de la opcion que quieres usar");
        System.out.print("Opcion: ");
    }
}