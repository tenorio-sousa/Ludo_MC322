package com.ludo.jogo.game.exceptions;

/** Excecao lancada ao tentar acessar um slot de save invalido ou corrompido */
public class SlotSaveIndisponivelException extends Exception {

    // CONSTRUTOR

    /**
     * Exceção com mensagem personalizada.
     *
     * @param mensagem Descricao do erro.
     */
    public SlotSaveIndisponivelException(String mensagem) { super(mensagem); }

}