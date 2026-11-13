package domain;

import java.util.ArrayList;
import java.util.List;
import domain.enums.TipoRol;

/**
 * EjecucionMision
 *
 * Representa la ejecucion de una mision a cargo de un equipo de secuaces
 * asignados. Gestiona el resultado de la ejecucion, calcula la probabilidad
 * de exito y el reparto del botin, y actualiza la disponibilidad de los secuaces.
 *
 * @author Arian Mohammadian
 */
public class EjecucionMision {
    private final String idEjecucion_;
    private boolean exito_;
    private final double probExitoCalculada_;
    private double botinObtenido_;

    private final List<AsignacionRol> equipo_;
    private final Mision mision_;

    private boolean finalizada_;

    // CONSTRUCTOR

    /**
     * Crea una nueva ejecucion asociada a una mision y a un equipo de secuaces.
     * Inicialmente, la ejecucion no esta finalizada, no tiene exito registrado
     * y su botin obtenido es cero.
     *
     * Calcula la probabilidad de exito a partir de la mision y del equipo.
     *
     * @param idEjecucion Identificador de la ejecucion.
     * @param mision Mision que se desea ejecutar.
     * @param equipo Lista de asignaciones de roles de los secuaces participantes.
     * @throws IllegalArgumentException Si el identificador es nulo o vacio,
     *         la mision es nula o el equipo es nulo o esta vacio.
     */
    public EjecucionMision(String idEjecucion, Mision mision, List<AsignacionRol> equipo) {
        if (idEjecucion == null || idEjecucion.isBlank()) {
            throw new IllegalArgumentException(
                "El ID de la ejecución no puede estar vacío"
            );
        }

        if (mision == null) {
            throw new IllegalArgumentException(
                "La misión no puede ser nula"
            );
        }

        if (equipo == null || equipo.isEmpty()) {
            throw new IllegalArgumentException(
                "El equipo no puede ser nulo y no puede estar vacío"
            );
        }

        idEjecucion_ = idEjecucion;
        mision_ = mision;
        equipo_ = new ArrayList<>(equipo);
        exito_ = false;
        probExitoCalculada_ = calcularProbabilidadExito(mision_, equipo_);
        botinObtenido_ = 0.0;
        finalizada_ = false;
    }

    // ----------

    // GETTERS CLASE

    /**
     * Obtiene el identificador de la ejecucion.
     *
     * @return Identificador de la ejecucion.
     */
    public String getIdEjecucion() {
        return idEjecucion_;
    }

    /**
     * Consulta si la ejecucion ha sido exitosa.
     *
     * @return true si se ha registrado un resultado exitoso;
     *         false en caso contrario.
     */
    public boolean isExito() {
        return exito_;
    }

    /**
     * Obtiene la probabilidad de exito calculada al crear la ejecucion.
     *
     * @return Probabilidad de exito expresada como valor entre 0 y 1.
     */
    public double getProbExitoCalculada() {
        return probExitoCalculada_;
    }

    /**
     * Obtiene el botin registrado como resultado de la ejecucion.
     *
     * @return Cantidad de botin obtenida, o cero si la ejecucion
     *         no ha tenido exito o todavia no ha finalizado.
     */
    public double getBotinObtenido() {
        return botinObtenido_;
    }

    /**
     * Obtiene la mision asociada a la ejecucion.
     *
     * @return Mision que se esta ejecutando o se ha ejecutado.
     */
    public Mision getMision() {
        return mision_;
    }

    /**
     * Obtiene las asignaciones de roles del equipo participante.
     *
     * @return Nueva lista con las asignaciones de roles de la ejecucion.
     */
    public List<AsignacionRol> getEquipo() {
        return new ArrayList<>(equipo_);
    }

