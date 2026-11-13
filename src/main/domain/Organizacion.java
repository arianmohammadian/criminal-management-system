package domain;

/**
 * Clase Organizacion
 * 
 * Representa el centro de "el Sindicato".
 * Se encarga de gestionar el nombre del sindicato, el control de los fondos globales
 * y el porcentaje de comisión que se reserva el Don de los botines de las misiones.
 * 
 * @author Arian Mohammadian
 */
public class Organizacion {
    private String nombre_;
    private double comisionDon_;
    private double fondos_;

    /**
     * Constructor por defecto de la organización.
     * 
     * Inicializa el nombre como "Sindicato Criminal", la comisión del Don al 20.0%
     * y los fondos iniciales en 0.0.
     * 
     */
    public Organizacion() {
        nombre_ = "Sindicato Criminal";
        comisionDon_ = 20.0;
        fondos_ = 0.0;
    }

    /**
     * Constructor parametrizado de la organización.
     * 
     * @param nombre Nombre de la organización criminal.
     * @param comision Porcentaje de comisión del Don (entre 0.0 y 100.0).
     * @param fondosIniciales Fondos económicos iniciales de la organización.
     * @throws IllegalArgumentException Si alguno de los parámetros no cumple las precondiciones.
     */
    public Organizacion(String nombre, double comision, double fondosIniciales) {
        setNombre(nombre);
        setComisionDon(comision);
        setFondos(fondosIniciales);
    }

    /**
     * Intenta descontar dinero de los fondos de la organización.
     * 
     * @param dinero Cantidad a descontar de la caja global.
     * @return true si se ha podido realizar el descuento de fondos, false si no se ha podido.
     * @throws IllegalArgumentException Si dinero es menor que 0.0.
     */
    public boolean descontarFondos(double dinero) {
        if (dinero < 0.0) {
            throw new IllegalArgumentException("El dinero a descontar no puede ser negativo: " + dinero);
        }
        if (fondos_ >= dinero) {
            fondos_ = fondos_ - dinero;
            return true;
        }
        return false;
    }

    /**
     * Incrementa la cantidad de fondos de la organización.
     * 
     * @param dinero Cantidad de dinero a ingresar.
     * @throws IllegalArgumentException Si dinero es menor que 0.0.
     */
    public void añadirFondos(double dinero) {
        if (dinero < 0.0) {
            throw new IllegalArgumentException("El dinero a añadir no puede ser negativo: " + dinero);
        }
        fondos_ = fondos_ + dinero;
    }

    /**
     * Obtiene el nombre de la organización.
     * 
     * @return Nombre de la organización.
     */
    public String getNombre() {
        return nombre_;
    }

    /**
     * Actualiza el nombre de la organización.
     * 
     * @param n Nuevo nombre para la organización.
     * @throws IllegalArgumentException Si el nombre recibido es nulo o está vacío.
     */
    public void setNombre(String n) {
        if (n == null || n.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la organización no puede ser null ni estar vacío");
        }
        nombre_ = n.trim();
    }

    /**
     * Obtiene el porcentaje de comisión configurado para el Don.
     * 
     * @return Porcentaje de comisión del Don.
     */
    public double getComision() {
        return comisionDon_;
    }

    /**
     * Actualiza el porcentaje de comisión que se reserva el Don.
     * 
     * @param n Nuevo valor de comisión (en porcentaje).
     * @throws IllegalArgumentException Si n es menor que 0.0 o mayor que 100.0.
     */
    public void setComisionDon(double n) {
        if (n < 0.0 || n > 100.0) {
            throw new IllegalArgumentException("La comisión del don debe ser un número entre 0 y 100, valor recibido: " + n);
        }
        comisionDon_ = n;
    }

    /**
     * Obtiene los fondos totales disponibles de la organización.
     * 
     * @return Fondos totales disponibles.
     */
    public double getFondos() {
        return fondos_;
    }

    /**
     * Asigna directamente una nueva cantidad a los fondos globales.
     * 
     * @param fondos Nuevo total de fondos para la organización.
     * @throws IllegalArgumentException Si fondos es menor que 0.0.
     */
    public void setFondos(double fondos) {
        if (fondos < 0.0) {
            throw new IllegalArgumentException("Los fondos de la organización no pueden ser negativos: " + fondos);
        }
        fondos_ = fondos;
    }
}