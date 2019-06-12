package it.polimi.se2019.view.gui;

import it.polimi.se2019.view.View;
import javafx.application.Platform;
import javafx.scene.Scene;

public class GUIView extends View {
    BoardScreen boardScreen;

    @Override
    public synchronized void refresh() {
        if(boardScreen == null){
            boardScreen = new BoardScreen(this);
            Platform.runLater(()->changeStage());
        }
        else
            Platform.runLater(()->
                    boardScreen.updateBoard(getBoard()));
    }

    public BoardScreen getBoardScreen() {
        return boardScreen;
    }

    private void changeStage(){
        LoginScreen.getPrimaryStage().setScene(new Scene(boardScreen));
        LoginScreen.getPrimaryStage().setFullScreen(true);
    }
}
