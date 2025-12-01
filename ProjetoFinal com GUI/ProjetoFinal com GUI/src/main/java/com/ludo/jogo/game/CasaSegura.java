package com.ludo.jogo.game;

/** Representa uma casa segura (como saidas ou estrelas) onde não podem haver capturas. */
public class CasaSegura extends Casa {

    // CONSTRUTOR

    /** Construtor padrao. */
    public CasaSegura() {
        super();
    }


    // MÉTODOS

    /**
     * Retorna true pois um peão aqui não pode ser comido.
     *
     * @return Sempre true.
     */
    @Override public boolean isCasaSegura() { return true; }
}