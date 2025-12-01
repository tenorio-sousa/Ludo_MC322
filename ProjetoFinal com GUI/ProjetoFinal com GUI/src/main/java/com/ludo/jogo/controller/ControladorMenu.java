package com.ludo.jogo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ludo.jogo.game.ConfigJogo;
import com.ludo.jogo.game.Jogador;
import com.ludo.jogo.game.JogadorHumano;
import com.ludo.jogo.game.JogadorIA;
import com.ludo.jogo.game.enums.Cor;
import com.ludo.jogo.game.exceptions.SlotSaveIndisponivelException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Controlador da tela de Menu Inicial. */
public class ControladorMenu {

    // ATRIBUTOS

    @FXML private BorderPane rootPane;
    @FXML private VBox menuPrincipal; // definido no FXML

    @FXML private Button btnJogadorVsJogador;
    @FXML private Button btnJogadorVsMaquina;
    @FXML private Button btnCarregarJogo;
    @FXML private Button btnRegras;
    @FXML private Button btnSair;

    // Estado temporário para configurações
    private int qtdJogadoresJxJ;
    private Map<Integer, Cor> coresPorJogadorJxJ;

    private int qtdMaquinasJxM;
    private Cor corHumanoJxM;
    private List<Cor> coresMaquinasJxM;


    // MÉTODOS

    // Inicialização

