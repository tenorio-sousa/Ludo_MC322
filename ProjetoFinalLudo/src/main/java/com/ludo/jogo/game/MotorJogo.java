package com.ludo.jogo.game;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoJogo;
import com.ludo.jogo.game.enums.EstadoPeao;
import com.ludo.jogo.game.exceptions.MovimentoInvalidoException;
import com.ludo.jogo.game.exceptions.NenhumMovimentoPossivelException;
import com.ludo.jogo.game.exceptions.SlotSaveIndisponivelException;
import com.ludo.jogo.persistencia.GerenciadorDePersistencia;

/** Classe com a lógica do jogo. */
public class MotorJogo implements Serializable {

    // ATRIBUTOS

    private Tabuleiro tabuleiro;
    private List<Jogador> jogadores;
    private Jogador jogadorAtual;
    private Dado dado;
    private int valorDadoAtual;
    private EstadoJogo estado;


    // CONSTRUTOR

    /** Construtor padrao. */
    public MotorJogo() {
        this.estado = EstadoJogo.ENCERRADO;
    }


    // MÉTODOS

    // Getters
    /** Getter para o tabuleiro.
     * @return O tabuleiro. */
    public Tabuleiro getTabuleiro() { return this.tabuleiro; }
    /** Getter para o jogador atual.
     * @return O jogador da vez. */
    public Jogador getJogadorAtual() { return this.jogadorAtual; }
    /** Getter para o valor do dado.
     * @return Ultimo valor rolado. */
    public int getValorDadoAtual() { return this.valorDadoAtual; }
    /** Getter para o estado do jogo.
     * @return Estado atual. */
    public EstadoJogo getEstado() { return this.estado; }
    /** Getter para os jogadores do jogo.
     * @return Lista de jogadores. */
    public List<Jogador> getJogadores() { return this.jogadores; }

    // Principais

    /**
     * Inicia o jogo usando os tipos e cores escolhidos no menu.
     * Usa Reflection =).
     *
     * @param tipos Jogador Humano ou JogadorIA;
     * @param coresEscolhidas Permite escolha de cores para cada jogador humano.
     */
    public void iniciarNovoJogo(List<Class<? extends Jogador>> tipos, List<Cor> coresEscolhidas) {
        this.tabuleiro = new Tabuleiro();
        this.dado = new Dado();
        this.jogadores = new ArrayList<>();
        this.estado = EstadoJogo.EM_JOGO;

        for (int i = 0; i < tipos.size() && i < coresEscolhidas.size() && i < 4; i++) {
            try {
                Constructor<? extends Jogador> construtor = tipos.get(i).getConstructor(Cor.class, MotorJogo.class);

                Jogador j = construtor.newInstance(coresEscolhidas.get(i), this);

                this.jogadores.add(j);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.jogadorAtual = jogadores.get(0);
    }

    /**
     * Atualiza jogadorAtual (ou repete, se tirou 6).
     */
    public void finalizarTurno() {
        if (this.estado != EstadoJogo.EM_JOGO) return;

        // Verifica vitoria
        if (jogadorAtual.verificarVitoria()) {
            this.estado = EstadoJogo.ENCERRADO;
            // PODEMOS NOTIFICAR GUI AQUI !!!!!!!!!!
            return;
        }

        // Regra do 6: Joga de novo
        if (valorDadoAtual == 6) {
            return;
        }

        // Passa a vez para o próximo jogador
        int indice = jogadores.indexOf(jogadorAtual);
        this.jogadorAtual = jogadores.get((indice + 1) % jogadores.size()); // Conecta o último jogador ao primeiro, em círculo
    }

    /**
     * Verifica se o jogadorAtual tem movimentos. Se não, lança exceção.
     *
     * @param valorDado Valor rolado.
     * @throws NenhumMovimentoPossivelException Se lista vazia.
     */
    private void verificarMovimentosPossiveis(int valorDado) throws NenhumMovimentoPossivelException {
        List<Peao> listaPeoesValidos = jogadorAtual.getPeoesValidos(valorDado, this.tabuleiro);

        if (listaPeoesValidos.isEmpty()) {
            // Forca passagem de turno se não pode mover
            finalizarTurno();
            throw new NenhumMovimentoPossivelException("Nenhum movimento possível! Passando a vez.");
        }
    }

    /**
     * Rola o dado e armazena o valor.
     *
     * @throws NenhumMovimentoPossivelException Se nao houver movimentos.
     * @throws MovimentoInvalidoException Se erro de logica IA.
     */
    public void rolarDado() throws NenhumMovimentoPossivelException, MovimentoInvalidoException {
        if (this.estado != EstadoJogo.EM_JOGO) return;

        this.valorDadoAtual = dado.rolar();

        try {
            verificarMovimentosPossiveis(valorDadoAtual);
        }
        catch (NenhumMovimentoPossivelException e) {
            throw e; // Sobe para a GUI exibir msg
        }

        if (jogadorAtual instanceof Jogavel) { // cast
            ((Jogavel) jogadorAtual).fazerJogada(valorDadoAtual);
        }
    }

    /**
     * Tenta mover o peão. Valida e delega para o tabuleiro.
     *
     * @param peao O peao a ser movido.
     * @throws MovimentoInvalidoException Se movimento ilegal.
     */
    public void tentarMoverPeao(Peao peao) throws MovimentoInvalidoException {
        if (this.estado != EstadoJogo.EM_JOGO) return;

        // Não deixa mexer em peão que já terminou o caminho
        if (peao.getEstado() == EstadoPeao.FINALIZADO) {
            throw new MovimentoInvalidoException("Este peão já completou o percurso!");
        }

        // Validar dono
        if (peao.getCor() != jogadorAtual.getCor()) {
            throw new MovimentoInvalidoException("Este peão não é seu!");
        }

        // Validar movimento fisico
        Casa destino = tabuleiro.getCasaDestino(peao, valorDadoAtual);

        if (destino == null) {
            if (peao.getEstado() == EstadoPeao.BASE) {
                throw new MovimentoInvalidoException("Precisa tirar 6 para sair da base!");
            }
            if (destino == null) {
                throw new MovimentoInvalidoException("Você não pode realizar esse movimento!");
            }
        }

        if (tabuleiro.verificarBloqueio(destino)) {
            throw new MovimentoInvalidoException("Esta casa está bloqueada!");
        }

        // Executa
        tabuleiro.moverPeao(peao, destino);

        if (jogadorAtual.verificarVitoria()) {
            this.estado = EstadoJogo.ENCERRADO;
            return; // O controlador detecta e abre a tela de vitória
        }

        finalizarTurno();
    }

    // Persistencia

    /**
     * Salva o Jogo do dado Slot.
     *
     * @param slot Slot (1 a 4) que se deseja salvar.
     */
    public void salvarJogo(int slot) {
        GerenciadorDePersistencia.salvarJogo(this, slot);
    }

    /**
     * Carrega um jogo salvo de um dado Slot.
     *
     * @param slot Slot de onde queremos carregar o jogo;
     * @throws SlotSaveIndisponivelException Exceção caso não tenha save no slot.
     */
    public void carregarJogo(int slot) throws SlotSaveIndisponivelException {
        MotorJogo carregado = GerenciadorDePersistencia.carregarJogo(slot);
        this.tabuleiro = carregado.tabuleiro;
        this.jogadores = carregado.jogadores;
        this.jogadorAtual = carregado.jogadorAtual;
        this.dado = carregado.dado;
        this.valorDadoAtual = carregado.valorDadoAtual;
        this.estado = carregado.estado;
    }
}