    /**
     * Consulta si la ejecucion ha sido finalizada.
     *
     * @return true si la ejecucion ha finalizado; false en caso contrario.
     */
    public boolean isFinalizada() {
        return finalizada_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    /**
     * Calcula la probabilidad de exito de una mision para un equipo determinado.
     *
     * La probabilidad depende de la proporcion de secuaces cuyo rol base
     * coincide con el rol que desempenan en la mision y de la probabilidad
     * de fracaso de esta. El resultado se limita al intervalo [0, 1].
     *
     * @param mision Mision cuya probabilidad de exito se desea calcular.
     * @param equipo Lista de asignaciones de roles de los secuaces participantes.
     * @return Probabilidad de exito calculada, expresada entre 0 y 1.
     * @throws IllegalArgumentException Si la mision es nula o el equipo
     *         es nulo o esta vacio.
     */
    public static double calcularProbabilidadExito(Mision mision, List<AsignacionRol> equipo) {
        if (mision == null) {
            throw new IllegalArgumentException(
                "La misión no puede ser nula"
            );
        }

        if (equipo == null || equipo.isEmpty()) {
            throw new IllegalArgumentException(
                "El equipo no puede ser nulo y no puede estar vacío"
            );
        }

        int N = equipo.size();
        int m = 0;
        double k = mision.getProbFracaso();

        for (AsignacionRol asig : equipo) {
            Secuaz secuaz = asig.getSecuaz();

            if (secuaz != null && secuaz.getRolBase() == asig.getRolEmpleado()) m++;
        }

        double prob = ((double) m / N) * ((100.0 - k) / 100.0);

        return (prob > 1.0) ? 1.0 : (prob < 0.0 ? 0.0 : prob);
    }

    /**
     * Calcula la cantidad de botin correspondiente a la comision del Don.
     *
     * Si la ejecucion no ha tenido exito o no se ha obtenido botin,
     * devuelve cero.
     *
     * @param comisionDon Porcentaje del botin destinado al Don, entre 0 y 100.
     * @return Cantidad de botin correspondiente a la comision del Don.
     * @throws IllegalArgumentException Si la comision esta fuera
     *         del intervalo [0, 100].
     */
    public double calcularRepartoBotin(double comisionDon) {
        if (comisionDon < 0.0 || comisionDon > 100.0) {
            throw new IllegalArgumentException(
                "La comisión del Don debe ser un valor válido en porcentaje entre 0.0 y 100.0"
            );
        }

        if (!exito_ || botinObtenido_ <= 0.0) return 0.0;

        return botinObtenido_ * (comisionDon / 100.0);
    }

    /**
     * Finaliza la ejecucion de una mision y registra su resultado.
     *
     * Actualiza el estado de la mision segun el resultado obtenido.
     * En caso de exito, distribuye entre los secuaces participantes
     * la parte del botin destinada al equipo, teniendo en cuenta
     * los porcentajes asociados a sus roles base.
     *
     * Finalmente, libera a los secuaces participantes, tanto si la
     * ejecucion ha tenido exito como si ha fracasado.
     *
     * @param exito Indica si la ejecucion ha sido exitosa.
     * @param botin Cantidad de botin obtenida si la ejecucion ha tenido exito.
     * @param comisionDon Porcentaje del botin destinado al Don, entre 0 y 100.
     * @throws IllegalStateException Si la ejecucion ya ha sido finalizada
     *         o si algun secuaz no se encuentra ocupado al intentar liberarlo.
     * @throws IllegalArgumentException Si la ejecucion es exitosa y el botin
     *         es negativo, o si la comision esta fuera del intervalo [0, 100].
     */
    public void finalizarEjecucion(boolean exito, double botin, double comisionDon) {
        if (finalizada_) {
            throw new IllegalStateException(
                "La ejecución con ID " + idEjecucion_ + " ha sido finalizada"
            );
        }

        if (exito && botin < 0.0) {
            throw new IllegalArgumentException(
                "El botín obtenido no puede ser menor que 0.0 si la ejecución es exitosa"
            );
        }

        if (comisionDon < 0.0 || comisionDon > 100.0) {
            throw new IllegalArgumentException(
                "La comisión del Don debe ser un valor válido en porcentaje entre 0.0 y 100.0"
            );
        }

        finalizada_ = true;
        exito_ = exito;
        botinObtenido_ = exito ? botin : 0.0;

        if (mision_ != null) {
            if (exito) mision_.alCompletar();
            else mision_.alFracasar();
        }

        if (equipo_ != null) {
            double botinEquipo = botinObtenido_ * ((100.0 - comisionDon) / 100.0);
            double sumPercentage = 0.0;

            if (exito && botinEquipo > 0.0) {
                for (AsignacionRol asig : equipo_) {
                    if (asig != null && asig.getSecuaz() != null) {
                        TipoRol sec = asig.getSecuaz().getRolBase();
                        sumPercentage += porcentajeBotinSecuaz(sec);
                    }
                }
            }

            double escala = (sumPercentage > 1.0) ? (1.0 / sumPercentage) : 1.0;

            for (AsignacionRol asig : equipo_) {
                if (asig != null && asig.getSecuaz() != null) {
                    Secuaz secuaz = asig.getSecuaz();

                    if (exito && botinEquipo > 0.0) {
                        double porcentajeBotin = porcentajeBotinSecuaz(secuaz.getRolBase());
                        double beneficioSecuaz = botinEquipo * porcentajeBotin * escala;
                        secuaz.sumarBotin(beneficioSecuaz);
                    }
                    secuaz.desocupar();
                }
            }
        }
    }

    private double porcentajeBotinSecuaz(TipoRol rol) {
        if (rol == null) {
            throw new IllegalArgumentException(
                "El rol no puede ser nulo"
            );
        }

        switch(rol) {
            case CONDUCTOR: return 0.15;
            case CERRAJERO: return 0.05;
            case ASALTO: return 0.2;
            case NEGOCIADOR: return 0.1;
            case HACKER: return 0.03;
            default: return 0.0;
        }
    }

    // ----------
}