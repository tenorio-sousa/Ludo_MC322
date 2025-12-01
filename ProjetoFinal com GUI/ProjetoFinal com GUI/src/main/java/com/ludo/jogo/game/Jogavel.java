package com.ludo.jogo.game;

import com.ludo.jogo.game.exceptions.MovimentoInvalidoException;

/** Interface que define o comportamento de realizar uma jogada. */
public interface Jogavel {

    // MÉTODOS

    /**
     * Define o contrato para um jogador (Humano ou IA).
     *
     * @param valorDado O valor obtido na rolagem do dado.
     * @throws MovimentoInvalidoException Caso a jogada tentada viole as regras.
     */
    void fazerJogada(int valorDado) throws MovimentoInvalidoException;

}