package test.domain;

import domain.Secuaz;
import domain.enums.TipoRol;
import domain.enums.EstadoSecuaz;

/**
 * SecuazStub
 *
 * Simula el comportamiento de un Secuaz para realizar
 * las pruebas unitarias de ConjuntoSecuaces.
 *
 * @author Arian Mohammadian
 */
public class SecuazStub extends Secuaz {

    private final String idStub_;
    private final EstadoSecuaz estadoStub_;

    // CONSTRUCTOR

    public SecuazStub(String id, EstadoSecuaz estado) {
        super(id, TipoRol.HACKER);

        idStub_ = id;
        estadoStub_ = estado;
    }

    // ----------

    // GETTERS CLASE

    @Override
    public String getId() {
        return idStub_;
    }

    @Override
    public EstadoSecuaz getEstado() {
        return estadoStub_;
    }

    // ----------

    // OPERACIONES ESPECIFICAS CLASE

    @Override
    public boolean estaLibre() {
        return estadoStub_ == EstadoSecuaz.LIBRE;
    }

    // ----------
}