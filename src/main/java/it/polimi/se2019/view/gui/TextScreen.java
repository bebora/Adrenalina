package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Screen used for displaying a message to the player
 * during loading
 */
public class TextScreen extends Pane {
    @FXML
    private Text text;

    @FXML
    private ProgressIndicator progress;

    /**
     * Creates a new TextScreen with the given message and
     * a set a new scene in the given stage
     * @param primaryStage used for displaying this screen
     * @param message the displayed message
     */
    TextScreen(Stage primaryStage,String message){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/LoadingScreen.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
        this.setStyle("-fx-background-color: white");
        text.setText(message);
        GuiHelper.resizeToScreenSize(this);
        Scene scene = new Scene(this);
        scene.setFill(Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
    }

    public void setMessage(String message) {
        this.text.setText(message);
    }
}
