package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
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

/**
 * Extends the view to provide the proper methods for
 * drawing communication with the rest of the GUI.
 */
public class GUIView extends View {
    private BoardScreen boardScreen;
    private String usr;
    private String passwd;
    private Properties connectionProperties;
    private String connectionType;

    /**
     * Signals to the rest of the GUI that there is an update
     * and the displayed elements should be updated.
     * Il also create the BoardScreen at the first update the view receives.
     */
    @Override
    public synchronized void refresh() {
        if (getPlayers()!= null && getSelf() != null && !getPlayers().isEmpty() && !getStatus().equals(Status.END)) {
            if (boardScreen == null) {
                boardScreen = new BoardScreen(this);
                Platform.runLater(()->changeStage());
            } else
                Platform.runLater(()->totalUpdate());
        }
    }

    /**
     * Gets the main stage from the LoginScreen and replace the scene
     * with a new one created from the boardScreen
     */
    private void changeStage(){
        Scene scene = new Scene(boardScreen);
        scene.setFill(Color.BLACK);
        LoginScreen.getPrimaryStage().setScene(scene);
        LoginScreen.getPrimaryStage().setFullScreen(true);
    }

    /**
     * Updates every element of the boardScreen after the view
     * received a totalUpdate.
     */
    private void totalUpdate(){
        boardScreen.updateMessages(getMessages());
        boardScreen.updateBoard(getBoard(),getPlayers());
        boardScreen.updatePowerUps(getPowerUps());
        boardScreen.updatePoints(getPoints());
        List<ViewWeapon> allWeapons = Stream.concat(getLoadedWeapons().stream(),getSelf().getUnloadedWeapons().stream()).collect(Collectors.toList());
        boardScreen.updateWeapons(allWeapons);
        boardScreen.setSelectableOptionsWrapper(getSelectableOptionsWrapper());
    }

    /**
     * Sets username and password to be used for a reconnecting
     * without closing the client
     * @param username username used for logging in
     * @param password password used for logging in
     */
    void setCredentials(String username, String password){
        usr = username;
        passwd = password;
    }

    /**
     * Sets the connection type and the ConnectionProperties
     * used for establishing a connection at login
     * @param connectionProperties properties containing address and port used at log in
     * @param connectionType connection type chosen at login between rmi and socket
     */
    void setConnectionProperties(Properties connectionProperties,String connectionType){
        this.connectionProperties = connectionProperties;
        this.connectionType = connectionType;
    }

    /**
     * Attempt reconnection after the client has lost
     * connection with the server.
     * @return <code>true</code> if the reconnection was successful
     *         <code>false</code> otherwise
     */
    boolean reconnect(){
        GUIView reconnectedView = new GUIView();
        reconnectedView.setCredentials(usr,passwd);
        reconnectedView.setConnectionProperties(connectionProperties,connectionType);
        reconnectedView.setGameMode(getGameMode());
        if(reconnectedView.setupConnection(connectionType,usr,passwd,connectionProperties,true,getGameMode())) {
            while (reconnectedView.getStatus() == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logger.log(Priority.DEBUG, "Interrupted for " + e.getMessage());
                }
            }
            return true;
        }else
            return false;
    }

    /**
     * Change the status of the view after a disconnection is noticed.
     * It creates a new ReconnectionScreen from which the user can reconnect to the server
     * with the parameters used at login.
     */
    @Override
    public void disconnect() {
        if(getStatus() != Status.END){
            boardScreen = null;
            setStatus(Status.END);
            Platform.runLater(() -> new ReconnectionScreen(LoginScreen.getPrimaryStage(),"Press ENTER to reconnect!",this));
        }
    }

    /**
     * Creates a new FinalScreen and check if the user has won or lost.
     * @param winners the players that won the game
     */
    @Override
    public void printWinners(List<String> winners) {
        if(getStatus() != Status.END) {
            boolean loser = !winners.contains(getSelf().getUsername());
            setStatus(Status.END);
            Platform.runLater(() -> new FinalScreen(loser, LoginScreen.getPrimaryStage(),winners));
        }
    }
}
