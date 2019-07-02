package it.polimi.se2019.view.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

class ReconnectionScreen extends TextScreen {
    private Stage stage;

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
