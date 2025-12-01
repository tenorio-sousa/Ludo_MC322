package com.ludo.jogo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** Controlador da tela de vitória. */
public class ControladorVitoria {

    // ATRIBUTOS

    @FXML private Label labelVitoria;
    @FXML private Label labelDescricao;


    // MÉTODOS

    /**
     * Configura os textos da tela com as informações do vencedor.
     *
     * @param nomeJogador Nome do jogador que venceu.
     * @param cor Cor do jogador vencedor.
     */
    public void configurarVencedor(String nomeJogador, String cor) {
        labelVitoria.setText("VITÓRIA!!!");
        labelDescricao.setText(nomeJogador + " (" + cor + ") venceu!");
    }

    /**
     * Retorna ao menu inicial.
     *
     * @param event Evento do botão.
     */
    @FXML
    private void handleVoltarMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/ludo/jogo/gui/tela_inicial.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LUDO - Menu Inicial");
            stage.show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}