package test.controllers;

import controllers.CtrlPersistencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
- DriverCtrlPersistencia
 *
- Driver para probar el funcionamiento de CtrlPersistencia.
 *
@author Arian Mohammadian
 */
public class DriverCtrlPersistencia {

    private static CtrlPersistencia ctrl_;
    public static void main(String[] args) {
        Scanner scaner =new Scanner(System.in);
        ctrl_= CtrlPersistencia.getInstance();
        int opcion_a_escoger= -85483;
        while (opcion_a_escoger!= 0) {
            enseñarOpciones();
            if (!scaner.hasNextInt()) {
                System.out.println("Error: pon un numero entero entre el 0 y el 4");
                scaner.nextLine();
                continue;
            }
            opcion_a_escoger = scaner.nextInt();
            scaner.nextLine(); // limpio el buffer

            try {
                switch (opcion_a_escoger) {
                    case 0:
                        System.out.println("Salimos de la prueba");
                        break;
                    case 1: {
                        System.out.print("Escribe la ruta del fichero a cargar, por ejemplo: secuaces_prueba.txt: ");
                        String ruta = scaner.nextLine();
                        List<String> lineas = ctrl_.cargarDatosFichero(ruta);
                        System.out.println("\nLineas cargadas (" +lineas.size()+ "):");
                        for (int i = 0; i < lineas.size(); i++) {
                            System.out.println((i + 1) + ". " + lineas.get(i));
                        }
                        break;
                    }
                    case 2: {
                        System.out.print("Escribe la ruta donde exportar los ultimos datos, por ejemplo: salida.txt: ");
                        String ruta =scaner.nextLine();
                        ctrl_.exportarDatosFichero(ruta);
                        System.out.println("Ultimos datos exportados correctamente a "+ruta);
                        break;
                    }
                    case 3: {
                        System.out.print("Escribe la ruta del fichero de salida: ");
                        String ruta =scaner.nextLine();
                        List<String> nuevos_datos =new ArrayList<>();
                        System.out.println("Escribe las lineas que quieras guardar (escribe FIN para terminar):");
                        while (true) {
                            String linea= scaner.nextLine();
                            if (linea.equals("FIN")) {
                                break;
                            }
                            nuevos_datos.add(linea);
                        }
                        ctrl_.exportarDatosFichero(ruta, nuevos_datos);
                        System.out.println("Lista exportada correctamente a "+ruta);
                        break;
                    }
                    case 4: {
                        crearFicherosDePrueba();
                        System.out.println("Ficheros 'secuaces_prueba.txt' y 'misiones_prueba.txt' creados.");
                        break;
                    }
                    default:
                        System.out.println("Opcion no valida, escribe un numero entre 0 y 4");
                        break;
                } 
                //cogemos excepciones
            } 
            catch(FileNotFoundException e) {
                System.out.println("Error de fichero: no se ha encontrado la ruta  (" + e.getMessage() + ")");
            } 
            catch(IllegalArgumentException e) {
                System.out.println("Error de formato o parametro: " + e.getMessage());
            }
        }
        scaner.close();
    }

    //para probar genero dos ficheros de prueba con el formato correcto
    private static void crearFicherosDePrueba() throws FileNotFoundException {
        File fich=new File("secuaces_prueba.txt");
        PrintWriter escritor =new PrintWriter(fich);
        escritor.println("Vinicius CONDUCTOR");
        escritor.println("Marc ASALTO");
        escritor.println("Marta HACKER");
        escritor.println("Joan CERRAJERO");
        escritor.close();

        File fichMision = new File("misiones_prueba.txt");
        PrintWriter escritorMisi = new PrintWriter(fichMision);
        escritorMisi.println("Robo_Casa_SAD 20000 [Conductor 1 Asalto 2] 10 []");
        escritorMisi.println("Infiltración_casa_Arian 10000 [Conductor 1 Hacker 1] 5 []");
        escritorMisi.println("Infiltracion_TiendaJD 5000 [Conductor 1 Negociador 1 Asalto 1] 15 [Asalto_Furgon Robo_Licoreria]");
        escritorMisi.close();
    }

    private static void enseñarOpciones() {
        System.out.println("\n      MENU PERSISTENCIA ");
        System.out.println("1. Cargar datos desde fichero");
        System.out.println("2. Exportar ultimos datos cargados a fichero");
        System.out.println("3. Exportar lista personalizada a fichero");
        System.out.println("4. Generar ficheros de prueba (secuaces y misiones)");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }
}