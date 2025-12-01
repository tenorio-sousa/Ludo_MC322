package com.ludo.jogo.game;

import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;
import java.io.Serializable;

/** Representa um peão no tabuleiro. */
public class Peao implements Serializable {

    // ATRIBUTOS

    private final Cor cor;
    private Casa casaAtual;
    private final Casa casaInicial;
    private EstadoPeao estado;


    // CONSTRUTOR

    /**
     * Construtor que inicializa o peão na sua base.
     *
     * @param cor A cor do peão;
     * @param casaInicial A casa base onde o peão começa.
     */
    public Peao(Cor cor, Casa casaInicial) {
        this.cor = cor;
        this.casaInicial = casaInicial;
        this.casaAtual = casaInicial;
        this.estado = EstadoPeao.BASE;
    }


    // MÉTODOS

    // Getters
    /**
     * Getter que retorna a casa Casa atual em que está o peão.
     *
     * @return Retorna a uma instância Casa.
     */
    public Casa getCasa() { return this.casaAtual; }
    /**
     * Getter que rertorna a cor Cor do peão.
     *
     * @return Retorna a cor Cor.
     */
    public Cor getCor() { return this.cor; }
    /**
     * Getter que retorna o Estado atual do peão.
     *
     * @return Retorna o Estado do peão (BASE, EM_JOGO ou FINALIZADO).
     */
    public EstadoPeao getEstado() { return this.estado; }

    // Setters
    /**
     * Setter que muda a casa do poeão.
     *
     * @param novaCasa A nova casa Casa do peão.
     */
    public void setCasa(Casa novaCasa) { this.casaAtual = novaCasa; }
    /**
     * Setter que muda o Estado do peão.
     *
     * @param estado O novo Estado (BASE, EM_JOGO ou FINALIZADO).
     */
    public void setEstado(EstadoPeao estado) { this.estado = estado; }

    // Principais
    /**
     * Move o peão de volta para sua CasaInicial e atualiza seu estado.
     */
    public void voltarParaBase() {
        this.casaAtual.removerPeao(this); // Tira o peão da casa que estava
        setCasa(this.casaInicial); // Joga o peão pra casa inicial
        setEstado(EstadoPeao.BASE); // Sinaliza que o peão esta na base
    }
}