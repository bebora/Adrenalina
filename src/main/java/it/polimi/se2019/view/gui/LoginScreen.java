package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginScreen extends Application {
    @FXML ListView connectionType;
    @FXML Button loginButton;
    @FXML TextField username;
    @FXML PasswordField password;
    private String selectedConnection;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/LoginScreen.fxml"));
        Scene scene = new Scene(root,400,400);
        stage.setTitle("LoginScreen");
        stage.setScene(scene);
        stage.show();
    }

    public void selectConnection(){
        selectedConnection = (String)connectionType.getSelectionModel().getSelectedItem();
    }

    public void login(){
        Stage stage = (Stage)loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/BoardScreen.fxml"));

        try {
            Parent root = loader.load();
            Scene scene = new Scene(root, 1024, 576);
            BoardScreen boardScreen = loader.getController();
            Properties connectionProperties = new Properties();
            FileInputStream fin;
            try{
                fin = new FileInputStream(getClass().getClassLoader().getResource("connection.properties").getPath());
                connectionProperties.load(fin);
            }catch (Exception e){
                Logger.log(Priority.ERROR,e.getMessage());
            }
            boardScreen.setupConnection(selectedConnection,username.getText(),password.getText(),connectionProperties);
            stage.setScene(scene);
        }catch (IOException e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }
    }

    public static void main(String[] args){
        launch();
    }


}
