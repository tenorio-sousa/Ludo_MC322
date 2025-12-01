package com.ludo.jogo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ludo.jogo.game.Casa;
import com.ludo.jogo.game.ConfigJogo;
import com.ludo.jogo.game.Jogador;
import com.ludo.jogo.game.JogadorHumano;
import com.ludo.jogo.game.JogadorIA;
import com.ludo.jogo.game.MotorJogo;
import com.ludo.jogo.game.Peao;
import com.ludo.jogo.game.Tabuleiro;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.enums.EstadoJogo;
import com.ludo.jogo.game.enums.EstadoPeao;
import com.ludo.jogo.game.exceptions.MovimentoInvalidoException;
import com.ludo.jogo.game.exceptions.NenhumMovimentoPossivelException;
import com.ludo.jogo.game.exceptions.SlotSaveIndisponivelException;
import com.ludo.jogo.persistencia.GerenciadorDePersistencia;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Controlador da tela principal do jogo (Tabuleiro). */
public class ControladorJogo {

    // ATRIBUTOS

    private static final int BOARD_CELLS = 15;
    private MotorJogo motorJogo;
    private final Map<Peao, Circle> mapaPeoes = new HashMap<>();
    private boolean aguardandoMovimentoHumano = false;

    @FXML private Pane painelTabuleiro;
    @FXML private Button botaoRolarDado;
    @FXML private Label labelStatus;
    @FXML private Pane paneDado;


    /** Classe auxiliar para posicionamento no grid. */
    private static class CellPos {
        final double row;
        final double col;
        CellPos(double row, double col) {
            this.row = row;
            this.col = col;
        }
    }


    // MÉTODOS

    // Inicialização

    /**
     * Inicializa o controlador, configura o jogo e desenha o tabuleiro.
     */
    @FXML
    public void initialize() {
        this.motorJogo = new MotorJogo();

        // Pega config escolhida no menu ou define padrao
        List<Class<? extends Jogador>> tipos = ConfigJogo.getTiposJogadores();
        List<Cor> cores = ConfigJogo.getCoresJogadores();

        if (tipos == null || tipos.isEmpty()) {
            tipos = new ArrayList<>();
            tipos.add(JogadorHumano.class);
            tipos.add(JogadorIA.class);
            tipos.add(JogadorIA.class);
            tipos.add(JogadorIA.class);
        }

        if (cores == null || cores.isEmpty()) {
            cores = new ArrayList<>();
            cores.add(Cor.VERMELHO);
            cores.add(Cor.VERDE);
            cores.add(Cor.AMARELO);
            cores.add(Cor.AZUL);
        }

        // Permite no máx 4 jogadores e listas com mesmo tamanho
        int n = Math.min(4, Math.min(tipos.size(), cores.size()));
        List<Class<? extends Jogador>> tiposFinais = new ArrayList<>(tipos.subList(0, n));
        List<Cor> coresFinais = new ArrayList<>(cores.subList(0, n));

        // Inicia o jogo
        this.motorJogo.iniciarNovoJogo(tiposFinais, coresFinais);

        // Força um tamanho mínimo para garantir que o Pane não suma ao usar clear()
        paneDado.setMinWidth(100);
        paneDado.setMinHeight(100);

        // Listeners para o Tabuleiro
        painelTabuleiro.widthProperty().addListener((obs, oldV, newV) -> atualizarGUI());
        painelTabuleiro.heightProperty().addListener((obs, oldV, newV) -> atualizarGUI());

        // Listeners para o Dado (Garanta que isso está aqui)
        paneDado.widthProperty().addListener((obs, oldVal, newVal) -> {
            desenharDado(motorJogo.getValorDadoAtual());
        });
        paneDado.heightProperty().addListener((obs, oldVal, newVal) -> {
            desenharDado(motorJogo.getValorDadoAtual());
        });

        // Desenha estado inicial
        atualizarGUI();
        desenharDado(0);

        // Inicia IA se necessário
        gerenciarTurnoIA();
    }


    // Gerenciamento de Turno e IA

    /**
     * Verifica se é a vez da IA. Se for, aguarda um tempo e executa a jogada.
     */
    private void gerenciarTurnoIA() {
        if (motorJogo.getJogadorAtual() instanceof JogadorIA) {
            botaoRolarDado.setDisable(true);
            labelStatus.setText(textoVezJogador() + " (Calculando...)");

            // Pausa inicial de 1s para dar ritmo ao jogo
            PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
            pause.setOnFinished(event -> executarJogadaIA());
            pause.play();
        }
        else {
            // Vez do Humano
            botaoRolarDado.setDisable(false);
            labelStatus.setText(textoVezJogador());
        }
    }

