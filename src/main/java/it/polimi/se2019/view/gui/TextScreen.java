package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class TextScreen extends AnchorPane {
    @FXML
    Text text;

    @FXML
    ProgressIndicator progress;

    public TextScreen(Stage primaryStage,String message){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/LoadingScreen.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
        text.setText(message);
        Scene scene = new Scene(this);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
    }

    public void setMessage(String message) {
        this.text.setText(message);
    }
}
