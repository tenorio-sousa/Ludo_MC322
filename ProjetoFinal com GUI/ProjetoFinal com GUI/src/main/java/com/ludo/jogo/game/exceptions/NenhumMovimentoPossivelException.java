package com.ludo.jogo.game.exceptions;

/** Excecao lancada quando o jogador nao tem pecas para mover */
public class NenhumMovimentoPossivelException extends Exception {

    // CONSTRUTOR

    /**
     * Exceção com mensagem personalizada.
     *
     * @param mensagem Descricao do erro.
     */
    public NenhumMovimentoPossivelException(String mensagem) { super(mensagem); }

}