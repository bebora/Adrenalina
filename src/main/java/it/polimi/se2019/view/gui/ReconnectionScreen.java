package it.polimi.se2019.view.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * A screen displayed to the player in case
 * of lost connection.
 */
class ReconnectionScreen extends TextScreen {
    private Stage stage;

    /**
     * Creates a new ReconnectionScreen and displays it in the given stage,
     * with the given message. It also creates a new EventHandler to attempt
     * reconnection when the player press space bar.
     * @param primaryStage the Stage where this screen should be drawn
     * @param message the displayed message
     * @param view the view containing parameter and actual methods for reconnecting
     */
    ReconnectionScreen(Stage primaryStage, String message,GUIView view){
        super(primaryStage,message);
        stage = primaryStage;
        EventHandler<KeyEvent> handleEnter = new EventHandler<>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (KeyCode.ENTER == keyEvent.getCode()) {
                    if (!view.reconnect()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Login failed!!");
                        alert.setContentText("Server is unreachable, try again later");
                        alert.showAndWait();
                    } else {
                        stage.removeEventHandler(KeyEvent.KEY_RELEASED, this);
                    }
                }
            }
        };
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, handleEnter);
    }
}
