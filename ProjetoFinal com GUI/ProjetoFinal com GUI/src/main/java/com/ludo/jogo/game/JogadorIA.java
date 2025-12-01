package com.ludo.jogo.game;

import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;
import com.ludo.jogo.game.exceptions.MovimentoInvalidoException;
import java.util.List;
import java.util.Random;

/** Implementacao do jogador controlado pelo computador. */
public class JogadorIA extends Jogador implements Jogavel {

    // CONSTRUTOR

    /**
     * Construtor padrao.
     *
     * @param cor Cor do jogador;
     * @param motorJogo Referencia ao motor.
     */
    public JogadorIA(Cor cor, MotorJogo motorJogo) {
        super(cor, motorJogo);
    }


    // MÉTODOS

    /**
     * Ação da IA: Calcula e executa a jogada automaticamente.
     *
     * @param valorDado Valor do dado rolado;
     * @throws MovimentoInvalidoException Se tentar um movimento inválido.
     */
    @Override
    public void fazerJogada(int valorDado) throws MovimentoInvalidoException {
        List<Peao> peoesValidos = getPeoesValidos(valorDado, motorJogo.getTabuleiro());

        if (peoesValidos.isEmpty()) {
            // Se não há movimentos, apenas passa o turno. Apenas por segurança, pois o MotorJogo já lida com isso
            motorJogo.finalizarTurno();
        }
        else {
            // Tenta o melhor Peão primeiro
            Peao escolhido = melhorPeao(peoesValidos, valorDado);
            try {
                motorJogo.tentarMoverPeao(escolhido);
                return;
            }
            catch (MovimentoInvalidoException e) {
                System.err.println("Erro na IA (Melhor Peão): " + e.getMessage());
                // Remove este peão falho da lista e tenta os outros
                peoesValidos.remove(escolhido);
            }

            // primeiro fallback: Se o melhor falhou, tenta aleatoriamente outro válido da lista
            for (Peao p : peoesValidos) {
                try {
                    motorJogo.tentarMoverPeao(p);
                    return;
                }
                catch (MovimentoInvalidoException e) {
                    System.err.println("Erro na IA (Fallback): " + e.getMessage());
                }
            }
        }

        // Segundo fallback para bugs
        System.err.println("IA travou: tinha peões válidos mas todos falharam no movimento real.");
        motorJogo.finalizarTurno(); // Força passar a vez para não congelar o jogo
    }

    /**
     * Reliza uma escolha para o melhor Peao para a IA jogar.
     *
     * @param peoesValidos Lista com peões que podem ser movidos;
     * @param valorDado Valor tirado no dado;
     * @return Retorna o melhor peão para se jogar.
     */
    private Peao melhorPeao(List<Peao> peoesValidos, int valorDado){
        // Caso tenha apenas um peão na lista de peões válidos, ele é o escolhido
        if (peoesValidos.size() == 1){
            return peoesValidos.getFirst();
        }

        // Quando o valor do dado é 6, verifica se existe algum peão na base. Se sim, ele é o escolhido
        for (Peao peao : peoesValidos) {
            if (valorDado == 6 && peao.getEstado() == EstadoPeao.BASE){
                return peao;
            }
        }

        // Verifica se a casa destino do peão é segura, e se tem peões adversários. Se a casa não for segura, e tem
        // peões adversários, ele é o escolhido
        for (Peao peao : peoesValidos) {
            Casa casaDestino = motorJogo.getTabuleiro().getCasaDestino(peao, valorDado);
            if (!casaDestino.isCasaSegura() && !casaDestino.getPeoes().isEmpty()){
                if (casaDestino.getPeoes().getFirst().getCor() != peao.getCor()) {
                    return peao;
                }
            }
        }
        // Verifica se a casa do peão é segura, e se a casa destino do peão é segura. Se o peão não estiver numa casa
        // segura, mas o seu destino é seguro, ele é o escolhido
        for (Peao peao : peoesValidos) {
            Casa casaDestino = motorJogo.getTabuleiro().getCasaDestino(peao, valorDado);
            if (!peao.getCasa().isCasaSegura() && casaDestino.isCasaSegura()){
                return peao;
            }
        }

        // Verifica se a casa que o peão está é segura. Se não, ele é o escolhido.
        for (Peao peao : peoesValidos) {
            if (!peao.getCasa().isCasaSegura()){
                return peao;
            }
        }

        // Caso nenhum dos requisitos seja atendido, retorna um peão aleatório entre a lista de peões validos
        return peoesValidos.get(new Random().nextInt(peoesValidos.size()));

    }
}