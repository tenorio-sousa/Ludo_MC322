package com.ludo.jogo.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoPeao;

/** Representa o tabuleiro do jogo Ludo. */
public class Tabuleiro implements Serializable {

    // ATRIBUTOS

    private final List<Casa> casasCircuito;
    private final Map<Cor, Casa> casasBase;
    private final Map<Cor, List<Casa>> casasRetaFinal;
    private final Map<Cor, Casa> casasSaida;
    private final Map<Cor, Integer> indicesEntradaReta;


    // CONSTRUTOR

    /** Inicializa o tabuleiro com todas as casas e configurações. */
    public Tabuleiro() {
        this.casasCircuito = new ArrayList<>();
        this.casasRetaFinal = new HashMap<>();
        this.casasSaida = new HashMap<>();
        this.casasBase = new HashMap<>();
        this.indicesEntradaReta = new HashMap<>();

        // Índices de entrada na retaFinal: sempre UMA casa antes da casa de saída de cada cor
        // Ajustados para o circuito de 56 casas (0 a 55)
        indicesEntradaReta.put(Cor.VERMELHO, 55); // Última casa do array (antes do 0)
        indicesEntradaReta.put(Cor.VERDE, 13); // A casa antes do 14
        indicesEntradaReta.put(Cor.AMARELO, 27); // A casa antes do 28
        indicesEntradaReta.put(Cor.AZUL, 41); // A casa antes do 42

        // Inicializa as 56 casas do circuito
        for (int i = 0; i < 56; i++) {
            Casa casa;

            // Índices de saída (onde o peão entra ao sair da base) – NÃO são seguras
            if (i == 0 || i == 14 || i == 28 || i == 42) {
                casa = new CasaInicial();

                if (i == 0)  casasSaida.put(Cor.VERMELHO, casa);
                if (i == 14) casasSaida.put(Cor.VERDE,    casa);
                if (i == 28) casasSaida.put(Cor.AMARELO,  casa);
                if (i == 42) casasSaida.put(Cor.AZUL,     casa);
            }
            // Casas de estrela: realmente seguras
            else if (i == 9 || i == 23 || i == 37 || i == 51) {
                casa = new CasaSegura();
            }
            // Demais casas comuns
            else {
                casa = new CasaComum();
            }

            casasCircuito.add(casa);
        }

        // Bases e retas finais
        for (Cor cor : Cor.values()) {
            if (cor == Cor.NENHUMA) continue;

            this.casasBase.put(cor, new CasaInicial());

            List<Casa> reta = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                reta.add(new CasaRetaFinal(cor));
            }
            reta.add(new CasaSegura()); // casa central (compartilhada conceitualmente como chegada)
            this.casasRetaFinal.put(cor, reta);
        }
    }


    // MÉTODOS

    // Getters

    /**
     * Getter para auxiliar inicialização.
     *
     * @param cor Cor do jogador;
     * @return Retorna uma referência da Casa inicial respectiva ao jogador.
     */
    public Casa getCasaBase(Cor cor) {
        return this.casasBase.get(cor);
    }

    /**
     * Calcula a casa de destino baseada na lógica do grafo do tabuleiro.
     *
     * @param peao O peão a ser movido;
     * @param valorDado O valor do dado;
     * @return A casa de destino ou null se movimento impossível.
     */
    public Casa getCasaDestino(Peao peao, int valorDado) {
        // Infos úteis
        EstadoPeao estado = peao.getEstado();
        Cor cor = peao.getCor();
        Casa casaAtual = peao.getCasa();

        // Peão na Base
        if (estado == EstadoPeao.BASE) {
            return (valorDado == 6) ? this.casasSaida.get(cor) : null;
        }

        // Peão na reta final
        if (this.casasRetaFinal.get(cor).contains(casaAtual)) {
            List<Casa> casasRetaFinal = this.casasRetaFinal.get(cor);
            int indiceAtual = casasRetaFinal.indexOf(casaAtual);
            int indiceAlvo = indiceAtual + valorDado;
            int ultimaCasa = casasRetaFinal.size() - 1;

            // Se passou do final, retrocede
            if (indiceAlvo > ultimaCasa) {
                int excesso = indiceAlvo - ultimaCasa;
                int indiceVoltando = ultimaCasa - excesso;
                return (indiceVoltando >= 0) ? casasRetaFinal.get(indiceVoltando) : null;
            }
            return casasRetaFinal.get(indiceAlvo);
        }

        // Peão em um lugar genérico do circuito
        if (this.casasCircuito.contains(casaAtual)) {
            int indiceDaCasaAtual = this.casasCircuito.indexOf(casaAtual);
            int indiceDaEntradaRetaFinal = this.indicesEntradaReta.get(cor);
            int movimentos = valorDado;

            // Andamos passo a passo para detectar o momento de entrar na reta final
            while (movimentos > 0) {
                // Entrada na reta final
                if (indiceDaCasaAtual == indiceDaEntradaRetaFinal) {
                    int movimentosNaReta = movimentos - 1;
                    List<Casa> listaCasasRetaFinal = this.casasRetaFinal.get(cor);
                    return (movimentosNaReta < listaCasasRetaFinal.size())
                            ? listaCasasRetaFinal.get(movimentosNaReta)
                            : null;
                }
                indiceDaCasaAtual = (indiceDaCasaAtual + 1) % 56; // loop (0 a 55)
                movimentos--;
            }
            return this.casasCircuito.get(indiceDaCasaAtual);
        }
        return null; // fallback
    }

    /**
     * Executa, efetivamente, o movimento do Peão peao.
     *
     * @param peao O peão que será movido;
     * @param destino O destino do peão peao movido.
     */
    public void moverPeao(Peao peao, Casa destino) {
        peao.getCasa().removerPeao(peao); // Remove da casa antiga

        verificarCaptura(peao, destino); // Verifica captura antes de entrar

        destino.adicionarPeao(peao); // Add info na casa
        peao.setCasa(destino); // Add info no peão

        // Atualiza estado se entrou/moveu na reta final ou saiu da base
        List<Casa> reta = this.casasRetaFinal.get(peao.getCor());
        if (!this.casasBase.get(peao.getCor()).equals(destino)) { // se não está na base
            // Coloca FINALIZADO se o destino é igual à última casa
            if (destino == reta.get(reta.size() - 1)) {
                peao.setEstado(EstadoPeao.FINALIZADO);
                // Opcional: remover visualmente da casa final para não amontoar,
                // mas logicamente ele precisa estar lá para contar vitória.
                // Se quiser remover: destino.removerPeao(peao); peao.setCasa(null);
            } else {
                peao.setEstado(EstadoPeao.JOGANDO);
            }
        }
    }

    /**
     * Verifica se o movimento acarretará em uma captura.
     *
     * @param peaoMovido O peão que foi movido nesse turno;
     * @param destino A casa em que o peão peaoMovido vai chegar.
     */
    private void verificarCaptura(Peao peaoMovido, Casa destino) {
        // Captura impossível se é uma casa segura ou se não há peões para serem capturados
        if (destino.isCasaSegura() || destino.getPeoes().isEmpty()) {
            return;
        }

        // Bloqueio: mais de um peão na casa
        if (!verificarBloqueio(destino)) {
            Peao peaoNaCasa = destino.getPeoes().get(0);
            // Se for inimigo, captura
            if (peaoNaCasa.getCor() != peaoMovido.getCor()) {
                destino.removerPeao(peaoNaCasa); // tira o peão inimigo
                peaoNaCasa.voltarParaBase();     // volta pra base
            }
        }
    }

    /**
     * Retorna a resposta para "aqui há um bloqueio?".
     * Regra: Não há bloqueio em casas de Reta Final.
     *
     * @param casa A casa que verificaremos se tem o bloqueio;
     * @return Retorna true se for um bloqueio válido.
     */
    public boolean verificarBloqueio(Casa casa) {
        // Usa allMatch para garantir que a casa NÃO está em nenhuma lista de reta final de nenhuma cor
        boolean naoEhRetaFinal = this.casasRetaFinal.values().stream().allMatch(lista -> !lista.contains(casa));

        // Só é bloqueio se: (Não for reta final) E (Tiver mais de 1 peão)
        return naoEhRetaFinal && casa.getPeoes().size() > 1;
    }

    /**
     * Verifica se uma dada Casa faz parte do circuito.
     *
     * @param casa A casa que vamos verificar;
     * @return True se a casa faz parte do circuito principal (0 a 51).
     */
    public boolean isCasaDoCircuito(Casa casa) {
        return this.casasCircuito.contains(casa);
    }

    /**
     * Retorna o índice de uma dada Casa.
     *
     * @param casa A Casa que queremos o índice;
     * @return Retorna o índice Casa no circuito, ou -1 se não fizer parte do circuito.
     */
    public int getIndiceCircuito(Casa casa) {
        return this.casasCircuito.indexOf(casa);
    }

    /**
     * Verifica se uma Casa faz parte da RetaFinal de uma cor dada.
     *
     * @param cor A cor que queremos verificar;
     * @param casa A Casa que queremos verificar;
     * @return True se a casa faz parte da reta final da cor dada.
     */
    public boolean isCasaDaRetaFinal(Cor cor, Casa casa) {
        List<Casa> reta = this.casasRetaFinal.get(cor);
        return reta != null && reta.contains(casa);
    }

    /**
     * Retorna o índice de uma dada Casa na Reta Final.
     *
     * @param cor A Cor da casa, para ver se compatível com a que buscamos;
     * @param casa A Casa a RetaFinal que queremos verificar o índice
     * @return Retorna o índice da casa na reta final da cor dada, ou -1 se não fizer parte.
     */
    public int getIndiceRetaFinal(Cor cor, Casa casa) {
        List<Casa> reta = this.casasRetaFinal.get(cor);
        if (reta == null) return -1;
        return reta.indexOf(casa);
    }
}