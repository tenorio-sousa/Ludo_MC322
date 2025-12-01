package com.ludo.jogo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Classe principal que inicia a aplicação JavaFX. */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        String fxmlPath = "/com/ludo/jogo/gui/tela_inicial.fxml";

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("MC322 - Ludo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

// 24/11 - 18:30h