    /**
     * Executa a logica de rolar dado e mover da IA com delays para visualização.
     */
    private void executarJogadaIA() {
        Jogador iaDaVez = motorJogo.getJogadorAtual();

        try {
            motorJogo.rolarDado();

            // Visual do dado e mensagem
            int valor = motorJogo.getValorDadoAtual();
            desenharDado(valor);
            labelStatus.setText("IA (" + iaDaVez.getCor() + ") tirou " + valor + " e moveu.");

            // Animação para a leitura
            PauseTransition delayLeitura = new PauseTransition(Duration.seconds(1.5));
            delayLeitura.setOnFinished(ev1 -> {

                atualizarGUI(); // Atualiza o tabuleiro

                // Animação Pós movimento
                PauseTransition delayPosMovimento = new PauseTransition(Duration.seconds(1.0));
                delayPosMovimento.setOnFinished(ev2 -> {

                    // Verifica se após o movimento o jogo encerrou
                    if (motorJogo.getEstado() == com.ludo.jogo.game.enums.EstadoJogo.ENCERRADO) {
                        carregarTelaVitoria();
                    } else {
                        // Se não venceu, segue o jogo
                        gerenciarTurnoIA();
                    }
                });
                delayPosMovimento.play();
            });
            delayLeitura.play();
        }
        catch (NenhumMovimentoPossivelException e) {
            int valor = motorJogo.getValorDadoAtual();
            desenharDado(valor);

            String msg = "A IA (" + iaDaVez.getCor() + ") tirou " + valor + ", mas não há movimentos possíveis!";
            labelStatus.setText(msg);

            // Pausa longa (2.0s) para ler o erro antes de passar a vez
            PauseTransition pauseErro = new PauseTransition(Duration.seconds(2.0));
            pauseErro.setOnFinished(ev -> {
                atualizarGUI();
                gerenciarTurnoIA();
            });
            pauseErro.play();

            return; // Impede execução cascata
        }
        catch (MovimentoInvalidoException e) {
            System.err.println("Erro IA: " + e.getMessage());
            atualizarGUI();
            gerenciarTurnoIA();
        }
    }

