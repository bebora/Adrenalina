package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.Status;
import it.polimi.se2019.view.View;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginScreen extends Application {
    @FXML ListView connectionType;
    @FXML Button loginButton;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML CheckBox existingGame;
    @FXML ComboBox<String> mode;
    private String gameMode = "NORMAL";
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

    @FXML
    public void selectMode(){
        if(mode.getSelectionModel().getSelectedItem() != null){
            gameMode = mode.getSelectionModel().getSelectedItem();
        }
    }

    public void login(){
        View view = new View();
        Properties connectionProperties = new Properties();
        FileInputStream fin;
        try{
            fin = new FileInputStream(getClass().getClassLoader().getResource("connection.properties").getPath());
            connectionProperties.load(fin);
        }catch (Exception e){
            Logger.log(Priority.ERROR,e.getMessage());
        }
        view.setupConnection(selectedConnection,username.getText(),password.getText(),connectionProperties,existingGame.isSelected(),gameMode);
        while(view.getStatus() != Status.PLAYING){
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.DEBUG, "Interrupted for " + e.getMessage());
            }
        }
        Stage stage = (Stage)loginButton.getScene().getWindow();

    }

    public static void main(String[] args){
        launch();
    }
}
