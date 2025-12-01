package com.ludo.jogo.game;

import com.ludo.jogo.game.enums.Cor;

/** Representa as casas coloridas finais antes da chegada. */
public class CasaRetaFinal extends Casa {

    // ATRIBUTOS

    private final Cor corPermitida;


    // CONSTRUTOR

    /**
     * Construtor que define a cor da reta final.
     *
     * @param corPermitida A unica cor que pode entrar nesta casa.
     */
    public CasaRetaFinal(Cor corPermitida) {
        super();
        this.corPermitida = corPermitida;
    }


    // MÉTODOS

    // Principais
    /**
     * Retorna true pois um peão aqui não pode ser comido.
     *
     * @return Sempre true.
     */
    @Override
    public boolean isCasaSegura() { return true; }

    //Getter
    public Cor getCorPermitida() {
        return corPermitida;
    }
    
}