package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import domain.enums.EstadoSecuaz;

/**
 * Prision
 *
 * Gestiona los secuaces encarcelados, el nivel de seguridad de la prision,
 * el estado de alerta maxima y las operaciones de rescate, fianza y soborno.
 */
public class Prision {
    private static final int SEGURIDAD_DEFECTO = 5;
    private static final double FIANZA_BASE_DEFECTO = 1000.0;
    private static final int UMBRAL_PRISIONEROS_ALERTA = 3;
    private static final int UMBRAL_SEGURIDAD_ALERTA = 8;

    private final Map<String, Secuaz> prisioneros;
    private int nivelSeguridad;                     //Rango [1-10]
    private double fianzaBase;                      // > 0
    private boolean alertaMaxima;                   //3 Prisionero || nivelSeguridad >= 8

    // CONSTRUCTORES

    /**
     * Crea una prision vacia con el nivel de seguridad y la fianza base
     * establecidos por defecto.
     */
    public Prision() {
        this(SEGURIDAD_DEFECTO, FIANZA_BASE_DEFECTO);
    }

    /**
     * Crea una prision vacia con el nivel de seguridad y la fianza base
     * indicados. Inicializa el estado de alerta maxima.
     *
     * @param nivelSeguridad Nivel de seguridad inicial, entre 1 y 10.
     * @param fianzaBase Importe base utilizado para calcular las fianzas.
     * @throws IllegalArgumentException Si el nivel de seguridad esta fuera
     *         del intervalo [1, 10] o la fianza base no es positiva.
     */
    public Prision(int nivelSeguridad, double fianzaBase) {
        if (nivelSeguridad < 1 || nivelSeguridad > 10) {
            throw new IllegalArgumentException( "El nivel de seguridad debe estar entre 1 y 10");
        }

        if (fianzaBase <= 0) {
            throw new IllegalArgumentException("La fianza base debe ser mayor que 0");
        }

        this.prisioneros = new HashMap<>();
        this.nivelSeguridad = nivelSeguridad;
        this.fianzaBase = fianzaBase;
        this.alertaMaxima = false;
        comprobarEstadoAlerta();
    }

    // ----------

    // GETTERS

    /**
     * Obtiene el nivel de seguridad actual de la prision.
     *
     * @return Nivel de seguridad de la prision, entre 1 y 10.
     */
    public int getNivelSeguridad() {
        return nivelSeguridad;
    }

    /**
     * Obtiene el importe base utilizado para calcular las fianzas.
     *
     * @return Importe de la fianza base.
     */
    public double getFianzaBase() {
        return fianzaBase;
    }

    /**
     * Comprueba si la prision se encuentra en estado de alerta maxima.
     *
     * @return true si la alerta maxima esta activada; false en caso contrario.
     */
    public boolean isAlertaMaxima() {
        return alertaMaxima;
    }

    // ----------

    /**
     * Encarcela a un secuaz y lo incorpora al registro de prisioneros.
     * Si el secuaz no esta encarcelado, actualiza su estado a prisionero.
     * Despues de incorporarlo, recalcula el estado de alerta maxima.
     *
     * @param s Secuaz que se desea encarcelar.
     * @throws IllegalArgumentException Si el secuaz es nulo o su
     *         identificador es nulo o vacio.
     * @throws IllegalStateException Si ya existe un prisionero registrado
     *         con el mismo identificador.
     */
    public void encarcelar(Secuaz s) {
        if (s == null) throw new IllegalArgumentException( "El secuaz a encarcelar no puede ser nulo" );
        String id = s.getId();

        if (id == null || id.isBlank()) throw new IllegalArgumentException("El secuaz debe tener un ID válido");
        if (prisioneros.containsKey(id))  throw new IllegalStateException( "El secuaz con ID " + id + " ya se encuentra en prisión");


        if (s.getEstado() != EstadoSecuaz.PRISIONERO) s.encarcelar();
        prisioneros.put(id, s);
        comprobarEstadoAlerta();
    }

    /**
     * Libera a un secuaz del registro de prisioneros.
     * Si el secuaz se encuentra en estado prisionero, cambia su estado a libre
     * y recalcula el estado de alerta maxima.
     *
     * @param idSecuaz Identificador del secuaz que se desea liberar.
     * @return Secuaz que ha sido liberado de la prision.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio.
     * @throws NoSuchElementException Si el secuaz no se encuentra registrado
     *         en la prision.
     */
    public Secuaz liberar(String idSecuaz) {
        if (idSecuaz == null || idSecuaz.isBlank()) throw new IllegalArgumentException("El ID del secuaz no puede ser nulo o estar vacío");

        Secuaz secuaz = prisioneros.remove(idSecuaz);
        if (secuaz == null) throw new NoSuchElementException("El secuaz con ID " + idSecuaz + " no está en prisión");

        if (secuaz.getEstado() == EstadoSecuaz.PRISIONERO) secuaz.rescatarDePrision();
        comprobarEstadoAlerta();
        return secuaz;
    }

