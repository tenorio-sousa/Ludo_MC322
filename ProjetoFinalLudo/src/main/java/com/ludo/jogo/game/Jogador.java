package com.ludo.jogo.game;

import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/** Define a base para um jogador. */
public abstract class Jogador implements Serializable {

    // ATRIBUTOS

    protected final Cor cor;
    protected List<Peao> peoes;
    protected MotorJogo motorJogo;


    // CONSTRUTOR

    /**
     * Construtor padrao.
     *
     * @param cor Cor do jogador;
     * @param motorJogo Referencia ao motor.
     */
    public Jogador(Cor cor, MotorJogo motorJogo) {
        this.cor = cor;
        this.motorJogo = motorJogo;
        this.peoes = new ArrayList<>();

        // Cria os 4 peões na base
        Casa base = motorJogo.getTabuleiro().getCasaBase(cor);
        for (int i = 0; i < 4; i++) {
            this.peoes.add(new Peao(cor, base));
            base.adicionarPeao(this.peoes.get(i));
        }
    }


    // MÉTODOS

    // Getters
    /**
     * Getter que retorna a cor Cor do jogador.
     *
     * @return Retorna a cor Cor.
     */
    public Cor getCor() { return this.cor; }
    /**
     * Getter que retorna uma lista com os peões deste jogador.
     *
     * @return Retorna a lista de peões do jogador.
     */
    public List<Peao> getPeoes() { return this.peoes; }

    // Principais
    /**
     * Lógica para verificar quais peões podem se mover.
     *
     * @param valorDado Valor de 1 a 6 tirado no dado neste turno;
     * @param tabuleiro Uma referência do tabuleiro entregue para uso de métodos;
     * @return Retorna uma lista com os peões que podem se mover.
     */
    public List<Peao> getPeoesValidos(int valorDado, Tabuleiro tabuleiro) {
        List<Peao> listaPeoesValidos = new ArrayList<>();
        for (Peao peao : peoes) {
            if (peao.getEstado() == EstadoPeao.FINALIZADO) continue;

            Casa casaDestino = tabuleiro.getCasaDestino(peao, valorDado);

            // Verifica se destino existe e nao é bloqueio
            if (casaDestino != null && !tabuleiro.verificarBloqueio(casaDestino)) {
                listaPeoesValidos.add(peao);
            }
        }
        return listaPeoesValidos;
    }

    /**
     * Retorna true se todos os 4 Peoes estão no estado FINALIZADO.
     *
     * @return True se venceu, false se não.
     */
    public boolean verificarVitoria() {
        // Se todos (allMatch) os peões tiverem o Estado FINALIZADO, retorna true
        return peoes.stream().allMatch(p -> p.getEstado() == EstadoPeao.FINALIZADO);
    }
}