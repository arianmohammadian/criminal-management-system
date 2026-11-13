
package domain;

import java.util.ArrayList;
import java.util.List;

import domain.state.EstadoDisponible;
import domain.state.EstadoMision;

/**
 * Mision
 *
 * Representa una mision de la organizacion criminal. Gestiona su identificador,
 * botin, probabilidad de fracaso, requisitos de roles y estado actual.
 *
 * Permite modificar sus caracteristicas, consultar su disponibilidad y
 * actualizar su estado mediante el patron State.
 * 
 * @author Arian Mohammadian
 */
public class Mision {
    /** Identificador de la mision. */
    protected String id;

    /** Botin economico asociado a la mision. */
    protected double botin;

    /** Probabilidad de fracaso de la mision, expresada en porcentaje. */
    protected double probFracaso;

    /** Numero de fracasos acumulados por la mision. */
    protected int numFracasos;

    /** Lista de requisitos de rol necesarios para ejecutar la mision. */
    protected List<RequisitoRol> requisitos;

    /** Estado actual de la mision, gestionado mediante el patron State. */
    protected EstadoMision estadoActual;

    /**
     * Crea una nueva mision con los datos y requisitos indicados.
     * Inicialmente, la mision esta disponible y no acumula fracasos.
     *
     * @param id Identificador de la mision.
     * @param botin Cantidad de botin asociada a la mision.
     * @param probFracaso Probabilidad de fracaso expresada en porcentaje.
     * @param requisitosIniciales Lista inicial de requisitos de rol.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         el botin es negativo, la probabilidad esta fuera del intervalo
     *         [0, 100] o no existe al menos un requisito de rol valido.
     */
    public Mision(String id, double botin, double probFracaso, List<RequisitoRol> requisitosIniciales) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la mision no puede ser nulo o estar vacio");
        }
        if (botin < 0.0) {
            throw new IllegalArgumentException("El botin debe ser mayor o igual que 0");
        }
        if (probFracaso < 0.0 || probFracaso > 100.0) {
            throw new IllegalArgumentException("La probabilidad de fracaso debe estar entre [0-100]");
        }
        if (requisitosIniciales == null || requisitosIniciales.isEmpty()) {
            throw new IllegalArgumentException("La mision debe tener al menos un requisito de rol");
        }

        this.id = id;
        this.botin = botin;
        this.probFracaso = probFracaso;
        this.numFracasos = 0;
        this.requisitos = new ArrayList<>();

        for (RequisitoRol req : requisitosIniciales) {
            añadirRequisito(req);
        }

        if (this.requisitos.isEmpty()) {
            throw new IllegalArgumentException("La misión debe tener al menos un requisito de rol válido");
        }

        this.estadoActual = new EstadoDisponible();
    }

    /**
     * Obtiene el identificador de la mision.
     *
     * @return Identificador de la mision.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el botin asociado a la mision.
     *
     * @return Cantidad de botin de la mision.
     */
    public double getBotin() {
        return botin;
    }

    /**
     * Modifica la cantidad de botin asociada a la mision.
     *
     * @param botin Nueva cantidad de botin.
     * @throws IllegalArgumentException Si el botin es negativo.
     */
    public void setBotin(double botin) {
        if (botin < 0.0) throw new IllegalArgumentException("Botin debe ser un valor mayor a 0");
        this.botin = botin;
    }

    /**
     * Obtiene la probabilidad de fracaso de la mision.
     *
     * @return Probabilidad de fracaso expresada en porcentaje.
     */
    public double getProbFracaso() {
        return probFracaso;
    }

    /**
     * Modifica la probabilidad de fracaso de la mision.
     *
     * @param probFracaso Nueva probabilidad de fracaso, entre 0 y 100.
     * @throws IllegalArgumentException Si la probabilidad esta fuera
     *         del intervalo [0, 100].
     */
    public void setProbFracaso(double probFracaso) {
        if (probFracaso < 0.0 || probFracaso > 100.0) throw new IllegalArgumentException("La probabilidad de fracaso debe estar entre 0 y 100");
        this.probFracaso = probFracaso;
    }

    /**
     * Obtiene el numero de fracasos acumulados por la mision.
     *
     * @return Numero de fracasos registrados.
     */
    public int getNumFracasos() {
        return numFracasos;
    }

    /**
     * Obtiene el estado actual de la mision.
     *
     * @return Objeto que representa el estado actual de la mision.
     */
    public EstadoMision getEstadoActual() {
        return estadoActual;
    }

    /**
     * Obtiene los requisitos de rol de la mision.
     *
     * @return Nueva lista con los requisitos de rol registrados.
     */
    public List<RequisitoRol> getRequisitos() {
        return new ArrayList<>(requisitos);
    }

    /**
     * Calcula el numero total de secuaces necesarios para ejecutar la mision.
     * Suma las cantidades requeridas de todos los roles.
     *
     * @return Cantidad total de secuaces requeridos.
     */
    public int getCantidadTotalRequerida() {
        int total = 0;
        for (RequisitoRol r : requisitos) {
            total += r.getCantidadRequerida();
        }
        return total;
    }

    /**
     * Anade un requisito de rol a la mision.
     * Si ya existe un requisito para el mismo rol, incrementa su cantidad
     * requerida en lugar de crear otro requisito independiente.
     *
     * @param req Requisito de rol que se desea incorporar.
     * @throws IllegalArgumentException Si el requisito es nulo,
     *         su rol es nulo o la cantidad requerida no es positiva.
     */
    public void añadirRequisito(RequisitoRol req) {
      if (req == null) {
            throw new IllegalArgumentException("El requisito no puede ser nulo");
        }
        if (req.getTipoRol() == null || req.getCantidadRequerida() <= 0) {
            throw new IllegalArgumentException("El requisito de rol debe tener un rol válido y cantidad mayor que 0");
        }
        for (RequisitoRol r : requisitos) {
            if (r.getTipoRol() == req.getTipoRol()) {
                r.setCantidadRequerida(r.getCantidadRequerida() + req.getCantidadRequerida());
                return;
            }
        }
        this.requisitos.add(req);
    }

    /**
     * Comprueba si la mision puede ejecutarse en su estado actual.
     *
     * @return true si la mision es ejecutable; false en caso contrario.
     */
    public boolean esEjecutable() {
        return estadoActual.esEjecutable();
    }

    /**
     * Notifica que la mision se ha completado.
     * Delega en el estado actual la gestion de la transicion correspondiente.
     */
    public void alCompletar() {
        estadoActual.alCompletar(this);
    }

    /**
     * Notifica un intento fallido de ejecutar la mision.
     * Si la mision es ejecutable, incrementa el contador de fracasos
     * y delega en el estado actual la gestion de la transicion.
     */
    public void alFracasar() {
        if (esEjecutable()) {
            ++numFracasos;
            estadoActual.alFracasar(this);
        }
    }

    /**
     * Obtiene el nombre del estado actual de la mision.
     *
     * @return Nombre del estado actual.
     */
    public String getNombreEstado() {
        return estadoActual.getNombreEstado();
    }

    /**
     * Modifica el estado actual de la mision.
     *
     * @param nuevoEstado Nuevo estado que se desea establecer.
     * @throws IllegalArgumentException Si el nuevo estado es nulo.
     */
    public void cambiarEstado(EstadoMision nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El estado de la mision no puede ser nulo");
        estadoActual = nuevoEstado;
    }
}
