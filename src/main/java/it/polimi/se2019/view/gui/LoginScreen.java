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
    @FXML ListView connectionType;
    @FXML Button loginButton;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML TextField port;
    @FXML TextField url;
    @FXML CheckBox existingGame;
    @FXML ComboBox<String> mode;
    private String gameMode = "NORMAL";
    private String selectedConnection;

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

    public void selectConnection(){
        selectedConnection = (String)connectionType.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void selectMode(){
        if(mode.getSelectionModel().getSelectedItem() != null){
            gameMode = mode.getSelectionModel().getSelectedItem();
        }
    }

    public void login(){
        GUIView view = new GUIView();
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("url", url.getText());
        connectionProperties.setProperty("port", port.getText());
        view.setupConnection(selectedConnection,username.getText(),password.getText(),connectionProperties,existingGame.isSelected(),gameMode);
        //TODO:prevent further input and display some kind of loading screen when it gets set to WAITING
    }

    public static void main(String[] args){
        launch();
    }
}
