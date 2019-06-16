package it.polimi.se2019.view.gui;

import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewWeapon;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GUIView extends View {
    BoardScreen boardScreen;

    @Override
    public synchronized void refresh() {
        if (getPlayers()!= null && getSelf() != null && !getPlayers().isEmpty()) {
            if (boardScreen == null) {
                boardScreen = new BoardScreen(this);
                Platform.runLater(() -> changeStage());
            } else
                Platform.runLater(() -> totalUpdate());
        }
    }

    public BoardScreen getBoardScreen() {
        return boardScreen;
    }

    private void changeStage(){
        LoginScreen.getPrimaryStage().setScene(new Scene(boardScreen));
        LoginScreen.getPrimaryStage().setFullScreen(true);
    }

    private void totalUpdate(){
        boardScreen.updateBoard(getBoard(),getPlayers());
        boardScreen.updatePowerUps(getPowerUps());
        List<ViewWeapon> allWeapons = Stream.concat(getLoadedWeapons().stream(),getSelf().getUnloadedWeapons().stream()).collect(Collectors.toList());
        boardScreen.updateWeapons(allWeapons);
        boardScreen.setSelectableOptionsWrapper(getSelectableOptionsWrapper());
    }
}
