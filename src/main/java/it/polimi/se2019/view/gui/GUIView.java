package it.polimi.se2019.view.gui;

import it.polimi.se2019.view.Status;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewWeapon;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GUIView extends View {
    BoardScreen boardScreen;

    private String usr;
    private String passwd;
    private Properties connectionProperties;
    private String connectionType;

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
        Scene scene = new Scene(boardScreen);
        scene.setFill(Color.BLACK);
        LoginScreen.getPrimaryStage().setScene(scene);
        LoginScreen.getPrimaryStage().setFullScreen(true);
        if(getGameMode().equals("DOMINATION"))
            boardScreen.hideKillshotTrack();
    }

    private void totalUpdate(){
        boardScreen.updateMessages(getMessages());
        boardScreen.updateBoard(getBoard(),getPlayers());
        boardScreen.updatePowerUps(getPowerUps());
        List<ViewWeapon> allWeapons = Stream.concat(getLoadedWeapons().stream(),getSelf().getUnloadedWeapons().stream()).collect(Collectors.toList());
        boardScreen.updateWeapons(allWeapons);
        boardScreen.setSelectableOptionsWrapper(getSelectableOptionsWrapper());
    }

    public void setCredentials(String username, String password){
        usr = username;
        passwd = password;
    }

    public void setConnectionProperties(Properties connectionProperties,String connectionType){
        this.connectionProperties = connectionProperties;
        this.connectionType = connectionType;
    }

    public void reconnect(){
        setupConnection(connectionType,usr,passwd,connectionProperties,true,getGameMode());
    }

    @Override
    public void disconnect() {
        if(getStatus() != Status.END){
            boardScreen = null;
            setStatus(Status.END);
            Platform.runLater(() -> new ReconnectionScreen(LoginScreen.getPrimaryStage(),"Press ENTER to reconnect!",this));
        }
    }

    @Override
    public void printWinners(List<String> winners) {
        if(getStatus() != Status.END) {
            boolean loser = !winners.contains(getSelf().getUsername());
            setStatus(Status.END);
            Platform.runLater(() -> new FinalScreen(loser, LoginScreen.getPrimaryStage(),winners));
        }
    }
}
