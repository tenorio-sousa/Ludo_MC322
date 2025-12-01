package com.ludo.jogo.game;

import java.io.Serializable;

/** Classe que simula um dado. */
public class Dado implements Serializable {

    // CONSTRUTOR

    /** Construtor padrao. */
    public Dado() { }


    // MÉTODOS

    /**
     * Simula a rolagem de um dado.
     *
     * @return Retorna um número inteiro entre 1 e 6.
     */
    public int rolar() { return (int) (Math.random() * 6 + 1); }
}