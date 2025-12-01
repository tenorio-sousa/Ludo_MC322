package com.ludo.jogo.game;

import com.ludo.jogo.game.enums.Cor;

/** Implementacao de um jogador humano. */
public class JogadorHumano extends Jogador implements Jogavel {

    // CONSTRUTOR

    /**
     * Construtor padrao.
     *
     * @param cor Cor do jogador;
     * @param motorJogo Referencia ao motor.
     */
    public JogadorHumano(Cor cor, MotorJogo motorJogo) {
        super(cor, motorJogo);
    }


    // MÉTODOS

    /**
     * Um jogador humano apenas libera a GUI.
     * Apenas realiza um print simples, para debugg.
     *
     * @param valorDado Valor do dado rolado.
     */
    @Override
    public void fazerJogada(int valorDado) {
        // Apenas log para debug, a GUI lida com isso pelo ControladorJogo
        System.out.println("Vez do Humano (" + cor + "). Dado: " + valorDado);
    }
}