package com.ludo.jogo.game;

/** Representa a base de onde os peoes saem. */
public class CasaInicial extends Casa {

    // CONSTRUTOR

    /** Construtor padrao. */
    public CasaInicial() {
        super();
    }


    // MÉTODOS

    /**
     * Retorna false pois um peão aqui pode ser comido, por escolha de design.
     *
     * @return Sempre false.
     */
    @Override public boolean isCasaSegura() { return false; }
}