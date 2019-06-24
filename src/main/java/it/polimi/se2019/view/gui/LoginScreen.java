package it.polimi.se2019.view.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Properties;

public class LoginScreen extends Application {
    @FXML ComboBox<String> connectionType;
    @FXML Button loginButton;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML TextField port;
    @FXML TextField url;
    @FXML CheckBox existingGame;
    @FXML ComboBox<String> mode;

    private static Stage primaryStage;

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/LoginScreen.fxml"));
        Scene scene = new Scene(root,400,400);
        stage.setTitle("LoginScreen");
        stage.setScene(scene);
        stage.show();
    }


    public void login(){
        GUIView view = new GUIView();
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("url", url.getText());
        connectionProperties.setProperty("port", port.getText());

        String connection = connectionType.getValue() != null ? connectionType.getValue() : connectionType.getPromptText();
        String gameMode = mode.getValue() != null ? mode.getValue() : mode.getPromptText();
        view.setGameMode(gameMode);
        view.setupConnection(connection,username.getText(),password.getText(),connectionProperties,existingGame.isSelected(),gameMode);
        //TODO:prevent further input and display some kind of loading screen when it gets set to WAITING
    }

    public static void main(String[] args){
        launch();
    }
}