    /**
     * Inicializa o controlador e configura a escalabilidade da tela.
     */
    @FXML
    public void initialize() {
        // Escala o menu conforme o tamanho da janela
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, ov, nv) -> ajustarEscala());
                newScene.heightProperty().addListener((o, ov, nv) -> ajustarEscala());
                ajustarEscala();
            }
        });
    }


    // Menu Principal (Eventos)

    /** Trata o clique no botão "Jogador vs Jogador". */
    @FXML
    private void onJogadorVsJogador() {
        mostrarSubmenuQtdJogadores();
    }

    /** Trata o clique no botão "Jogador vs Máquina". */
    @FXML
    private void onJogadorVsMaquina() {
        mostrarSubmenuQtdMaquinas();
    }

    /** Trata o clique no botão "Carregar Jogo". */
    @FXML
    private void onCarregarJogo() {
        mostrarTelaCarregarJogo();
    }

    /** Trata o clique no botão "Regras". */
    @FXML
    private void onRegras() {
        mostrarTelaRegras();
    }

    /** Trata o clique no botão "Sair", fechando a aplicação. */
    @FXML
    private void onSair() {
        Stage stage = (Stage) btnSair.getScene().getWindow();
        stage.close();
    }

    /** Restaura a visualização do menu principal no centro da tela. */
    private void mostrarMenuPrincipal() {
        rootPane.setCenter(menuPrincipal);
    }


    // Fluxo Jogador x Jogador

    /** Exibe o submenu para escolher a quantidade de jogadores humanos. */
    private void mostrarSubmenuQtdJogadores() {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Jogador x Jogador");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Selecione a quantidade de jogadores humanos:");
        subtitulo.setStyle("-fx-font-size: 14px;");

        Button b2 = criarBotaoMenu("2 jogadores", () -> iniciarFluxoJxJ(2));
        Button b3 = criarBotaoMenu("3 jogadores", () -> iniciarFluxoJxJ(3));
        Button b4 = criarBotaoMenu("4 jogadores", () -> iniciarFluxoJxJ(4));
        Button voltar = criarBotaoMenu("Voltar", this::mostrarMenuPrincipal);

        box.getChildren().addAll(titulo, subtitulo, b2, b3, b4, voltar);

        rootPane.setCenter(box);
    }

    /**
     * Inicia a configuração para o número selecionado de jogadores.
     *
     * @param qtdJogadores Quantidade de humanos na partida.
     */
    private void iniciarFluxoJxJ(int qtdJogadores) {
        this.qtdJogadoresJxJ = qtdJogadores;
        this.coresPorJogadorJxJ = new LinkedHashMap<>();
        mostrarTelaEscolherCorJogador(1, new ArrayList<>(getCoresValidas()));
    }

    /**
     * Exibe a tela de seleção de cor para um jogador específico.
     *
     * @param numJogador O número do jogador atual (1, 2, 3...).
     * @param coresDisponiveis Lista de cores que ainda não foram escolhidas.
     */
    private void mostrarTelaEscolherCorJogador(int numJogador, List<Cor> coresDisponiveis) {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Jogador x Jogador");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Jogador " + numJogador + ": escolha uma cor");
        subtitulo.setStyle("-fx-font-size: 14px;");

        HBox linhaCores = new HBox(10);
        linhaCores.setAlignment(Pos.CENTER);

        for (Cor cor : coresDisponiveis) {
            Button btnCor = new Button(cor.name());
            btnCor.setPrefWidth(120);
            btnCor.setOnAction(e -> {
                // salva cor escolhida para este jogador
                coresPorJogadorJxJ.put(numJogador, cor);

                // lista de cores disponíveis para o próximo jogador
                List<Cor> novasDisponiveis = new ArrayList<>(coresDisponiveis);
                novasDisponiveis.remove(cor);

                if (numJogador < qtdJogadoresJxJ) {
                    // vai para o próximo jogador
                    mostrarTelaEscolherCorJogador(numJogador + 1, novasDisponiveis);
                
                } else {
                    // terminou de configurar todos
                    finalizarConfigJxJ();
                }
            });
            linhaCores.getChildren().add(btnCor);
        }

        Button voltar = criarBotaoMenu("Voltar", () -> {
            if (numJogador == 1) {
                // primeira escolha -> volta para tela de 1/2/3/4 jogadores
                mostrarSubmenuQtdJogadores();
            
            } else {
                // removemos a cor do jogador anterior para ele poder escolher de novo
                coresPorJogadorJxJ.remove(numJogador - 1);

                // recalculamos as cores disponíveis para o jogador anterior:
                List<Cor> coresValidas = getCoresValidas();
                for (int j = 1; j <= numJogador - 2; j++) {
                    Cor c = coresPorJogadorJxJ.get(j);
                    if (c != null) {
                        coresValidas.remove(c);
                    }
                }

                // volta para a tela de escolha de cor do jogador anterior
                mostrarTelaEscolherCorJogador(numJogador - 1, coresValidas);
            }
        });

        box.getChildren().addAll(titulo, subtitulo, linhaCores, voltar);

        rootPane.setCenter(box);
    }

    /** Finaliza a configuração JxJ e inicia o jogo. */
    private void finalizarConfigJxJ() {
        // Monta lista de tipos e cores para o MotorJogo
        List<Class<? extends Jogador>> tipos = new ArrayList<>();
        List<Cor> cores = new ArrayList<>();

        for (int i = 1; i <= qtdJogadoresJxJ; i++) {
            tipos.add(JogadorHumano.class);                 // todos humanos
            cores.add(coresPorJogadorJxJ.get(i));           // cor escolhida no menu
        }

        ConfigJogo.configurar(tipos, cores);

        System.out.println("Modo: Jogador x Jogador");
        System.out.println("Qtd jogadores humanos: " + qtdJogadoresJxJ);
        for (int i = 1; i <= qtdJogadoresJxJ; i++) {
            System.out.println("Jogador " + i + " -> " + coresPorJogadorJxJ.get(i));
        }

        abrirTelaJogo(null); // novo jogo
    }


    // Fluxo Jogador x Máquina

    /** Exibe o submenu para escolher a quantidade de IAs. */
    private void mostrarSubmenuQtdMaquinas() {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Jogador x Máquina");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Selecione a quantidade de máquinas:");
        subtitulo.setStyle("-fx-font-size: 14px;");

        Button b1 = criarBotaoMenu("1 Máquina", () -> iniciarFluxoJxM(1));
        Button b2 = criarBotaoMenu("2 Máquinas", () -> iniciarFluxoJxM(2));
        Button b3 = criarBotaoMenu("3 Máquinas", () -> iniciarFluxoJxM(3));
        Button voltar = criarBotaoMenu("Voltar", this::mostrarMenuPrincipal);

        box.getChildren().addAll(titulo, subtitulo, b1, b2, b3, voltar);

        rootPane.setCenter(box);
    }

    /**
     * Inicia o fluxo de configuração para JxM.
     *
     * @param qtdMaquinas Quantidade de oponentes controlados pela IA.
     */
    private void iniciarFluxoJxM(int qtdMaquinas) {
        this.qtdMaquinasJxM = qtdMaquinas;
        mostrarTelaEscolherCorHumanoJxM(new ArrayList<>(getCoresValidas()));
    }

    /**
     * Exibe a tela para o humano escolher sua cor.
     *
     * @param coresDisponiveis Lista de cores disponíveis.
     */
    private void mostrarTelaEscolherCorHumanoJxM(List<Cor> coresDisponiveis) {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Jogador x Máquina");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Escolha a cor do jogador humano:");
        subtitulo.setStyle("-fx-font-size: 14px;");

        HBox linhaCores = new HBox(10);
        linhaCores.setAlignment(Pos.CENTER);

        for (Cor cor : coresDisponiveis) {
            Button btnCor = new Button(cor.name());
            btnCor.setPrefWidth(120);
            btnCor.setOnAction(e -> {
                corHumanoJxM = cor;
                List<Cor> restantes = new ArrayList<>(coresDisponiveis);
                restantes.remove(cor);
                definirCoresMaquinas(restantes);
                finalizarConfigJxM();
            });
            linhaCores.getChildren().add(btnCor);
        }

        Button voltar = criarBotaoMenu("Voltar", this::mostrarSubmenuQtdMaquinas);

        box.getChildren().addAll(titulo, subtitulo, linhaCores, voltar);

        rootPane.setCenter(box);
    }

    /**
     * Atribui automaticamente cores para as IAs baseadas nas restantes.
     *
     * @param coresRestantes Lista de cores que o humano não escolheu.
     */
    private void definirCoresMaquinas(List<Cor> coresRestantes) {
        coresMaquinasJxM = new ArrayList<>();
        for (int i = 0; i < qtdMaquinasJxM && i < coresRestantes.size(); i++) {
            coresMaquinasJxM.add(coresRestantes.get(i));
        }
    }

    /** Finaliza a configuração JxM e inicia o jogo. */
    private void finalizarConfigJxM() {
        // 1 humano + N máquinas
        List<Class<? extends Jogador>> tipos = new ArrayList<>();
        List<Cor> cores = new ArrayList<>();

        tipos.add(JogadorHumano.class);
        cores.add(corHumanoJxM);

        for (int i = 0; i < qtdMaquinasJxM; i++) {
            tipos.add(JogadorIA.class);
            cores.add(coresMaquinasJxM.get(i));
        }

        ConfigJogo.configurar(tipos, cores);

        System.out.println("Modo: Jogador x Máquina");
        System.out.println("Humano -> " + corHumanoJxM);
        for (int i = 0; i < coresMaquinasJxM.size(); i++) {
            System.out.println("Máquina " + (i + 1) + " -> " + coresMaquinasJxM.get(i));
        }

        abrirTelaJogo(null); // novo jogo
    }


    // Fluxo Carregar Jogo

    /** Exibe a tela de seleção de slot para carregamento. */
    private void mostrarTelaCarregarJogo() {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Carregar Jogo");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Selecione o slot de save para carregar:");
        subtitulo.setStyle("-fx-font-size: 14px;");

        Button s1 = criarBotaoMenu("Slot 1", () -> carregarDoSlot(1));
        Button s2 = criarBotaoMenu("Slot 2", () -> carregarDoSlot(2));
        Button s3 = criarBotaoMenu("Slot 3", () -> carregarDoSlot(3));
        Button s4 = criarBotaoMenu("Slot 4", () -> carregarDoSlot(4));
        Button voltar = criarBotaoMenu("Voltar", this::mostrarMenuPrincipal);

        box.getChildren().addAll(titulo, subtitulo, s1, s2, s3, s4, voltar);

        rootPane.setCenter(box);
    }

    /**
     * Tenta carregar o jogo do slot especificado.
     *
     * @param slot O número do slot a carregar.
     */
    private void carregarDoSlot(int slot) {
        try {
            abrirTelaJogo(slot);
        }
        catch (Exception e) {
            System.err.println("Erro ao carregar slot " + slot + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Fluxo Regras

    /** Exibe a tela de regras. */
    private void mostrarTelaRegras() {
        VBox box = criarVBoxBase();

        Label titulo = new Label("Regras do Ludo");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label texto = new Label(
                "• Cada jogador possui 4 peões.\n" +
                        "• Para sair da base é necessário tirar 6 no dado.\n" +
                        "• O objetivo é levar todos os seus peões até a reta final da sua cor.\n" +
                        "• Casas com estrela são consideradas casas seguras.\n" +
                        "• Se você cair em uma casa ocupada por um adversário (fora das casas seguras),\n" +
                        "  o peão dele é capturado e volta para a base.\n" +
                        "• Se dois peões estiverem na mesma casa, essa casa fica bloqueada\n" +
                        "  e nenhum peão pode entrar nela."
        );

        texto.setStyle("-fx-font-size: 14px;");
        texto.setWrapText(true);

        Button voltar = criarBotaoMenu("Voltar", this::mostrarMenuPrincipal);

        box.getChildren().addAll(titulo, texto, voltar);

        rootPane.setCenter(box);
    }


    // Métodos Auxiliares

    /**
     * Abre a tela do tabuleiro (tela_jogo.fxml).
     *
     * @param slotParaCarregar Slot do save (null se for Novo Jogo).
     */
    private void abrirTelaJogo(Integer slotParaCarregar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ludo/jogo/gui/tela_jogo.fxml")
            );
            Parent root = loader.load();

            // pega o controlador da tela de jogo
            ControladorJogo controladorJogo = loader.getController();

            if (slotParaCarregar != null) {
                try {
                    controladorJogo.carregarDoSlot(slotParaCarregar);
                }
                catch (SlotSaveIndisponivelException e) {
                    // Usa exatamente a mensagem da exceção lançada pelo GerenciadorDePersistencia
                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setTitle("Carregar jogo");
                    alerta.setHeaderText("Slot indisponível");
                    alerta.setContentText(e.getMessage());
                    alerta.showAndWait();
                    return; // não troca para a tela do tabuleiro
                }
            }
            // se slot == null, o initialize() do ControladorJogo já cria um novo jogo

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("MC322 - Ludo");
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna a lista de cores válidas (ignorando NENHUMA).
     *
     * @return Lista de cores enum.
     */
    private List<Cor> getCoresValidas() {
        List<Cor> lista = new ArrayList<>();
        for (Cor c : Cor.values()) {
            if (!c.name().equalsIgnoreCase("NENHUMA")) {
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * Cria um VBox padrão centralizado para as telas internas.
     *
     * @return VBox configurado.
     */
    private VBox criarVBoxBase() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40, 40, 40, 40));
        return box;
    }

    /**
     * Cria botão padrão do menu/submenus.
     *
     * @param texto Texto do botão.
     * @param acao Runnable a ser executado no clique.
     * @return Button configurado.
     */
    private Button criarBotaoMenu(String texto, Runnable acao) {
        Button btn = new Button(texto);
        btn.setPrefWidth(220);
        btn.setOnAction(e -> acao.run());
        return btn;
    }

    /** Ajusta a escala do menu conforme o tamanho da janela. */
    private void ajustarEscala() {
        if (rootPane.getScene() == null) {
            return;
        }

        double baseWidth = 800.0;
        double baseHeight = 600.0;

        double width = rootPane.getScene().getWidth();
        double height = rootPane.getScene().getHeight();

        double scaleX = width / baseWidth;
        double scaleY = height / baseHeight;
        double scale = Math.min(scaleX, scaleY);

        rootPane.setScaleX(scale);
        rootPane.setScaleY(scale);
    }
}