package com.ludo.jogo.game.exceptions;

/** Excecao lancada quando um movimento desrespeita as regras */
public class MovimentoInvalidoException extends Exception {

    // CONSTRUTOR

    /**
     * Exceção com mensagem personalizada.
     *
     * @param mensagem Descricao do erro.
     */
    public MovimentoInvalidoException(String mensagem) { super(mensagem); }

}