    private void mostrarTelaVitoria(Jogador vencedor) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ludo/jogo/gui/tela_vitoria.fxml")
            );
            Parent root = loader.load();

            // controlador da tela de vitória
            ControladorVitoria controladorVitoria = loader.getController();

            String nome = nomeJogador(vencedor);
            String cor  = vencedor.getCor().toString();

            // AQUI: usa o mesmo nome do método que existe no ControladorVitoria
            controladorVitoria.configurarVencedor(nome, cor);

            Stage stage = (Stage) painelTabuleiro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Vitória - Ludo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Auxiliares de Texto

    /**
     * Gera um nome amigavel para o jogador (Ex: Jogador 1, Máquina 1).
     *
     * @param j O jogador a ser nomeado;
     * @return String com o nome formatado.
     */
    private String nomeJogador(Jogador j) {
        List<Jogador> lista = motorJogo.getJogadores();
        int idx = lista.indexOf(j);
        int countHumanos = 0;
        int countIAs = 0;

        for (int i = 0; i <= idx; i++) {
            Jogador atual = lista.get(i);
            if (atual instanceof JogadorHumano) countHumanos++;
            else if (atual instanceof JogadorIA) countIAs++;
        }

        if (j instanceof JogadorHumano) return "Jogador " + countHumanos;
        if (j instanceof JogadorIA) return "Máquina " + countIAs;
        return "Jogador";
    }

    /**
     * Retorna o texto indicando de quem é a vez.
     *
     * @return String formatada.
     */
    private String textoVezJogador() {
        if (motorJogo == null || motorJogo.getJogadorAtual() == null) return "";
        Jogador atual = motorJogo.getJogadorAtual();
        return "Vez de: " + nomeJogador(atual) + " (" + atual.getCor() + ")";
    }


    // Ações da GUI

    /**
     * Handler para o botão de rolar o dado (apenas Humanos).
     *
     * @param event Evento do clique.
     */
    @FXML
    private void handleRolarDado(ActionEvent event) {
        // Capturamos quem vai jogar ANTES de rolar
        Jogador jogadorDaVez = motorJogo.getJogadorAtual();

        try {
            motorJogo.rolarDado();

            Jogador atual = motorJogo.getJogadorAtual();
            int valor = motorJogo.getValorDadoAtual();

            aguardandoMovimentoHumano = true;
            desenharDado(valor);

            StringBuilder sb = new StringBuilder();
            sb.append(nomeJogador(atual))
                    .append(" (").append(atual.getCor()).append(") tirou ")
                    .append(valor);

            if (atual instanceof JogadorHumano) {
                sb.append("\nClique no peão para mover.");
                botaoRolarDado.setDisable(true);
            }
            labelStatus.setText(sb.toString());
        }
        catch (NenhumMovimentoPossivelException e) {
            int valor = motorJogo.getValorDadoAtual();
            desenharDado(valor);
            aguardandoMovimentoHumano = false;

            String msg = nomeJogador(jogadorDaVez) + " (" + jogadorDaVez.getCor() +
                    ") tirou " + valor + ", mas não há movimentos possíveis!";
            labelStatus.setText(msg);

            // Pausa para ler o erro (2.0s)
            botaoRolarDado.setDisable(true);
            PauseTransition pauseErro = new PauseTransition(Duration.seconds(2.0));
            pauseErro.setOnFinished(ev -> {
                atualizarGUI();
                gerenciarTurnoIA();
                // Nota: Se a vez voltou para humano, o gerenciarTurnoIA habilita o botão.
            });
            pauseErro.play();
        }
        catch (MovimentoInvalidoException e) {
            aguardandoMovimentoHumano = false;
            labelStatus.setText("Erro: " + e.getMessage());
        }

        atualizarGUI();
    }

    /**
     * Handler para o clique visual no peão.
     *
     * @param peao O peão clicado.
     */
    private void tratarCliqueNoPeao(Peao peao) {
        // Ignora clique se for vez da IA
        if (motorJogo.getJogadorAtual() instanceof JogadorIA) return;

        if (!aguardandoMovimentoHumano) {
        labelStatus.setText("Você precisa rolar o dado antes de mover um peão.");
        return;
        }

        try {
            motorJogo.tentarMoverPeao(peao);
            labelStatus.setText("Movimento realizado!");

            aguardandoMovimentoHumano = false;

            // Verifica a vitória
            if (motorJogo.getEstado() == com.ludo.jogo.game.enums.EstadoJogo.ENCERRADO) {
                carregarTelaVitoria();
                return; // Para a execução aqui
            }

            // Turno finalizado, verifica proximo
            gerenciarTurnoIA();
        }
        catch (MovimentoInvalidoException e) {
            labelStatus.setText(e.getMessage());
        }

        atualizarGUI();
    }



    /**
     * Salva o jogo no slot correspondente ao botão clicado.
     *
     * @param event O evento do botão.
     */
    @FXML
    private void handleSalvarJogo(ActionEvent event) {
        Node node = (Node) event.getSource();
        String id = node.getId();
        int slot = Character.getNumericValue(id.charAt(id.length() - 1));

        GerenciadorDePersistencia.salvarJogo(motorJogo, slot);
        labelStatus.setText("Jogo salvo no Slot " + slot + "!");
    }

    /**
     * Carrega o jogo de um slot específico.
     *
     * @param slot O slot a ser carregado.
     * @throws SlotSaveIndisponivelException Se falhar ao carregar.
     */
    public void carregarDoSlot(int slot) throws SlotSaveIndisponivelException {
        this.motorJogo.carregarJogo(slot);
        labelStatus.setText("Jogo carregado do Slot " + slot + "!");
        desenharDado(motorJogo.getValorDadoAtual());
        atualizarGUI();
        gerenciarTurnoIA(); // Verifica de quem é a vez no save carregado
    }

    /**
     * Apaga o save do slot correspondente.
     *
     * @param event O evento do botão.
     */
    @FXML
    private void handleApagarSave(ActionEvent event) {
        Node node = (Node) event.getSource();
        String id = node.getId();
        int slot = Character.getNumericValue(id.charAt(id.length() - 1));

        boolean apagou = GerenciadorDePersistencia.apagarSave(slot);
        if (apagou) {
            labelStatus.setText("Save do Slot " + slot + " apagado!");
        }
        else {
            labelStatus.setText("Nenhum jogo salvo no Slot " + slot + " para apagar.");
        }
    }

    /**
     * Retorna para o menu inicial.
     *
     * @param event O evento do botão.
     */
    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ludo/jogo/gui/tela_inicial.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LUDO - Menu Inicial");
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
            labelStatus.setText("Erro ao voltar ao menu.");
        }
    }


    // Desenho e Atualização
    /**
     * Carrega a tela de vitória e exibe o vencedor.
     */
    private void carregarTelaVitoria() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ludo/jogo/gui/tela_vitoria.fxml"));
            Parent root = loader.load();

            // Configura o controlador da tela de vitória
            ControladorVitoria controller = loader.getController();
            Jogador vencedor = motorJogo.getJogadorAtual();

            // Passa o nome e a cor para a próxima tela
            controller.configurarVencedor(nomeJogador(vencedor), vencedor.getCor().toString());

            // Troca a cena
            Stage stage = (Stage) painelTabuleiro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LUDO - Fim de Jogo");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            labelStatus.setText("Erro ao carregar tela de vitória.");
        }
    }

    /** Atualiza a GUI redesenhando o tabuleiro. */
    private void atualizarGUI() {
        desenharTabuleiroBase();
        if (motorJogo.getEstado() == EstadoJogo.ENCERRADO) {
            mostrarTelaVitoria(motorJogo.getJogadorAtual());
            return;
        }
    }

    /** Desenha a base do tabuleiro e os elementos estáticos. */
    private void desenharTabuleiroBase() {
        painelTabuleiro.getChildren().clear();

        double w = painelTabuleiro.getWidth();
        double h = painelTabuleiro.getHeight();
        if (w <= 0) w = 600;
        if (h <= 0) h = 600;

        double size = Math.min(w, h);
        double cell = size / BOARD_CELLS;
        double ox = (w - size) / 2.0;
        double oy = (h - size) / 2.0;

        // Grid e Cores
        for (int row = 0; row < BOARD_CELLS; row++) {
            for (int col = 0; col < BOARD_CELLS; col++) {
                double x = ox + col * cell;
                double y = oy + row * cell;
                Color fill = Color.WHITE;

                // Bases
                if (row < 6 && col < 6) fill = Color.RED;
                else if (row < 6 && col > 8) fill = Color.LIMEGREEN;
                else if (row > 8 && col < 6) fill = Color.DODGERBLUE;
                else if (row > 8 && col > 8) fill = Color.GOLD;

                // Braços
                if (row == 7 && col >= 1 && col <= 5) fill = Color.RED;
                if (row == 7 && col >= 9 && col <= 13) fill = Color.GOLD;
                if (col == 7 && row >= 1 && row <= 5) fill = Color.LIMEGREEN;
                if (col == 7 && row >= 9 && row <= 13) fill = Color.DODGERBLUE;

                // Centro/Caminho
                if ((col >= 6 && col <= 8) || (row >= 6 && row <= 8)) {
                    if (!(row == 7 && (col >= 1 && col <= 5)) &&
                            !(row == 7 && (col >= 9 && col <= 13)) &&
                            !(col == 7 && (row >= 1 && row <= 5)) &&
                            !(col == 7 && (row >= 9 && row <= 13))) {
                        fill = Color.WHITE;
                    }
                }

                // Saídas coloridas
                if (row == 6 && col == 1) fill = Color.RED;
                if (row == 1 && col == 8) fill = Color.LIMEGREEN;
                if (row == 8 && col == 13) fill = Color.GOLD;
                if (row == 13 && col == 6) fill = Color.DODGERBLUE;

                // Casas Brancas Específicas
                if ((row == 5 && col == 5) || (row == 5 && col == 9) ||
                        (row == 9 && col == 5) || (row == 9 && col == 9)) {
                    fill = Color.WHITE;
                }

                Rectangle r = new Rectangle(x, y, cell, cell);
                r.setFill(fill);
                r.setStroke(Color.BLACK);
                painelTabuleiro.getChildren().add(r);

            }
        }

        // Elementos graficos
        drawStarOutline(ox, oy, cell, 2, 6);
        drawStarOutline(ox, oy, cell, 6, 12);
        drawStarOutline(ox, oy, cell, 12, 8);
        drawStarOutline(ox, oy, cell, 8, 2);

        drawArrowInCell(ox, oy, cell, 6, 0, "RIGHT", Color.RED);
        drawArrowInCell(ox, oy, cell, 0, 8, "DOWN", Color.LIMEGREEN);
        drawArrowInCell(ox, oy, cell, 8, 14, "LEFT", Color.GOLD);
        drawArrowInCell(ox, oy, cell, 14, 6, "UP", Color.DODGERBLUE);

        // Triangulos centrais
        double cx = ox + 6 * cell;
        double cy = oy + 6 * cell;
        double cmx = cx + 1.5 * cell;
        double cmy = cy + 1.5 * cell;

        painelTabuleiro.getChildren().addAll(
                criarTriangulo(cx, cy, cx, cy+3*cell, cmx, cmy, Color.RED),
                criarTriangulo(cx, cy, cx+3*cell, cy, cmx, cmy, Color.LIMEGREEN),
                criarTriangulo(cx+3*cell, cy, cx+3*cell, cy+3*cell, cmx, cmy, Color.GOLD),
                criarTriangulo(cx, cy+3*cell, cx+3*cell, cy+3*cell, cmx, cmy, Color.DODGERBLUE)
        );

        // Bases brancas
        drawBase(ox, oy, cell, 0, 0);
        drawBase(ox, oy, cell, 0, 9);
        drawBase(ox, oy, cell, 9, 0);
        drawBase(ox, oy, cell, 9, 9);

        desenharPeoes(ox, oy, cell);
        desenharContadoresFinais(ox, oy, cell);
    }

    /** Desenha os peões sobre o tabuleiro. */
    private void desenharPeoes(double ox, double oy, double cell) {
        mapaPeoes.clear();
        if (motorJogo == null) return;

        Tabuleiro tabuleiro = motorJogo.getTabuleiro();
        if (tabuleiro == null) return;

        // 1) Primeiro tratamos os peões na BASE normalmente (um círculo por peão)
        for (Jogador jogador : motorJogo.getJogadores()) {
            Color corVisual = converterCor(jogador.getCor());
            List<Peao> peoes = jogador.getPeoes();

            for (int i = 0; i < peoes.size(); i++) {
                Peao peao = peoes.get(i);

                if (peao.getEstado() == EstadoPeao.FINALIZADO) {
                    continue;
                }

                if (peao.getEstado() == EstadoPeao.BASE) {
                    CellPos posBase = posicaoNaBase(jogador.getCor(), i);
                    double cxBase = ox + (posBase.col + 0.5) * cell;
                    double cyBase = oy + (posBase.row + 0.5) * cell;
                    double raioBase = cell * 0.35;

                    Circle cBase = new Circle(cxBase, cyBase, raioBase);
                    cBase.setFill(corVisual);
                    cBase.setStroke(Color.BLACK);
                    cBase.setStrokeWidth(raioBase * 0.3);
                    cBase.setOnMouseClicked(e -> tratarCliqueNoPeao(peao));

                    painelTabuleiro.getChildren().add(cBase);
                    mapaPeoes.put(peao, cBase);

                    Circle centroBase = new Circle(cxBase, cyBase, raioBase * 0.4);
                    centroBase.setFill(Color.BLACK);
                    centroBase.setMouseTransparent(true);
                    painelTabuleiro.getChildren().add(centroBase);
                }
            }
        }

        // 2) Agora agrupamos os peões que estão EM CASA (circuito ou reta final) pela casa
        Map<Casa, List<Peao>> gruposPorCasa = new HashMap<>();

        for (Jogador jogador : motorJogo.getJogadores()) {
            List<Peao> peoes = jogador.getPeoes();
            for (Peao peao : peoes) {
                if (peao.getEstado() == EstadoPeao.FINALIZADO ||
                        peao.getEstado() == EstadoPeao.BASE) {
                    continue;
                }
                Casa casa = peao.getCasa();
                if (casa == null) continue;

                gruposPorCasa
                        .computeIfAbsent(casa, k -> new ArrayList<>())
                        .add(peao);
            }
        }

        // 3) Para cada casa, desenhamos o peão "principal" grande
        //    e os marcadores pequenos no topo para TODOS os peões ali
        for (Map.Entry<Casa, List<Peao>> entry : gruposPorCasa.entrySet()) {
            List<Peao> listaPeoes = entry.getValue();
            if (listaPeoes.isEmpty()) continue;

            // usamos o primeiro da lista como "peão principal"
            Peao peaoPrincipal = listaPeoes.get(0);
            Cor corPrincipal = peaoPrincipal.getCor();
            Color corVisualPrincipal = converterCor(corPrincipal);

            // posição da casa no grid (qualquer peão dela serve pra obter a CellPos)
            CellPos posCasa = obterPosicaoPeao(peaoPrincipal, 0);
            if (posCasa == null) continue;

            double cx = ox + (posCasa.col + 0.5) * cell;
            double cy = oy + (posCasa.row + 0.5) * cell;
            double raio = cell * 0.35;

            // círculo grande do peão principal
            Circle circuloPrincipal = new Circle(cx, cy, raio);
            circuloPrincipal.setFill(corVisualPrincipal);
            circuloPrincipal.setStroke(Color.BLACK);
            circuloPrincipal.setStrokeWidth(raio * 0.3);
            circuloPrincipal.setOnMouseClicked(e -> tratarCliqueNoPeao(peaoPrincipal));

            painelTabuleiro.getChildren().add(circuloPrincipal);
            mapaPeoes.put(peaoPrincipal, circuloPrincipal);

            Circle centroPrincipal = new Circle(cx, cy, raio * 0.4);
            centroPrincipal.setFill(Color.BLACK);
            centroPrincipal.setMouseTransparent(true);
            painelTabuleiro.getChildren().add(centroPrincipal);

            // 4) marcadores pequenos para TODOS os peões que estão nessa casa
            int n = listaPeoes.size();
            if (n == 1) {
                // só o principal, não precisa marker extra
                continue;
            }

            double raioMini = cell * 0.13;
            // centralizamos a fileira de marcadores pequenos acima do peão grande
            double larguraTotal = (n - 1) * (raioMini * 2.0);
            double startX = cx - larguraTotal / 2.0;
            double cyMini = cy - raio * 0.95; // acima do círculo grande

            for (int k = 0; k < n; k++) {
                Peao peaoMarcado = listaPeoes.get(k);
                Color corMini = converterCor(peaoMarcado.getCor());

                double cxMini = startX + k * (raioMini * 2.0);

                Circle mini = new Circle(cxMini, cyMini, raioMini);
                mini.setFill(corMini);
                mini.setStroke(Color.BLACK);
                mini.setStrokeWidth(raioMini * 0.4);

                // AGORA: cada mini-círculo responde ao clique do seu próprio peão
                mini.setOnMouseClicked(e -> tratarCliqueNoPeao(peaoMarcado));

                painelTabuleiro.getChildren().add(mini);
            }
        }
    }

    // ===================== DESENHO DO DADO =====================

    private void desenharDado(int valor) {
        if (paneDado == null) return;

        // 1. Limpa o desenho anterior
        paneDado.getChildren().clear();

        // 2. Debug para verificar se o valor está chegando corretamente (olhe no console)
        System.out.println("Desenhando dado com valor: " + valor);

        // 3. Captura dimensões reais ou força um padrão se o JavaFX ainda não calculou layout
        double w = paneDado.getWidth();
        double h = paneDado.getHeight();

        // Se o layout ainda não calculou (comum no início), usa o PrefSize do FXML ou um padrão
        if (w <= 0) w = paneDado.getPrefWidth() > 0 ? paneDado.getPrefWidth() : 100;
        if (h <= 0) h = paneDado.getPrefHeight() > 0 ? paneDado.getPrefHeight() : 100;

        double size = Math.min(w, h) * 0.9; // Usa 90% do espaço disponível
        double x0 = (w - size) / 2.0;
        double y0 = (h - size) / 2.0;

        // 4. Desenha o Fundo (Quadrado Branco)
        Rectangle fundo = new Rectangle(x0, y0, size, size);
        fundo.setArcWidth(size * 0.2);
        fundo.setArcHeight(size * 0.2);
        fundo.setFill(Color.WHITE);
        fundo.setStroke(Color.BLACK);
        fundo.setStrokeWidth(2); // Borda um pouco mais grossa para visibilidade
        paneDado.getChildren().add(fundo);

        // Se valor for 0 (início) ou inválido, para aqui (fica apenas o quadrado branco)
        if (valor < 1 || valor > 6) return;

        // 5. Configuração das "bolinhas" (Pips)
        double r = size * 0.08; // Raio da bolinha
        double cx = x0 + size / 2.0; // Centro X
        double cy = y0 + size / 2.0; // Centro Y
        double offset = size * 0.25; // Distância do centro

        // Mapeamento das posições
        // Centro
        if (valor == 1 || valor == 3 || valor == 5) {
            addPip(cx, cy, r);
        }
        // Superior Esquerda e Inferior Direita
        if (valor >= 2 && valor <= 6) {
            addPip(cx - offset, cy - offset, r);
            addPip(cx + offset, cy + offset, r);
        }
        // Superior Direita e Inferior Esquerda
        if (valor >= 4 && valor <= 6) {
            addPip(cx + offset, cy - offset, r);
            addPip(cx - offset, cy + offset, r);
        }
        // Meio Esquerda e Meio Direita (apenas no 6)
        if (valor == 6) {
            addPip(cx - offset, cy, r);
            addPip(cx + offset, cy, r);
        }
    }

    private void addPip(double x, double y, double r) {
        Circle c = new Circle(x, y, r);
        c.setFill(Color.BLACK);
        paneDado.getChildren().add(c);
    }


    // Helpers de Desenho
    // private boolean isStarCell(int row, int col) {
    //     return (row == 2 && col == 6)   // estrela vermelha
    //             || (row == 6 && col == 12)  // estrela verde
    //             || (row == 12 && col == 8)  // estrela amarela
    //             || (row == 8 && col == 2);  // estrela azul
    // }

    private void drawBase(double ox, double oy, double cell, int rb, int cb) {
        double bx = ox + (cb + 1) * cell;
        double by = oy + (rb + 1) * cell;
        double s = 4 * cell;
        Rectangle r = new Rectangle(bx, by, s, s);
        r.setFill(Color.WHITE);
        r.setStroke(Color.BLACK);
        painelTabuleiro.getChildren().add(r);
    }

    private void drawStarOutline(double ox, double oy, double cell, int r, int c) {
        double cx = ox + (c + 0.5) * cell;
        double cy = oy + (r + 0.5) * cell;
        double out = cell * 0.35;
        double inn = cell * 0.17;
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double ang = Math.toRadians(-90 + i * 36);
            double rad = (i % 2 == 0) ? out : inn;
            star.getPoints().addAll(cx + rad * Math.cos(ang), cy + rad * Math.sin(ang));
        }
        star.setFill(Color.TRANSPARENT);
        star.setStroke(Color.BLACK);
        painelTabuleiro.getChildren().add(star);
    }

    private void drawArrowInCell(double ox, double oy, double cell, int r, int c, String d, Color color) {
        double cx = ox + (c + 0.5) * cell;
        double cy = oy + (r + 0.5) * cell;
        double s = cell * 0.35;
        Polygon arrow = new Polygon();
        switch (d) {
            case "RIGHT": arrow.getPoints().addAll(cx-s, cy-s, cx-s, cy+s, cx+s, cy); break;
            case "LEFT":  arrow.getPoints().addAll(cx+s, cy-s, cx+s, cy+s, cx-s, cy); break;
            case "UP":    arrow.getPoints().addAll(cx-s, cy+s, cx+s, cy+s, cx, cy-s); break;
            case "DOWN":  arrow.getPoints().addAll(cx-s, cy-s, cx+s, cy-s, cx, cy+s); break;
        }
        arrow.setFill(color);
        arrow.setStroke(Color.BLACK);
        painelTabuleiro.getChildren().add(arrow);
    }

    private Polygon criarTriangulo(double x1, double y1, double x2, double y2, double x3, double y3, Color c) {
        Polygon p = new Polygon(x1, y1, x2, y2, x3, y3);
        p.setFill(c);
        return p;
    }

    private Color converterCor(Cor cor) {
        switch (cor) {
            case VERMELHO: return Color.RED;
            case VERDE:    return Color.LIMEGREEN;
            case AZUL:     return Color.DODGERBLUE;
            case AMARELO:  return Color.GOLD;
            default:       return Color.GRAY;
        }
    }
    private void desenharContadoresFinais(double ox, double oy, double cell) {
        int finVermelho = contarFinalizados(Cor.VERMELHO);
        int finVerde    = contarFinalizados(Cor.VERDE);
        int finAmarelo  = contarFinalizados(Cor.AMARELO);
        int finAzul     = contarFinalizados(Cor.AZUL);

        double cx = ox + (7 + 0.5) * cell;
        double cy = oy + (7 + 0.5) * cell;

        // Para centralizar melhor o texto verticalmente
        double ajusteVertical = cell * 0.15;

        // Tamanho do texto proporcional ao tamanho do tabuleiro
        String estilo = "-fx-font-size: " + (cell * 0.55) +
                "px; -fx-font-weight: bold; -fx-fill: black;";

        // ----- VERMELHO (esquerda) -----
        if (finVermelho > 0) {
            Text t = new Text(String.valueOf(finVermelho));
            t.setStyle(estilo);
            t.setX(cx - cell * 0.65);
            t.setY(cy + ajusteVertical);
            painelTabuleiro.getChildren().add(t);
        }

        // ----- VERDE (cima) -----
        if (finVerde > 0) {
            Text t = new Text(String.valueOf(finVerde));
            t.setStyle(estilo);
            t.setX(cx - (cell * 0.18));
            t.setY(cy - (cell * 0.55) + ajusteVertical);
            painelTabuleiro.getChildren().add(t);
        }

        // ----- AMARELO (direita) -----
        if (finAmarelo > 0) {
            Text t = new Text(String.valueOf(finAmarelo));
            t.setStyle(estilo);
            t.setX(cx + cell * 0.35);
            t.setY(cy + ajusteVertical);
            painelTabuleiro.getChildren().add(t);
        }

        // ----- AZUL (baixo) -----
        if (finAzul > 0) {
            Text t = new Text(String.valueOf(finAzul));
            t.setStyle(estilo);
            t.setX(cx - (cell * 0.18));
            t.setY(cy + (cell * 0.9) + ajusteVertical);
            painelTabuleiro.getChildren().add(t);
        }
    }

    // Helpers de Mapeamento

    private int contarFinalizados(Cor cor) {
        if (motorJogo == null || motorJogo.getJogadores() == null) return 0;

        int count = 0;
        for (Jogador j : motorJogo.getJogadores()) {
            if (j.getCor() != cor) continue;
            for (Peao p : j.getPeoes()) {
                if (p.getEstado() == EstadoPeao.FINALIZADO) {
                    count++;
                }
            }
        }
        return count;
    }

    private CellPos obterPosicaoPeao(Peao peao, int i) {
        EstadoPeao est = peao.getEstado();
        Cor cor = peao.getCor();
        Casa casa = peao.getCasa();
        Tabuleiro tab = motorJogo.getTabuleiro();

        if (est == EstadoPeao.FINALIZADO) {
            return posicaoNaBase(cor, i);
        }
        if (est == EstadoPeao.BASE) return posicaoNaBase(cor, i);
        if (tab.isCasaDaRetaFinal(cor, casa)) {
            int idx = tab.getIndiceRetaFinal(cor, casa);
            if (idx >= 0) return posicaoRetaFinal(cor, idx);
        }
        if (tab.isCasaDoCircuito(casa)) {
            int idx = tab.getIndiceCircuito(casa);
            if (idx >= 0) return posicaoCircuito(idx);
        }
        return null;
    }

    private CellPos posicaoNaBase(Cor cor, int idx) {
        int lr = idx / 2; int lc = idx % 2;
        int br = 0, bc = 0;
        if (cor == Cor.VERDE) bc = 9;
        else if (cor == Cor.AZUL) br = 9;
        else if (cor == Cor.AMARELO) { br = 9; bc = 9; }
        return new CellPos(br + 1.5 + lr * 2.0, bc + 1.5 + lc * 2.0);
    }

    private CellPos posicaoRetaFinal(Cor cor, int idx) {
        if (idx == 5) return new CellPos(7, 7);
        switch (cor) {
            case VERMELHO: return new CellPos(7, 1 + idx);
            case VERDE:    return new CellPos(1 + idx, 7);
            case AMARELO:  return new CellPos(7, 13 - idx);
            case AZUL:     return new CellPos(13 - idx, 7);
            case NENHUMA:  return null;
        }
        return null;
    }

    private CellPos posicaoCircuito(int idx) {
        final CellPos[] C = new CellPos[56];
        C[0] = new CellPos(6,1); C[1] = new CellPos(6,2); C[2] = new CellPos(6,3);
        C[3] = new CellPos(6,4); C[4] = new CellPos(6,5); C[5] = new CellPos(5,5);
        C[6] = new CellPos(5,6); C[7] = new CellPos(4,6); C[8] = new CellPos(3,6);
        C[9] = new CellPos(2,6); C[10]= new CellPos(1,6); C[11]= new CellPos(0,6);
        C[12]= new CellPos(0,7); C[13]= new CellPos(0,8); C[14]= new CellPos(1,8);
        C[15]= new CellPos(2,8); C[16]= new CellPos(3,8); C[17]= new CellPos(4,8);
        C[18]= new CellPos(5,8); C[19]= new CellPos(5,9); C[20]= new CellPos(6,9);
        C[21]= new CellPos(6,10);C[22]= new CellPos(6,11);C[23]= new CellPos(6,12);
        C[24]= new CellPos(6,13);C[25]= new CellPos(6,14);C[26]= new CellPos(7,14);
        C[27]= new CellPos(8,14);C[28]= new CellPos(8,13);C[29]= new CellPos(8,12);
        C[30]= new CellPos(8,11);C[31]= new CellPos(8,10);C[32]= new CellPos(8,9);
        C[33]= new CellPos(9,9); C[34]= new CellPos(9,8); C[35]= new CellPos(10,8);
        C[36]= new CellPos(11,8);C[37]= new CellPos(12,8);C[38]= new CellPos(13,8);
        C[39]= new CellPos(14,8);C[40]= new CellPos(14,7);C[41]= new CellPos(14,6);
        C[42]= new CellPos(13,6);C[43]= new CellPos(12,6);C[44]= new CellPos(11,6);
        C[45]= new CellPos(10,6);C[46]= new CellPos(9,6); C[47]= new CellPos(9,5);
        C[48]= new CellPos(8,5); C[49]= new CellPos(8,4); C[50]= new CellPos(8,3);
        C[51]= new CellPos(8,2); C[52]= new CellPos(8,1); C[53]= new CellPos(8,0);
        C[54]= new CellPos(7,0); C[55]= new CellPos(6,0);

        if (idx >= 0 && idx < 56) return C[idx];
        return new CellPos(0,0);
    }
}