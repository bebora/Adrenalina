package it.polimi.se2019.view.gui;

import it.polimi.se2019.Utils;
import it.polimi.se2019.view.Status;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Properties;

/**
 * A login screen used to start a new game.
 */
public class LoginScreen extends Application {
    @FXML ComboBox<String> connectionType;
    @FXML Button loginButton;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML TextField port;
    @FXML TextField url;
    @FXML CheckBox existingGame;
    @FXML ComboBox<String> mode;
    GUIView view;

    /**
     * The main Stage used by this application, it is saved here to be used by other classes.
     */
    private static Stage primaryStage;

    static Stage getPrimaryStage(){
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
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                concludeGame();
            }
        });
        primaryStage.setFullScreenExitHint("Press ESC to close the application");
    }


    /**
     * Creates a new GUIView and try to log in. If the log in doesn't
     * go as expected it shows a corresponding Alert to the user.
     */
    public void login(){
        view = new GUIView();
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("url", url.getText());
        connectionProperties.setProperty("port", port.getText());

        String connection = connectionType.getValue() != null ? connectionType.getValue() : connectionType.getPromptText();
        String gameMode = mode.getValue() != null ? mode.getValue() : mode.getPromptText();
        view.setGameMode(gameMode);
        view.setCredentials(username.getText(),password.getText());
        view.setConnectionProperties(connectionProperties,connection);
        if (!view.setupConnection(connection,username.getText(),password.getText(),connectionProperties,existingGame.isSelected(),gameMode)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login failed!!");
            alert.setContentText("Server is unreachable, try again later");
            alert.showAndWait();
            return;
        }

        while(view.getStatus() == null) {
            Utils.sleepABit(10);
        }
        if(view.getStatus() == Status.END){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login failed!!");
            alert.setContentText(view.getMessages().get(0));
            alert.showAndWait();
        }else if(view.getStatus() == Status.WAITING){
            new TextScreen(primaryStage, "Waiting to play...");
        }
    }

    /**
     * Closes the application.
     */
    private void concludeGame(){
        Platform.exit();
    }

    /**
     * Overrides the default stop method to close also non JavaFX related threads
     */
    @Override
    public void stop(){
        System.exit(0);
    }

    public static void main(String[] args){
        launch(args);
    }
}