    /**
     * Intenta liberar a un secuaz mediante el pago de una fianza.
     * El coste depende de la fianza base, el nivel de seguridad y
     * la activacion de la alerta maxima.
     *
     * Si la organizacion dispone de fondos suficientes, descuenta
     * el importe correspondiente y libera al secuaz.
     *
     * @param idSecuaz Identificador del secuaz que se desea liberar.
     * @param org Organizacion que realiza el pago de la fianza.
     * @return true si se paga la fianza y se libera al secuaz;
     *         false si la organizacion no dispone de fondos suficientes.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio
     *         o la organizacion es nula.
     * @throws NoSuchElementException Si el secuaz no se encuentra
     *         registrado en la prision.
     */
    public boolean pagarFianza(String idSecuaz, Organizacion org) {
        if (idSecuaz == null || idSecuaz.isBlank()) throw new IllegalArgumentException( "El ID del secuaz no puede ser nulo o estar vacío");
        if (org == null) throw new IllegalArgumentException("La organización no puede ser nula");
        if (!prisioneros.containsKey(idSecuaz)) throw new NoSuchElementException("El secuaz con ID " + idSecuaz + " no está en prisión");


        double multiplicadorAlerta = alertaMaxima ? 1.5 : 1.0;
        double costeFianza = fianzaBase * (1.0 + (nivelSeguridad - 1) * 0.15) * multiplicadorAlerta;

        if (!org.descontarFondos(costeFianza)) return false;
        liberar(idSecuaz);
        return true;
    }

    /**
     * Intenta reducir el nivel de seguridad de la prision mediante un soborno.
     * El importe se descuenta de los fondos de la organizacion y la reduccion
     * de seguridad depende de la relacion entre el monto y la fianza base.
     *
     * El nivel de seguridad nunca puede reducirse por debajo de 1.
     * Tras realizar el soborno, se recalcula el estado de alerta maxima.
     *
     * @param monto Cantidad de dinero destinada al soborno.
     * @param org Organizacion que financia el soborno.
     * @throws IllegalArgumentException Si el monto no es positivo
     *         o la organizacion es nula.
     * @throws IllegalStateException Si el nivel de seguridad ya es minimo
     *         o la organizacion no dispone de fondos suficientes.
     */
    public void sobornarGuardias(double monto, Organizacion org) {
        if (monto <= 0) throw new IllegalArgumentException("El monto del soborno debe ser mayor que 0");
        if (org == null) throw new IllegalArgumentException("La organización no puede ser nula");
        if (this.nivelSeguridad <= 1) throw new IllegalStateException("El nivel de seguridad ya está en el mínimo posible (1).");
        if (!org.descontarFondos(monto)) throw new IllegalStateException("La organización no dispone de fondos suficientes para realizar el soborno");

        int reduccionSeguridad = Math.max(1, (int) (monto / fianzaBase));
        nivelSeguridad = Math.max(1, nivelSeguridad - reduccionSeguridad);
        comprobarEstadoAlerta();
    }

    /**
     * Calcula la dificultad de rescatar a un secuaz de la prision.
     * Tiene en cuenta el nivel de seguridad, la cantidad de prisioneros
     * y la activacion de la alerta maxima.
     *
     * @return Dificultad de rescate expresada en una escala de 0 a 100.
     */
    public double calcularDificultadRescate() {
        double baseSeguridad = nivelSeguridad * 6.0;
        double factorPrisioneros = prisioneros.size() * 4.0;
        double bonusAlerta = alertaMaxima ? 20.0 : 0.0;

        double dificultad = baseSeguridad + factorPrisioneros + bonusAlerta;
        return Math.min(100.0, Math.max(0.0, dificultad));
    }

    /**
     * Recalcula el estado de alerta maxima de la prision.
     * La alerta se activa cuando hay al menos tres prisioneros
     * o cuando el nivel de seguridad es igual o superior a 8.
     */
    public void comprobarEstadoAlerta() {
        alertaMaxima = (prisioneros.size() >= UMBRAL_PRISIONEROS_ALERTA || nivelSeguridad >= UMBRAL_SEGURIDAD_ALERTA);
    }

    /**
     * Obtiene una lista con todos los secuaces registrados en la prision.
     *
     * @return Nueva lista que contiene los secuaces encarcelados.
     */
    public List<Secuaz> obtenerPrisioneros() {
        return new ArrayList<>(prisioneros.values());
    }
}
