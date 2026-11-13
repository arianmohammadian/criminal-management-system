
package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CtrlPersistencia
 *
 * Controlador encargado de gestionar la lectura y escritura de ficheros
 * de texto utilizados para almacenar informacion del sistema.
 *
 * Diferencia entre los formatos de misiones y secuaces, delegando
 * las operaciones de carga y exportacion en los componentes correspondientes.
 *
 * @author Arian Mohammadian
 */
public class CtrlPersistencia {
    private static CtrlPersistencia instancia_;
    private GestionarSecuaces gestionarSecuaces_;
    private GestionarMisiones gestionarMisiones_;
    private List<String> ultimosDatos_;

    //CONSTRUCTORES

    /**
     * Crea un controlador de persistencia e inicializa los componentes
     * encargados de gestionar los ficheros de misiones y secuaces.
     *
     * Inicialmente, no contiene datos cargados.
     */
    public CtrlPersistencia() {
        gestionarSecuaces_ = new GestionarSecuaces();
        gestionarMisiones_ = new GestionarMisiones();
        ultimosDatos_ = new ArrayList<>();
    }

    /**
     * Obtiene la instancia compartida del controlador de persistencia.
     *
     * Si todavia no existe, crea una nueva instancia antes de devolverla.
     *
     * @return Instancia compartida del controlador de persistencia.
     */
    public static CtrlPersistencia getInstance() {
        if (instancia_ == null) {
            instancia_ = new CtrlPersistencia();
        }
        return instancia_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Carga los datos almacenados en un fichero de texto.
     *
     * Determina si el fichero contiene misiones o secuaces examinando
     * la primera linea no vacia. Si esta contiene un corchete de apertura,
     * interpreta el fichero como datos de misiones; en caso contrario,
     * lo interpreta como datos de secuaces.
     *
     * Guarda las lineas cargadas como los ultimos datos gestionados
     * por el controlador.
     *
     * @param ruta Ruta del fichero del que se desean cargar los datos.
     * @return Lista de lineas no vacias obtenidas del fichero.
     * @throws IllegalArgumentException Si la ruta es nula o vacia,
     *         o alguna linea no cumple el formato esperado.
     * @throws FileNotFoundException Si el fichero indicado no existe
     *         o no puede abrirse para su lectura.
     */
    public List<String> cargarDatosFichero(String ruta)throws FileNotFoundException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del fichero no puede estar vacia ni ser nula");
        }
        File archivo =new File(ruta);
        boolean esDeMisiones = false;
        //creo el scaner para leer el texto
        Scanner scaner = new Scanner(archivo);
        while (scaner.hasNextLine()) {
            String linea = scaner.nextLine().trim();
            if (!linea.isEmpty()) {
                //las misiones siempre llevan corchetes
                if (linea.contains("[")) {
                    esDeMisiones = true;
                }
                break;
            }
        }
        scaner.close();
        if (esDeMisiones) {
            ultimosDatos_ =gestionarMisiones_.cargarMisiones(ruta);
        }
        else {
            ultimosDatos_ =gestionarSecuaces_.cargarSecuaces(ruta);
        }
        return ultimosDatos_;
    }

    /**
     * Exporta a un fichero los ultimos datos cargados por el controlador.
     *
     * Utiliza los datos almacenados internamente y delega su escritura
     * en el componente correspondiente al formato detectado.
     *
     * @param ruta Ruta del fichero en el que se desean guardar los datos.
     * @throws IllegalArgumentException Si la ruta es nula o vacia.
     * @throws FileNotFoundException Si no puede abrirse el fichero
     *         para su escritura.
     */
    public void exportarDatosFichero(String ruta) throws FileNotFoundException {
        exportarDatosFichero(ruta, ultimosDatos_);
    }

    /**
     * Exporta una lista de lineas a un fichero de texto.
     *
     * Determina el tipo de datos examinando la primera linea de la lista.
     * Si contiene un corchete de apertura, utiliza el formato de misiones;
     * en caso contrario, utiliza el formato de secuaces.
     *
     * Si la lista esta vacia, se utiliza el componente de secuaces.
     * La operacion escribe las lineas proporcionadas en el fichero indicado.
     *
     * @param ruta Ruta del fichero en el que se desean guardar los datos.
     * @param datos Lista de lineas que se desean exportar.
     * @throws IllegalArgumentException Si la ruta es nula o vacia,
     *         o la lista de datos es nula.
     * @throws FileNotFoundException Si no puede abrirse el fichero
     *         para su escritura.
     */
    public void exportarDatosFichero(String ruta, List<String> datos) throws FileNotFoundException {
        if (ruta ==null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del fichero no puede estar vacia ni ser null");
        }
        if (datos== null) {
            throw new IllegalArgumentException("Los datos no pueden ser null");
        }
        if (!datos.isEmpty() && datos.get(0).contains("[")) { //Las misiones incluyen [
            gestionarMisiones_.guardarMisiones(ruta, datos);
        }
        else {
            gestionarSecuaces_.guardarSecuaces(ruta, datos);
        }
    }

    //me creo esta clase privada para cargar y guardar en archivo secuaces
    private static class GestionarSecuaces {

        public List<String> cargarSecuaces(String camino) throws FileNotFoundException {
            List<String> resultadoo = new ArrayList<>();
            File archivo = new File(camino);
            Scanner scan = new Scanner(archivo);
            while (scan.hasNextLine()) {
                String linea = scan.nextLine().trim();
                if (!linea.isEmpty()) {
                    //esperamos que venga minimo el nombre y rol[]
                    String[] partes = linea.split(" ");
                    if (partes.length < 2) {
                        scan.close();
                        throw new IllegalArgumentException("El formato de el secuaz es incorrecto en la linea: " + linea);
                    }
                    resultadoo.add(linea);
                }
            }
            scan.close();
            return resultadoo;
        }

        public void guardarSecuaces(String camino, List<String> datos)throws FileNotFoundException {
            File archiv = new File(camino);
            try (PrintWriter escribir = new PrintWriter(archiv)) {
                for (int i= 0;i < datos.size(); i++) {
                    escribir.println(datos.get(i));
                }
            }
        }
    }

    //me creo esta clase privada par cargar y guardar en archivo misiones
    private static class GestionarMisiones {

        public List<String> cargarMisiones(String camino) throws FileNotFoundException {
            List<String> resultadoo= new ArrayList<>();
            File archivo= new File(camino);
            Scanner scan = new Scanner(archivo);

            while (scan.hasNextLine()) {
                String linea = scan.nextLine().trim();
                if (!linea.isEmpty()) {
                    //Miro que haya corechetes porque el formato esperado es: Nombre botin [ROL x] k [...][...]
                    if (!linea.contains("[") || !linea.contains("]")) {
                        scan.close();
                        throw new IllegalArgumentException("El formato de la mision es incorrecto en la linea: " + linea);
                    }
                    resultadoo.add(linea);
                }
            }
            scan.close();
            return resultadoo;
        }

        public void guardarMisiones(String camino, List<String> datos)throws FileNotFoundException {
            File archivo =new File(camino);
            try (PrintWriter escribir = new PrintWriter(archivo)) {
                for (int i = 0; i < datos.size(); i++) {
                    escribir.println(datos.get(i));
                }
            }
        }
    }
}
