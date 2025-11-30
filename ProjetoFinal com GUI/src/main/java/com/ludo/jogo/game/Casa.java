package com.ludo.jogo.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Define a base para qualquer casa no tabuleiro. */
public abstract class Casa implements Serializable {

    // ATRIBUTOS

    protected List<Peao> peoesNaCasa;


    // CONSTRUTOR

    /** Construtor padrao que inicializa a lista de peoes. */
    public Casa() {
        this.peoesNaCasa = new ArrayList<>();
    }


    // MÉTODOS

    // Getters
    /**
     * Getter para ver peões em uma lista.
     *
     * @return Retorna a lista de peões na casa.
     */
    public List<Peao> getPeoes() { return this.peoesNaCasa; }

    /**
     * (Abstrato) Retorna true se for um abrigo.
     *
     * @return Verdadeiro se o peão não puder ser capturado nesta casa.
     */
    public abstract boolean isCasaSegura();

    // Principais
    /**
     * Adiciona um Peao na lista de peões da casa.
     *
     * @param peao O peão a ser adicionado.
     */
    public void adicionarPeao(Peao peao) { this.peoesNaCasa.add(peao); }

    /**
     * Remove um Peao na lista de peões da casa.
     *
     * @param peao O peão a ser removido.
     */
    public void removerPeao(Peao peao) { this.peoesNaCasa.remove(peao); }
}