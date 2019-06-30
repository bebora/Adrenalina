package it.polimi.se2019.view.gui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ReconnectionScreen extends TextScreen {
    Stage stage;
    public ReconnectionScreen(Stage primaryStage, String message,GUIView view){
        super(primaryStage,message);
        stage = primaryStage;
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ENTER == event.getCode()) {
                reconnect(view);
            }
        });
    }

    private void reconnect(GUIView view){
        stage.removeEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ENTER == event.getCode()) {
                reconnect(view);
            }
        });
        view.reconnect();
    }
}
