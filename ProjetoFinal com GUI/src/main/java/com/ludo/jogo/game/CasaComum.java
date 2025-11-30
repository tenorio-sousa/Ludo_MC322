package com.ludo.jogo.game;

/** Representa uma casa comum do tabuleiro. */
public class CasaComum extends Casa {

    // CONSTRUTOR

    /** Construtor padrao. */
    public CasaComum() {
        super();
    }


    // MÉTODOS

    /**
     * Retorna falso pois um peão aqui pode ser comido.
     *
     * @return Sempre false.
     */
    @Override public boolean isCasaSegura() { return false; }
}